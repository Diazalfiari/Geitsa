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
            // Cek status login di SharedPreferences
            val sharedPref = getSharedPreferences("login_prefs", MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            val nextIntent = if (isLoggedIn) {
                Intent(this, MainActivity::class.java).apply {
                    putExtra("from_splash", true)
                }
            } else {
                Intent(this, LoginActivity::class.java)
            }

            startActivity(nextIntent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 3000)
    }
}