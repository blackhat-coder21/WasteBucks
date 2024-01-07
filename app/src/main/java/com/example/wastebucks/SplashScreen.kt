package com.example.wastebucks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.schedule({
            Handler(Looper.getMainLooper()).post {
                startActivity(Intent(this, OnboardingActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }, 3, TimeUnit.SECONDS)

    }
}