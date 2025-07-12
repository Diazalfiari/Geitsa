package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import com.example.fleetcalculator.utils.TelegramBotSender

class Hauler : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hauler)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnRiwayat = findViewById<ImageButton>(R.id.btnRiwayat)
        val btnGenerateTravelTime = findViewById<Button>(R.id.btnGenerateTravelTime)
        val btnHitung = findViewById<Button>(R.id.btnHitungHauler)

        val edtVesselCapacity = findViewById<EditText>(R.id.edtVesselCapacity)
        val edtJarak = findViewById<EditText>(R.id.edtJarak)
        val edtKecepatan = findViewById<EditText>(R.id.edtKecepatan)
        val edtTravelTime = findViewById<EditText>(R.id.edtTravelTime)
        val edtSwellFactor = findViewById<EditText>(R.id.edtSwellFactor)
        val edtEfisiensiKerja = findViewById<EditText>(R.id.edtEfisiensiKerja)
        val txtResult = findViewById<TextView>(R.id.txtResult)

        val black = ContextCompat.getColor(this, android.R.color.black)

        edtVesselCapacity.setTextColor(black)
        edtJarak.setTextColor(black)
        edtKecepatan.setTextColor(black)
        edtTravelTime.setTextColor(black)
        edtSwellFactor.setTextColor(black)
        edtEfisiensiKerja.setTextColor(black)
        txtResult.setTextColor(black)

        btnBack.setOnClickListener { finish() }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Hauler::class.java)
            startActivity(intent)
        }

        btnGenerateTravelTime.setOnClickListener {
            val jarak = edtJarak.text.toString().toDoubleOrNull()
            val kecepatan = edtKecepatan.text.toString().toDoubleOrNull()
            if (jarak == null || kecepatan == null || kecepatan == 0.0) {
                txtResult.text = "Mohon isi jarak dan kecepatan dengan benar."
                return@setOnClickListener
            }
            val kecepatanMeterPerMenit = (kecepatan * 1000) / 60.0
            val travelTime = (jarak * 2) / kecepatanMeterPerMenit
            edtTravelTime.setText("%.2f".format(travelTime))
            txtResult.text = "Travel Time Pulang-Pergi: %.2f menit".format(travelTime)
        }

        btnHitung.setOnClickListener {
            val c = edtVesselCapacity.text.toString().toDoubleOrNull()
            val jarak = edtJarak.text.toString().toDoubleOrNull()
            val kecepatan = edtKecepatan.text.toString().toDoubleOrNull()
            val sf = edtSwellFactor.text.toString().toDoubleOrNull()
            val et = edtEfisiensiKerja.text.toString().toDoubleOrNull()

            if (c == null || jarak == null || kecepatan == null || sf == null || et == null || kecepatan == 0.0) {
                txtResult.text = "Mohon isi semua field dan generate Travel Time terlebih dahulu."
                return@setOnClickListener
            }

            val kecepatanMeterPerMenit = (kecepatan * 1000) / 60.0
            val travelTime = (jarak * 2) / kecepatanMeterPerMenit
            val cmt = 4 + travelTime
            val q = (c * 60) / (cmt * sf) * et

            val resultText = """
                Travel Time (Pulang-Pergi): %.2f menit
                Cycle Time: %.2f menit
                Produktivitas: %.2f m³/jam
            """.trimIndent().format(travelTime, cmt, q)
            txtResult.text = resultText

            val timestamp = getCurrentTimestamp()
            val formattedRiwayat = """
                🕒 $timestamp
                Vessel Capacity: $c m³
                Jarak: $jarak m
                Kecepatan: $kecepatan km/h
                Travel Time: %.2f menit
                Cycle Time: %.2f menit
                Swell Factor: $sf
                Efisiensi Kerja: $et
                Produktivitas: %.2f m³/jam
                ------------------------------------
            """.trimIndent().format(travelTime, cmt, q)

            // Ambil telegramId user dari SharedPreferences
            val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val telegramId = userPrefs.getString("telegramId", null)

            if (telegramId != null) {
                // Kirim ke Telegram user (akun pribadi)
                TelegramBotSender.sendToTelegram(telegramId, formattedRiwayat)
            } else {
                Toast.makeText(this, "Gagal mengirim ke Telegram: ID user tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }

            // Simpan ke Riwayat
            val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
            val existingData = sharedPref.getString("riwayat_hauler", "") ?: ""
            val newData = "$formattedRiwayat\n$existingData"
            sharedPref.edit().putString("riwayat_hauler", newData).apply()
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}