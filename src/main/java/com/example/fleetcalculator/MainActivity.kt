package com.example.fleetcalculator

import android.content.Intent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cek login state di SharedPreferences
        val pref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val telegramId = pref.getString("telegramId", null)
        val nama = pref.getString("nama", null)

        // Jika user belum login, lempar ke login
        if (telegramId.isNullOrEmpty() || nama.isNullOrEmpty()) {
            val loginIntent = Intent(this, TelegramLoginWebViewActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            finish()
            return
        }

        // --- Jika sampai sini, user sudah login ---
        val btnLoader = findViewById<ImageButton>(R.id.btn_loader)
        val btnHauler = findViewById<ImageButton>(R.id.btn_hauler)
        val btnJumlahHauler = findViewById<ImageButton>(R.id.btn_jumlah_hauler)
        val btnMatchingFactor = findViewById<ImageButton>(R.id.btn_matching_factor)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile) // Tambahkan tombol profile

        btnLoader.setOnClickListener {
            startActivity(Intent(this, LoaderActivity::class.java))
        }
        btnHauler.setOnClickListener {
            startActivity(Intent(this, Hauler::class.java))
        }
        btnJumlahHauler.setOnClickListener {
            startActivity(Intent(this, Jumlah_Hauler::class.java))
        }
        btnMatchingFactor.setOnClickListener {
            startActivity(Intent(this, Matching_Factor::class.java))
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}