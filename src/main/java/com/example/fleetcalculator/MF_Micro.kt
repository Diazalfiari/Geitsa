package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.example.fleetcalculator.utils.TelegramBotSender

class MF_Micro : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mf_micro)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnRiwayat = findViewById<ImageButton>(R.id.btnRiwayat)
        val etJumlahHauler = findViewById<EditText>(R.id.etJumlahHauler)
        val etServingTime = findViewById<EditText>(R.id.etServingTime)
        val etJumlahLoader = findViewById<EditText>(R.id.etJumlahLoader)
        val etCycleTime = findViewById<EditText>(R.id.etCycleTime)
        val btnCalculateMF = findViewById<Button>(R.id.btnCalculateMF)
        val tvResultMF = findViewById<TextView>(R.id.tvResultMF)

        btnBack.setOnClickListener {
            finish()
        }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Match_Factor::class.java)
            startActivity(intent)
        }

        btnCalculateMF.setOnClickListener {
            try {
                val nHauler = etJumlahHauler.text.toString().toDouble()
                val servingTime = etServingTime.text.toString().toDouble()
                val nLoader = etJumlahLoader.text.toString().toDouble()
                val cycleTime = etCycleTime.text.toString().toDouble()

                val mf = (nHauler * servingTime) / (nLoader * cycleTime)
                tvResultMF.text = "Matching Factor Micro: %.3f".format(mf)

                val timestamp = getCurrentTimestamp()
                val formattedTelegram = """
                    🕒 $timestamp
                    [MF Micro]
                    Jumlah Hauler: $nHauler
                    Serving Time: $servingTime menit
                    Jumlah Loader: $nLoader
                    Cycle Time: $cycleTime menit
                    Matching Factor Micro: %.3f
                    ------------------------------------
                """.trimIndent().format(mf)

                // Ambil telegramId user dari SharedPreferences
                val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val telegramId = userPrefs.getString("telegramId", null)

                if (telegramId != null) {
                    TelegramBotSender.sendToTelegram(telegramId, formattedTelegram)
                } else {
                    Toast.makeText(this, "Gagal mengirim ke Telegram: ID user tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }

                val formattedRiwayat = "$timestamp,," +
                        "${servingTime},," +
                        "${cycleTime}," +
                        "${nHauler},,," +
                        "${nLoader},," +
                        "${"%.3f".format(mf)},MF Micro"

                val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
                val existingData = sharedPref.getString("riwayat_matching_factor", "") ?: ""
                val newData = "$formattedRiwayat\n$existingData"
                sharedPref.edit().putString("riwayat_matching_factor", newData).apply()

            } catch (e: Exception) {
                tvResultMF.text = "Mohon isi semua field dengan angka yang valid."
            }
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}