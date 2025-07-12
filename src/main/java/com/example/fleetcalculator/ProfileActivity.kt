package com.example.fleetcalculator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvNamaUser = findViewById<TextView>(R.id.tvNamaUser)
        val tvTelegramId = findViewById<TextView>(R.id.tvTelegramId)
        val btnTelegram = findViewById<Button>(R.id.btnTelegram)
        val btnLogout = findViewById<Button>(R.id.btnLogout) // Tambahkan ini!

        // Ambil data user dari SharedPreferences (ambil nama, bukan username/email)
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        tvNamaUser.text = prefs.getString("nama", "User")
        tvTelegramId.text = prefs.getString("telegramId", "-")

        btnTelegram.setOnClickListener {
            val botUsername = "MasterFleet01_bot"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$botUsername"))
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            // Hapus semua data user (logout total)
            prefs.edit().clear().apply()

            // Arahkan ke halaman login, hapus semua history activity
            val loginIntent = Intent(this, TelegramLoginWebViewActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            finish()
        }
    }
}