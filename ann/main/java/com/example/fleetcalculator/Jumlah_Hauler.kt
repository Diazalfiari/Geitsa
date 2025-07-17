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

    private val fleets = arrayOf(
        "EX3002", "EX2045", "EX2043", "EX2039", "EX2035",
        "EX2052", "EX2053", "EX2054", "EX2055", "EX1036", "EX1047"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jumlah_hauler)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnRiwayat = findViewById<ImageButton>(R.id.btnRiwayat)
        val rgOptions = findViewById<RadioGroup>(R.id.rgOptions)
        val layoutPoint1 = findViewById<LinearLayout>(R.id.layoutPoint1)
        val layoutPoint2 = findViewById<LinearLayout>(R.id.layoutPoint2)
        val spinnerFleet = findViewById<Spinner>(R.id.spinnerFleetJumlahHauler)

        val resultView = findViewById<TextView>(R.id.tvResult1)
        val resultView2 = findViewById<TextView>(R.id.tvResult2)
        val btnKirimTelegram1 = findViewById<Button>(R.id.btnKirimTelegram1)
        val btnKirimTelegram2 = findViewById<Button>(R.id.btnKirimTelegram2)

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

        // Setup spinner fleet - UPDATE: pakai layout spinner_item_black & spinner_dropdown_item_black
        val fleetAdapter = ArrayAdapter(this, R.layout.spinner_item_black, fleets)
        fleetAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerFleet.adapter = fleetAdapter
        spinnerFleet.setPopupBackgroundResource(android.R.color.white)

        rgOptions.setOnCheckedChangeListener { _, checkedId ->
            layoutPoint1.visibility = if (checkedId == R.id.rbOption1) View.VISIBLE else View.GONE
            layoutPoint2.visibility = if (checkedId == R.id.rbOption2) View.VISIBLE else View.GONE
        }

        // Ambil nama user dan NRP dari SharedPreferences untuk laporan grup
        val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = userPrefs.getString("username", "Tanpa Nama")
        val nrp = userPrefs.getString("nrp", "-")

        var lastRiwayat1: String? = null
        var lastRiwayat2: String? = null

        // Tombol Hitung untuk Form 1
        findViewById<Button>(R.id.btnCalculate1).setOnClickListener {
            val loaderProductivity = etLoaderProductivity.text.toString().toDoubleOrNull()
            val haulerProductivity = etHaulerProductivity.text.toString().toDoubleOrNull()
            val fleet = spinnerFleet.selectedItem.toString()

            if (loaderProductivity != null && haulerProductivity != null && haulerProductivity != 0.0) {
                val result = loaderProductivity / haulerProductivity
                val resultText = "Jumlah Hauler: %.2f unit".format(result)
                resultView.text = resultText

                lastRiwayat1 = """
🔢 LAPORAN JUMLAH HAULER

👤 Nama                     : $username
🔑 NRP                      : $nrp
🚗 Fleet                    : $fleet

🚜 Produktivitas Loader     : $loaderProductivity BCM/jam
🚚 Produktivitas Hauler     : $haulerProductivity BCM/jam
🟰 Jumlah Hauler            : %.2f unit

🗓️ Tanggal                  : ${getCurrentTimestamp()}
                """.trimIndent().format(result)

                btnKirimTelegram1.isEnabled = true
                btnKirimTelegram1.visibility = View.VISIBLE
            } else {
                resultView.text = "Mohon isi semua input dengan benar."
                btnKirimTelegram1.isEnabled = false
                btnKirimTelegram1.visibility = View.GONE
            }
        }

        btnKirimTelegram1.setOnClickListener {
            lastRiwayat1?.let {
                TelegramBotSender.sendToTelegram(it)
                saveRiwayat(it)
                Toast.makeText(this, "Hasil berhasil dikirim ke Telegram!", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Hitung untuk Form 2
        findViewById<Button>(R.id.btnCalculate2).setOnClickListener {
            val numberOfHauler = etNumberOfHauler.text.toString().toDoubleOrNull()
            val haulerProductivity2 = etHaulerProductivity2.text.toString().toDoubleOrNull()
            val fleet = spinnerFleet.selectedItem.toString()

            if (numberOfHauler != null && haulerProductivity2 != null) {
                val result = numberOfHauler * haulerProductivity2
                val resultText = "Produktivitas: %.2f BCM/jam".format(result)
                resultView2.text = resultText

                lastRiwayat2 = """
🔢 LAPORAN JUMLAH HAULER

👤 Nama                     : $username
🔑 NRP                      : $nrp
🚗 Fleet                    : $fleet

🟰 Jumlah Hauler            : $numberOfHauler unit
🚚 Produktivitas Hauler     : $haulerProductivity2 BCM/jam
🧮 Produktivitas            : %.2f BCM/jam

🗓️ Tanggal                  : ${getCurrentTimestamp()}
                """.trimIndent().format(result)

                btnKirimTelegram2.isEnabled = true
                btnKirimTelegram2.visibility = View.VISIBLE
            } else {
                resultView2.text = "Mohon isi semua input dengan benar."
                btnKirimTelegram2.isEnabled = false
                btnKirimTelegram2.visibility = View.GONE
            }
        }

        btnKirimTelegram2.setOnClickListener {
            lastRiwayat2?.let {
                TelegramBotSender.sendToTelegram(it)
                saveRiwayat(it)
                Toast.makeText(this, "Hasil berhasil dikirim ke Telegram!", Toast.LENGTH_SHORT).show()
            }
        }

        // Default: tombol Kirim Telegram hidden/unaktif
        btnKirimTelegram1.isEnabled = false
        btnKirimTelegram1.visibility = View.GONE
        btnKirimTelegram2.isEnabled = false
        btnKirimTelegram2.visibility = View.GONE
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