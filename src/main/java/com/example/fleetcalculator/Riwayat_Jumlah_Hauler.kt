package com.example.fleetcalculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Riwayat_Jumlah_Hauler : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_jumlah_hauler)

        val tvRiwayatContent = findViewById<TextView>(R.id.tvRiwayatContent)
        val btnReset = findViewById<Button>(R.id.btnResetRiwayat)

        val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
        val riwayat = sharedPref.getString("riwayat_jumlah_hauler", null)

        if (riwayat.isNullOrBlank()) {
            tvRiwayatContent.text = "Belum ada riwayat yang ditampilkan."
        } else {
            tvRiwayatContent.text = riwayat
        }

        btnReset.setOnClickListener {
            val editor = sharedPref.edit()
            editor.remove("riwayat_jumlah_hauler")
            editor.apply()

            tvRiwayatContent.text = "Belum ada riwayat yang ditampilkan."
            Toast.makeText(this, "Riwayat berhasil dihapus.", Toast.LENGTH_SHORT).show()
        }
    }
}
