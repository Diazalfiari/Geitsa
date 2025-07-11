package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("from_splash", true)
            startActivity(mainIntent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 3000)
    }
}


