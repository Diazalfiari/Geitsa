package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import com.example.fleetcalculator.utils.TelegramBotSender

class Jumlah_Hauler : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jumlah_hauler)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnRiwayat = findViewById<ImageButton>(R.id.btnRiwayat)
        val rgOptions = findViewById<RadioGroup>(R.id.rgOptions)
        val layoutPoint1 = findViewById<LinearLayout>(R.id.layoutPoint1)
        val layoutPoint2 = findViewById<LinearLayout>(R.id.layoutPoint2)

        val resultView = findViewById<TextView>(R.id.tvResult1)
        val resultView2 = findViewById<TextView>(R.id.tvResult2)

        val black = ContextCompat.getColor(this, android.R.color.black)
        resultView.setTextColor(black)
        resultView2.setTextColor(black)

        val etLoaderProductivity = findViewById<EditText>(R.id.etLoaderProductivity)
        val etHaulerProductivity = findViewById<EditText>(R.id.etHaulerProductivity)
        val etNumberOfHauler = findViewById<EditText>(R.id.etNumberOfHauler)
        val etHaulerProductivity2 = findViewById<EditText>(R.id.etHaulerProductivity2)

        etLoaderProductivity.setTextColor(black)
        etHaulerProductivity.setTextColor(black)
        etNumberOfHauler.setTextColor(black)
        etHaulerProductivity2.setTextColor(black)

        btnBack.setOnClickListener { finish() }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Jumlah_Hauler::class.java)
            startActivity(intent)
        }

        rgOptions.setOnCheckedChangeListener { _, checkedId ->
            layoutPoint1.visibility = if (checkedId == R.id.rbOption1) View.VISIBLE else View.GONE
            layoutPoint2.visibility = if (checkedId == R.id.rbOption2) View.VISIBLE else View.GONE
        }

        // Ambil telegramId user dari SharedPreferences
        val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val telegramId = userPrefs.getString("telegramId", null)

        // Tombol Hitung untuk Form 1
        findViewById<Button>(R.id.btnCalculate1).setOnClickListener {
            val loaderProductivity = etLoaderProductivity.text.toString().toDoubleOrNull()
            val haulerProductivity = etHaulerProductivity.text.toString().toDoubleOrNull()

            if (loaderProductivity != null && haulerProductivity != null && haulerProductivity != 0.0) {
                val result = loaderProductivity / haulerProductivity
                val resultText = "Jumlah Hauler: %.2f unit".format(result)
                resultView.text = resultText

                val formattedRiwayat = """
                    🕒 ${getCurrentTimestamp()}
                    [Form 1]
                    Produktivitas Loader: $loaderProductivity BCM/jam
                    Produktivitas Hauler: $haulerProductivity BCM/jam
                    Jumlah Hauler: %.2f unit
                    ------------------------------------
                """.trimIndent().format(result)

                // Kirim ke Telegram user
                if (telegramId != null) {
                    TelegramBotSender.sendToTelegram(telegramId, formattedRiwayat)
                } else {
                    Toast.makeText(this, "Gagal mengirim ke Telegram: ID user tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }

                saveRiwayat(formattedRiwayat)
            } else {
                resultView.text = "Mohon isi semua input dengan benar."
            }
        }

        // Tombol Hitung untuk Form 2
        findViewById<Button>(R.id.btnCalculate2).setOnClickListener {
            val numberOfHauler = etNumberOfHauler.text.toString().toDoubleOrNull()
            val haulerProductivity2 = etHaulerProductivity2.text.toString().toDoubleOrNull()

            if (numberOfHauler != null && haulerProductivity2 != null) {
                val result = numberOfHauler * haulerProductivity2
                val resultText = "Produktivitas: %.2f BCM/jam".format(result)
                resultView2.text = resultText

                val formattedRiwayat = """
                    🕒 ${getCurrentTimestamp()}
                    [Form 2]
                    Jumlah Hauler: $numberOfHauler unit
                    Produktivitas Hauler: $haulerProductivity2 BCM/jam
                    Produktivitas: %.2f BCM/jam
                    ------------------------------------
                """.trimIndent().format(result)

                // Kirim ke Telegram user
                if (telegramId != null) {
                    TelegramBotSender.sendToTelegram(telegramId, formattedRiwayat)
                } else {
                    Toast.makeText(this, "Gagal mengirim ke Telegram: ID user tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }

                saveRiwayat(formattedRiwayat)
            } else {
                resultView2.text = "Mohon isi semua input dengan benar."
            }
        }
    }

    private fun saveRiwayat(text: String) {
        val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
        val existingData = sharedPref.getString("riwayat_jumlah_hauler", "") ?: ""
        val newData = "$text\n$existingData"
        sharedPref.edit().putString("riwayat_jumlah_hauler", newData).apply()
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}