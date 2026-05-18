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

    inner class LocalBinder : Binder() {
        fun getService(): SessionService = this@SessionService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        dictionaryClassifier.initialize(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        initSpeechRecognizer()
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: android.os.Bundle?) {
                        Log.d("SessionService", "Ready for speech")
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        Log.e("SessionService", "Speech recognition error: $error")
                        serviceScope.launch {
                            delay(500)
                            startListening()
                        }
                    }

                    override fun onResults(results: android.os.Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            processTranscriptChunk(matches[0])
                        }
                        startListening()
                    }

                    override fun onPartialResults(partialResults: android.os.Bundle?) { }

                    override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
                })
            }
            startListening()
        } else {
            Log.e("SessionService", "Speech recognition not available")
            stopSelf()
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer?.startListening(intent)
    }

    private fun processTranscriptChunk(chunk: String) {
        serviceScope.launch {
            val analysis = transcriptAnalyzer.analyze(chunk)
            sessionManager.addChunk(analysis)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sessionManager.setRecording(true)
        return START_STICKY
    }

    override fun onDestroy() {
        sessionManager.setRecording(false)
        speechRecognizer?.destroy()
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
    }
}
