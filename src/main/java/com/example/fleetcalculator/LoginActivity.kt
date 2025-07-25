package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnTelegramLogin = findViewById<Button>(R.id.btnTelegramLogin)
        btnTelegramLogin.setOnClickListener {
            // Buka WebView (atau custom tab) ke website login Telegram
            val intent = Intent(this, TelegramLoginWebViewActivity::class.java)
            startActivity(intent)
        }
    }
}