package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.example.fleetcalculator.utils.TelegramBotSender

class MF_Micro : AppCompatActivity() {

    private val fleets = arrayOf(
        "EX3002", "EX2045", "EX2043", "EX2039", "EX2035",
        "EX2052", "EX2053", "EX2054", "EX2055", "EX1036", "EX1047"
    )

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
        val spinnerFleet = findViewById<Spinner>(R.id.spinnerFleetMicro)
        val btnKirimTelegramMF = findViewById<Button>(R.id.btnKirimTelegramMF)

        // Setup fleet spinner - update: pakai layout spinner_item_black & spinner_dropdown_item_black
        val fleetAdapter = ArrayAdapter(this, R.layout.spinner_item_black, fleets)
        fleetAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerFleet.adapter = fleetAdapter
        spinnerFleet.setPopupBackgroundResource(android.R.color.white)

        btnBack.setOnClickListener { finish() }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Match_Factor::class.java)
            startActivity(intent)
        }

        var lastTelegramMsg: String? = null

        btnCalculateMF.setOnClickListener {
            try {
                val nHauler = etJumlahHauler.text.toString().toDouble()
                val servingTime = etServingTime.text.toString().toDouble()
                val nLoader = etJumlahLoader.text.toString().toDouble()
                val cycleTime = etCycleTime.text.toString().toDouble()
                val fleet = spinnerFleet.selectedItem.toString()

                val mf = (nHauler * servingTime) / (nLoader * cycleTime)
                tvResultMF.text = "Match Factor Micro: %.3f".format(mf)

                val timestamp = getCurrentTimestamp()
                val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val username = userPrefs.getString("username", "Tanpa Nama")
                val nrp = userPrefs.getString("nrp", "-")

                lastTelegramMsg = """
🔗 LAPORAN MATCH FACTOR (MF) MICRO

👤 Nama           : $username
🔑 NRP            : $nrp
🚗 Fleet          : $fleet

🚚 Jumlah Hauler  : $nHauler
⏱️ Serving Time   : $servingTime menit
🛠️ Jumlah Loader  : $nLoader
🔄 Cycle Time     : $cycleTime menit
🔢 Match Factor   : %.3f

🗓️ Tanggal        : $timestamp
                """.trimIndent().format(mf)

                btnKirimTelegramMF.isEnabled = true
                btnKirimTelegramMF.visibility = View.VISIBLE

                // Simpan riwayat (versi string CSV tetap, bisa disesuaikan formatnya jika perlu)
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
                btnKirimTelegramMF.isEnabled = false
                btnKirimTelegramMF.visibility = View.GONE
                lastTelegramMsg = null
            }
        }

        btnKirimTelegramMF.setOnClickListener {
            lastTelegramMsg?.let {
                TelegramBotSender.sendToTelegram(it)
                Toast.makeText(this, "Hasil berhasil dikirim ke Telegram!", Toast.LENGTH_SHORT).show()
            }
        }

        // Default: tombol Kirim Telegram hidden/unaktif
        btnKirimTelegramMF.isEnabled = false
        btnKirimTelegramMF.visibility = View.GONE
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}