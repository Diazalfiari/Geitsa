package com.example.fleetcalculator

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class InputNamaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_nama)

        // Ambil data dari intent extra
        val telegramId = intent.getStringExtra("telegramId") ?: ""
        val telegramName = intent.getStringExtra("telegramName") ?: ""

        val etNama = findViewById<EditText>(R.id.etNama)
        val btnLanjut = findViewById<Button>(R.id.btnLanjut)

        // Pre-fill nama jika ada dari Telegram
        if (telegramName.isNotEmpty()) {
            etNama.setText(telegramName)
        }

        btnLanjut.setOnClickListener {
            val nama = etNama.text.toString().trim()
            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan ke SharedPreferences
            val pref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            pref.edit()
                .putString("telegramId", telegramId)
                .putString("nama", nama)
                .apply()

            // Lanjut ke halaman utama, hapus semua activity sebelumnya
            val intentMain = Intent(this, MainActivity::class.java)
            intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentMain.putExtra("telegramId", telegramId)
            intentMain.putExtra("nama", nama)
            startActivity(intentMain)
            // Tidak perlu finish(), sudah di-clear task
        }
    }
}