package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.example.fleetcalculator.utils.TelegramBotSender

class LoaderActivity : AppCompatActivity() {

    private lateinit var spinnerBucketCapacity: Spinner
    private lateinit var etCycleTime: EditText
    private lateinit var spinnerBucketFactor: Spinner
    private lateinit var spinnerEfficiency: Spinner
    private lateinit var spinnerFleet: Spinner
    private lateinit var spinnerUnit: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var tvResult: TextView
    private lateinit var btnKirimTelegramLoader: Button

    private var lastTelegramMsg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        spinnerBucketCapacity = findViewById(R.id.spinnerBucketCapacity)
        etCycleTime = findViewById(R.id.etCycleTime)
        spinnerBucketFactor = findViewById(R.id.spinnerBucketFactor)
        spinnerEfficiency = findViewById(R.id.spinnerEfficiency)
        spinnerFleet = findViewById(R.id.spinnerFleet)
        spinnerUnit = findViewById(R.id.spinnerUnit)
        btnCalculate = findViewById(R.id.btnCalculate)
        tvResult = findViewById(R.id.tvResult)
        btnKirimTelegramLoader = findViewById(R.id.btnKirimTelegramLoader)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val btnRiwayat = findViewById<ImageButton>(R.id.btnRiwayat)
        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Loader::class.java)
            startActivity(intent)
        }

        setupSpinners()
        btnCalculate.setOnClickListener { calculateProductivity() }
        btnKirimTelegramLoader.setOnClickListener {
            lastTelegramMsg?.let {
                TelegramBotSender.sendToTelegram(it)
                Toast.makeText(this, "Hasil berhasil dikirim ke Telegram!", Toast.LENGTH_SHORT).show()
            }
        }

        btnKirimTelegramLoader.isEnabled = false
        btnKirimTelegramLoader.visibility = View.GONE
    }

    private fun setupSpinners() {
        val fleets = arrayOf(
            "EX3002", "EX2045", "EX2043", "EX2039", "EX2035",
            "EX2052", "EX2053", "EX2054", "EX2055", "EX1036", "EX1047"
        )
        val units = arrayOf(
            "PC2000 (Unit EX3000)", "PC1250 (Unit EX2000)", "PC850 (Unit EX1000)"
        )

        // Ganti dengan layout spinner_item_black dan spinner_dropdown_item_black
        val bucketCapacityAdapter = ArrayAdapter.createFromResource(
            this, R.array.bucket_capacity_options, R.layout.spinner_item_black)
        bucketCapacityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerBucketCapacity.adapter = bucketCapacityAdapter

        val bucketFactorAdapter = ArrayAdapter.createFromResource(
            this, R.array.bucket_factor_options, R.layout.spinner_item_black)
        bucketFactorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerBucketFactor.adapter = bucketFactorAdapter

        val efficiencyAdapter = ArrayAdapter.createFromResource(
            this, R.array.efficiency_options, R.layout.spinner_item_black)
        efficiencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerEfficiency.adapter = efficiencyAdapter

        val fleetAdapter = ArrayAdapter(this, R.layout.spinner_item_black, fleets)
        fleetAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerFleet.adapter = fleetAdapter

        val unitAdapter = ArrayAdapter(this, R.layout.spinner_item_black, units)
        unitAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerUnit.adapter = unitAdapter

        spinnerFleet.setPopupBackgroundResource(android.R.color.white)
        spinnerUnit.setPopupBackgroundResource(android.R.color.white)
        spinnerBucketCapacity.setPopupBackgroundResource(android.R.color.white)
        spinnerBucketFactor.setPopupBackgroundResource(android.R.color.white)
        spinnerEfficiency.setPopupBackgroundResource(android.R.color.white)
    }

    private fun extractValue(option: String): Double {
        val regex = Regex("""\(([\d.]+)\)$""")
        val match = regex.find(option)
        return match?.groups?.get(1)?.value?.toDoubleOrNull() ?: 0.0
    }

    private fun calculateProductivity() {
        try {
            val selectedFleet = spinnerFleet.selectedItem.toString()
            val selectedUnit = spinnerUnit.selectedItem.toString()
            val q = extractValue(spinnerBucketCapacity.selectedItem.toString())
            val k = extractValue(spinnerBucketFactor.selectedItem.toString())
            val ct = etCycleTime.text.toString().toDouble()
            val E = extractValue(spinnerEfficiency.selectedItem.toString())

            if (q == 0.0 || k == 0.0 || ct == 0.0 || E == 0.0) {
                tvResult.text = "Mohon isi semua field dengan angka yang valid."
                tvResult.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                btnKirimTelegramLoader.isEnabled = false
                btnKirimTelegramLoader.visibility = View.GONE
                lastTelegramMsg = null
                return
            }

            val productivity = ((q * k * 60) / ct) * E
            val resultText = "Produktivitas Loader: %.2f m³/jam".format(productivity)
            tvResult.text = resultText
            tvResult.setTextColor(resources.getColor(android.R.color.holo_green_dark))

            val timestamp = getCurrentTimestamp()
            val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val username = userPrefs.getString("username", "Tanpa Nama")
            val nrp = userPrefs.getString("nrp", "-")

            lastTelegramMsg = """
📊 LAPORAN PRODUKTIVITAS LOADER

👤 Nama           : $username
🔑 NRP            : $nrp
🚗 Fleet          : $selectedFleet
🛠️ Unit           : $selectedUnit

🦺 Kapasitas      : $q m³
📊 Bucket Factor  : $k
⏱️ Cycle Time     : $ct menit
💡 Efisiensi      : $E

🚀 PRODUKTIVITAS LOADER : %.2f m³/jam

$timestamp
""".trimIndent().format(productivity)

            btnKirimTelegramLoader.isEnabled = true
            btnKirimTelegramLoader.visibility = View.VISIBLE

            // Simpan riwayat CSV
            val formattedRiwayat = "$timestamp,$username,$nrp,$selectedFleet,$selectedUnit,$q,$k,$ct,$E,%.2f".format(productivity)
            val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
            val existingData = sharedPref.getString("riwayat_loader", "") ?: ""
            val newData = "$formattedRiwayat\n$existingData"
            sharedPref.edit().putString("riwayat_loader", newData).apply()

        } catch (e: Exception) {
            tvResult.text = "Mohon isi semua field dengan angka yang valid."
            tvResult.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            btnKirimTelegramLoader.isEnabled = false
            btnKirimTelegramLoader.visibility = View.GONE
            lastTelegramMsg = null
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}