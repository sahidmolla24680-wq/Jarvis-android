package com.sahidmolla.jarvis

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import java.util.Locale

class JarvisService : Service(), TextToSpeech.OnInitListener {

    private var serviceDestroyed = false

    private var recognizer: SpeechRecognizer? = null
    private lateinit var tts: TextToSpeech

    private val channelId = "jarvis_channel"

    override fun onCreate() {
        super.onCreate()

        tts = TextToSpeech(this, this)

        createNotificationChannel()

        val notification = NotificationCompat.Builder(
            this,
            channelId
        )
            .setContentTitle("Jarvis চালু আছে")
            .setContentText("“জার্ভিস” বললে সাড়া দেবে")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()

        startForeground(1001, notification)

        startListening()
    }

    override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {

        val result = tts.setLanguage(Locale("bn", "IN"))

        if (result != TextToSpeech.LANG_MISSING_DATA &&
            result != TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            tts.setSpeechRate(0.90f)
            tts.setPitch(0.95f)
        }
    }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Jarvis Background Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager =
            getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(channel)
    }

    private fun startListening() {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            speak("বস, Speech Recognition পাওয়া যাচ্ছে না।")
            return
        }

        recognizer?.destroy()

        recognizer =
            SpeechRecognizer.createSpeechRecognizer(this)

        recognizer?.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(
                    params: Bundle?
                ) {
                }

                override fun onBeginningOfSpeech() {
                }

                override fun onRmsChanged(
                    rmsdB: Float
                ) {
                }

                override fun onBufferReceived(
                    buffer: ByteArray?
                ) {
                }

                override fun onEndOfSpeech() {
                }

                override fun onError(
                    error: Int
                ) {
                    restartListening()
                }

                override fun onResults(
                    results: Bundle?
                ) {

                    val matches =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    val text =
                        matches?.firstOrNull() ?: ""

                    val command =
                        text.lowercase(Locale.getDefault())

                    if (
                        command.contains("জার্ভিস") ||
                        command.contains("jarvis")
                    ) {
                        speak("জি বস!")
                    }

                    restartListening()
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {
                }

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {
                }
            }
        )

        val intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "bn-IN"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            false
        )

        try {
            recognizer?.startListening(intent)
        } catch (e: Exception) {
            restartListening()
        }
    }

    private fun restartListening() {

        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (!serviceDestroyed) {
                    startListening()
                }
            },
            1000
        )
    }

    private fun speak(text: String) {

        if (::tts.isInitialized) {
            tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "jarvis_reply"
            )
        }
    }

    override fun onDestroy() {

        serviceDestroyed = true

        recognizer?.destroy()

        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null
}
