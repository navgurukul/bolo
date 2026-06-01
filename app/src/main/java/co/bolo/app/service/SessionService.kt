package co.bolo.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import co.bolo.app.analysis.DictionaryClassifier
import co.bolo.app.analysis.TranscriptAnalyzer
import co.bolo.app.data.repo.RecognizerStatus
import co.bolo.app.data.repo.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * Bolo uses the system network speech recognizer (Google Speech Services on
 * almost every device). The recognizer runs in a separate process, takes the
 * mic stream from us, ships audio to Google's backend, and returns text via
 * onResults. Bolo keeps only the transcript text — no raw audio is ever
 * written to disk on this device.
 */
@AndroidEntryPoint
class SessionService : Service() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var transcriptAnalyzer: TranscriptAnalyzer
    @Inject lateinit var dictionaryClassifier: DictionaryClassifier

    private val binder = LocalBinder()
    private var speechRecognizer: SpeechRecognizer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private var isListening = false
    @Volatile private var isRecording = false
    @Volatile private var isPaused = false
    private var currentBusyDelay = INITIAL_ERROR_DELAY_MS
    private var consecutiveBusyErrors = 0
    private var consecutiveNetworkErrors = 0

    private var savedMusicVolume: Int = -1

    inner class LocalBinder : Binder() {
        fun getService(): SessionService = this@SessionService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        dictionaryClassifier.initialize(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        try {
            muteRecognizerBeep()
            isRecording = true
            initSpeechRecognizer()
            observePauseState()
        } catch (e: Exception) {
            Log.e("SessionService", "onCreate failed — restoring state", e)
            restoreRecognizerBeep()
            stopSelf()
        }
    }

    private fun observePauseState() {
        // Skip the initial value (false) — onCreate already started listening.
        serviceScope.launch {
            sessionManager.paused.drop(1).collect { paused ->
                if (paused) handlePauseRequested() else handleResumeRequested()
            }
        }
    }

    private fun handlePauseRequested() {
        Log.d("SessionService", "Pausing recognizer")
        isPaused = true
        try {
            // cancel() drops the current recognition session — any in-flight
            // chunk is discarded so speech during the pause window doesn't
            // get attributed to the active speaker after the user resumes.
            speechRecognizer?.cancel()
        } catch (e: Exception) {
            Log.e("SessionService", "Error cancelling on pause", e)
        }
        isListening = false
    }

    private fun handleResumeRequested() {
        Log.d("SessionService", "Resuming recognizer")
        isPaused = false
        startListening()
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: android.os.Bundle?) {
            Log.d("SessionService", "Ready for speech")
            isListening = true
            currentBusyDelay = INITIAL_ERROR_DELAY_MS
        }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            Log.e("SessionService", "Speech recognition error: $error")
            isListening = false
            if (!isRecording || speechRecognizer == null) return

            // Track + surface persistent failures so the user sees a banner
            // instead of a silently 0% session.
            when (error) {
                SpeechRecognizer.ERROR_NETWORK,
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
                SpeechRecognizer.ERROR_SERVER -> {
                    consecutiveNetworkErrors++
                    val status = if (error == SpeechRecognizer.ERROR_SERVER)
                        RecognizerStatus.ServerError else RecognizerStatus.NoInternet
                    sessionManager.setRecognizerStatus(status)
                }
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                    consecutiveBusyErrors++
                    if (consecutiveBusyErrors >= MAX_CONSECUTIVE_BUSY) {
                        sessionManager.setRecognizerStatus(RecognizerStatus.Overloaded)
                    }
                }
                else -> {
                    consecutiveBusyErrors = 0
                }
            }

            if (isPaused) return

            val delay = when (error) {
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> MIN_RESTART_DELAY_MS
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                    val d = currentBusyDelay
                    currentBusyDelay = (currentBusyDelay * 2).coerceAtMost(MAX_ERROR_DELAY_MS)
                    d
                }
                SpeechRecognizer.ERROR_NETWORK,
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
                SpeechRecognizer.ERROR_SERVER -> NETWORK_RETRY_DELAY_MS
                else -> INITIAL_ERROR_DELAY_MS
            }
            if (error != SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                currentBusyDelay = INITIAL_ERROR_DELAY_MS
            }
            serviceScope.launch {
                delay(delay)
                restartListening()
            }
        }

        override fun onResults(results: android.os.Bundle?) {
            isListening = false
            if (!isRecording || speechRecognizer == null) return
            // A successful result means network + recognizer are healthy.
            consecutiveNetworkErrors = 0
            consecutiveBusyErrors = 0
            if (sessionManager.recognizerStatus.value != RecognizerStatus.Ok) {
                sessionManager.setRecognizerStatus(RecognizerStatus.Ok)
            }
            if (isPaused) return
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                Log.d("SessionService", "Got transcript chunk: ${matches[0]}")
                processTranscriptChunk(matches[0])
            }
            serviceScope.launch {
                delay(ON_RESULTS_RESTART_DELAY_MS)
                restartListening()
            }
        }

        override fun onPartialResults(partialResults: android.os.Bundle?) {}
        override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
    }

    private fun initSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("SessionService", "Speech recognition unavailable on this device — stopping")
            stopSelf()
            return
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(recognitionListener)
        startListening()
    }

    private fun startListening() {
        if (!isRecording || isPaused || speechRecognizer == null) return
        if (isListening) {
            Log.w("SessionService", "startListening called but already listening")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            // BCP-47 (hyphen). Locale.toString gives "en_IN" with underscore which some
            // OEM recognizers reject — toLanguageTag emits "en-IN".
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.ENGLISH.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

            // Adjust silence parameters to reduce frequency of restarts during brief pauses
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
        }
        try {
            isListening = true
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("SessionService", "Failed to start listening", e)
            isListening = false
        }
    }

    private fun restartListening() {
        if (!isRecording || isPaused || speechRecognizer == null) return
        try {
            speechRecognizer?.cancel()
        } catch (e: Exception) {
            Log.e("SessionService", "Error calling cancel() on speech recognizer", e)
        }
        isListening = false
        startListening()
    }

    private fun processTranscriptChunk(chunk: String) {
        serviceScope.launch {
            val analysis = transcriptAnalyzer.analyze(chunk)
            sessionManager.addChunk(analysis)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRecording = true
        sessionManager.setRecording(true)
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("SessionService", "Task removed — stopping service")
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        isRecording = false
        isListening = false
        isPaused = false
        sessionManager.setRecording(false)
        val recognizer = speechRecognizer
        speechRecognizer = null
        try {
            recognizer?.cancel()
            recognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SessionService", "Error destroying speech recognizer", e)
        }
        restoreRecognizerBeep()
        try {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            Log.e("SessionService", "Error stopping foreground", e)
        }
        serviceScope.coroutineContext[Job]?.cancel()
        super.onDestroy()
    }

    private fun muteRecognizerBeep() {
        val am = getSystemService(AUDIO_SERVICE) as? AudioManager ?: return
        savedMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        try {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        } catch (e: Exception) {
            Log.w("SessionService", "Could not mute STREAM_MUSIC to suppress recognizer beep", e)
        }
    }

    private fun restoreRecognizerBeep() {
        if (savedMusicVolume < 0) return
        val am = getSystemService(AUDIO_SERVICE) as? AudioManager ?: return
        try {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, savedMusicVolume, 0)
        } catch (e: Exception) {
            Log.w("SessionService", "Could not restore STREAM_MUSIC volume", e)
        }
        savedMusicVolume = -1
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Active Session",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Bolo Session Active")
            .setContentText("Listening and analyzing English usage...")
            .setSmallIcon(android.R.drawable.presence_audio_busy)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "session_channel"
        private const val NOTIFICATION_ID = 1

        private const val MIN_RESTART_DELAY_MS = 50L
        private const val ON_RESULTS_RESTART_DELAY_MS = 150L
        private const val INITIAL_ERROR_DELAY_MS = 500L
        private const val MAX_ERROR_DELAY_MS = 15_000L
        private const val NETWORK_RETRY_DELAY_MS = 3_000L
        private const val MAX_CONSECUTIVE_BUSY = 8
    }
}
