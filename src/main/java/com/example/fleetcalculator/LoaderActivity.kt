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

    private lateinit var etCycleTime: EditText
    private lateinit var spinnerBucketCapacity: Spinner
    private lateinit var spinnerBucketFactor: Spinner
    private lateinit var spinnerEfficiency: Spinner
    private lateinit var spinnerFleet: Spinner
    private lateinit var spinnerUnit: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        etCycleTime = findViewById(R.id.etCycleTime)
        spinnerBucketCapacity = findViewById(R.id.spinnerBucketCapacity)
        spinnerBucketFactor = findViewById(R.id.spinnerBucketFactor)
        spinnerEfficiency = findViewById(R.id.spinnerEfficiency)
        spinnerFleet = findViewById(R.id.spinnerFleet)
        spinnerUnit = findViewById(R.id.spinnerUnit)
        btnCalculate = findViewById(R.id.btnCalculate)
        tvResult = findViewById(R.id.tvResult)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnRiwayat = findViewById<ImageButton>(R.id.btnRiwayat)
        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Loader::class.java)
            startActivity(intent)
        }

        setupSpinners()
        btnCalculate.setOnClickListener { calculateProductivity() }
    }

    private fun setupSpinners() {
        val fleets = arrayOf(
            "EX3002", "EX2045", "EX2043", "EX2039", "EX2035",
            "EX2052", "EX2053", "EX2054", "EX2055", "EX1036", "EX1047"
        )
        val units = arrayOf(
            "PC2000 (Unit EX3000)", "PC1250 (Unit EX2000)", "PC850 (Unit EX1000)"
        )

        // Get spinner options from string arrays
        val bucketCapacityOptions = resources.getStringArray(R.array.bucket_capacity_options)
        val bucketFactorOptions = resources.getStringArray(R.array.bucket_factor_options)
        val efficiencyOptions = resources.getStringArray(R.array.efficiency_options)

        val layout = R.layout.custom_spinner_dropdown

        val fleetAdapter = ArrayAdapter(this, layout, fleets)
        fleetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFleet.adapter = fleetAdapter

        val unitAdapter = ArrayAdapter(this, layout, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = unitAdapter

        val bucketCapacityAdapter = ArrayAdapter(this, layout, bucketCapacityOptions)
        bucketCapacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBucketCapacity.adapter = bucketCapacityAdapter

        val bucketFactorAdapter = ArrayAdapter(this, layout, bucketFactorOptions)
        bucketFactorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBucketFactor.adapter = bucketFactorAdapter

        val efficiencyAdapter = ArrayAdapter(this, layout, efficiencyOptions)
        efficiencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEfficiency.adapter = efficiencyAdapter

        spinnerFleet.setPopupBackgroundResource(android.R.color.white)
        spinnerUnit.setPopupBackgroundResource(android.R.color.white)
        spinnerBucketCapacity.setPopupBackgroundResource(android.R.color.white)
        spinnerBucketFactor.setPopupBackgroundResource(android.R.color.white)
        spinnerEfficiency.setPopupBackgroundResource(android.R.color.white)
    }

    private fun calculateProductivity() {
        try {
            val selectedFleet = spinnerFleet.selectedItem.toString()
            val selectedUnit = spinnerUnit.selectedItem.toString()
            val q = extractValueFromParentheses(spinnerBucketCapacity.selectedItem.toString())
            val k = extractValueFromParentheses(spinnerBucketFactor.selectedItem.toString())
            val ct = etCycleTime.text.toString().toDouble()
            val E = extractValueFromParentheses(spinnerEfficiency.selectedItem.toString())

            val productivity = ((q * k * 60) / ct) * E
            val resultText = "Produktivitas Loader: %.2f per jam".format(productivity)
            tvResult.text = resultText
            tvResult.setTextColor(resources.getColor(android.R.color.holo_green_dark))

            val timestamp = getCurrentTimestamp()
            val formattedRiwayat = """
                🕒 $timestamp
                Fleet: $selectedFleet
                Unit: $selectedUnit
                Kapasitas Bucket: ${spinnerBucketCapacity.selectedItem}
                Bucket Factor: ${spinnerBucketFactor.selectedItem}
                Cycle Time: $ct menit
                Efisiensi: ${spinnerEfficiency.selectedItem}
                Produktivitas: %.2f m³/jam
                ------------------------------------
            """.trimIndent().format(productivity)

            // Ambil telegramId user dari SharedPreferences
            val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val telegramId = userPrefs.getString("telegramId", null)

            if (telegramId != null) {
                TelegramBotSender.sendToTelegram(telegramId, formattedRiwayat)
            } else {
                Toast.makeText(this, "Gagal mengirim ke Telegram: ID user tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }

            val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
            val existingData = sharedPref.getString("riwayat_loader", "") ?: ""
            val newData = "$formattedRiwayat\n$existingData"
            sharedPref.edit().putString("riwayat_loader", newData).apply()

        } catch (e: Exception) {
            tvResult.text = "Mohon isi semua field dengan angka yang valid."
            tvResult.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }
    }

    private fun extractValueFromParentheses(text: String): Double {
        val regex = "\\(([0-9]*\\.?[0-9]+)\\)".toRegex()
        val matchResult = regex.find(text)
        return matchResult?.groupValues?.get(1)?.toDouble() ?: 0.0
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}