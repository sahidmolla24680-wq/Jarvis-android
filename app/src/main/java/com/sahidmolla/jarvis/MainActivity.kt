package com.sahidmolla.jarvis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(40, 60, 40, 40)

        statusText = TextView(this)
        statusText.text = "🤖 Jarvis প্রস্তুত\n\nনিচের বোতাম চাপুন এবং কথা বলুন।"
        statusText.textSize = 20f

        val listenButton = Button(this)
        listenButton.text = "🎤 Jarvis-এর সাথে কথা বলুন"

        layout.addView(statusText)
        layout.addView(listenButton)

        setContentView(layout)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }

        listenButton.setOnClickListener {
            startListening()
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale("bn", "IN")
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "বলুন, বস..."
        )

        try {
            startActivityForResult(intent, 101)
        } catch (e: Exception) {
            statusText.text = "Speech recognition পাওয়া যাচ্ছে না।"
        }
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == RESULT_OK) {

            val results = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            val command = results?.firstOrNull() ?: ""

            statusText.text = "আপনি বলেছেন:\n$command"

            handleCommand(command.lowercase(Locale.getDefault()))
        }
    }

    private fun handleCommand(command: String) {

        when {
            command.contains("whatsapp") ||
            command.contains("হোয়াটসঅ্যাপ") -> {

                val intent = packageManager.getLaunchIntentForPackage(
                    "com.whatsapp"
                )

                if (intent != null) {
                    startActivity(intent)
                } else {
                    statusText.text = "WhatsApp ইনস্টল করা নেই।"
                }
            }

            command.contains("youtube") ||
            command.contains("ইউটিউব") -> {

                val intent = packageManager.getLaunchIntentForPackage(
                    "com.google.android.youtube"
                )

                if (intent != null) {
                    startActivity(intent)
                } else {
                    statusText.text = "YouTube পাওয়া যাচ্ছে না।"
                }
            }

            command.contains("chrome") ||
            command.contains("ক্রোম") -> {

                val intent = packageManager.getLaunchIntentForPackage(
                    "com.android.chrome"
                )

                if (intent != null) {
                    startActivity(intent)
                } else {
                    statusText.text = "Chrome পাওয়া যাচ্ছে না।"
                }
            }

            else -> {
                statusText.text =
                    "জি বস 😎\nআমি শুনেছি:\n$command\n\nএই command-এর কাজ এখনও যোগ করা হয়নি।"
            }
        }
    }
}
