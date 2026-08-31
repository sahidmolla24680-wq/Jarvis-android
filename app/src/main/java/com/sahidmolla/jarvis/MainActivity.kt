package com.sahidmolla.jarvis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    companion object {
        private const val AUDIO_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)

        requestAudioPermission()

        val startButton = findViewById<Button>(R.id.startButton)

        startButton.setOnClickListener {
            startJarvis()
        }
    }

    private fun requestAudioPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                AUDIO_PERMISSION_CODE
            )
        }
    }

    private fun startJarvis() {

        val serviceIntent =
            Intent(this, JarvisService::class.java)

        ContextCompat.startForegroundService(
            this,
            serviceIntent
        )

        statusText.text =
            "জি বস! 😎\n\nJarvis চালু হয়েছে।"
    }
}
