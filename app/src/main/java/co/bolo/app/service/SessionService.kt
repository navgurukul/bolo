package co.bolo.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import co.bolo.app.analysis.DictionaryClassifier
import co.bolo.app.analysis.TranscriptAnalyzer
import co.bolo.app.data.repo.SessionManager
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SessionService : Service() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var transcriptAnalyzer: TranscriptAnalyzer
    @Inject lateinit var dictionaryClassifier: DictionaryClassifier

    private val binder = LocalBinder()
    private var speechRecognizer: SpeechRecognizer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private var isListening = false
    private var isRecording = false
    private var currentBusyDelay = INITIAL_ERROR_DELAY_MS
    private var usingOnDeviceRecognizer = false
    private var fellBackToStandard = false
    private var consecutiveErrors = 0

    inner class LocalBinder : Binder() {
        fun getService(): SessionService = this@SessionService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        dictionaryClassifier.initialize(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        isRecording = true
        initSpeechRecognizer()
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: android.os.Bundle?) {
            Log.d("SessionService", "Ready for speech (onDevice=$usingOnDeviceRecognizer)")
            isListening = true
            currentBusyDelay = INITIAL_ERROR_DELAY_MS
        }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            Log.e("SessionService", "Speech recognition error: $error (onDevice=$usingOnDeviceRecognizer)")
            isListening = false
            if (!isRecording) return

            val unrecoverable = error == SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED ||
                error == SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE ||
                error == SpeechRecognizer.ERROR_CLIENT
            val tooManyTimeouts = (error == SpeechRecognizer.ERROR_NO_MATCH ||
                error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) && consecutiveErrors >= 5

            if (usingOnDeviceRecognizer && !fellBackToStandard && (unrecoverable || tooManyTimeouts)) {
                Log.w("SessionService", "On-device recognizer failing — falling back to standard network recognizer")
                fellBackToStandard = true
                consecutiveErrors = 0
                serviceScope.launch { fallbackToStandardRecognizer() }
                return
            }

            consecutiveErrors++

            val delay = when (error) {
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> MIN_RESTART_DELAY_MS
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                    val d = currentBusyDelay
                    currentBusyDelay = (currentBusyDelay * 2).coerceAtMost(MAX_ERROR_DELAY_MS)
                    d
                }
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
            consecutiveErrors = 0
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                Log.d("SessionService", "Got transcript chunk: ${matches[0]}")
                processTranscriptChunk(matches[0])
            }
            if (isRecording) {
                serviceScope.launch {
                    delay(ON_RESULTS_RESTART_DELAY_MS)
                    restartListening()
                }
            }
        }

        override fun onPartialResults(partialResults: android.os.Bundle?) {}
        override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
    }

    private fun initSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("SessionService", "Speech recognition not available")
            stopSelf()
            return
        }
        usingOnDeviceRecognizer = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            SpeechRecognizer.isOnDeviceRecognitionAvailable(this)
        Log.d("SessionService", "Creating recognizer (onDevice=$usingOnDeviceRecognizer)")
        speechRecognizer = if (usingOnDeviceRecognizer)
            SpeechRecognizer.createOnDeviceSpeechRecognizer(this)
        else
            SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(recognitionListener)
        startListening()
    }

    private fun fallbackToStandardRecognizer() {
        try { speechRecognizer?.destroy() } catch (_: Exception) {}
        usingOnDeviceRecognizer = false
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(recognitionListener)
        isListening = false
        startListening()
    }

    private fun startListening() {
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
        if (!isRecording) return
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
        return START_STICKY
    }

    override fun onDestroy() {
        isRecording = false
        isListening = false
        sessionManager.setRecording(false)
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SessionService", "Error destroying speech recognizer", e)
        }
        serviceScope.launch {
            delay(100)
            serviceScope.coroutineContext[Job]?.cancel()
        }
        super.onDestroy()
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
        private const val MAX_ERROR_DELAY_MS = 4000L
    }
}
