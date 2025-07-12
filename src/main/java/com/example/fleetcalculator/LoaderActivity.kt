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

    private lateinit var etBucketCapacity: EditText
    private lateinit var etCycleTime: EditText
    private lateinit var etCustomEfficiency: EditText
    private lateinit var spinnerBucketFactor: Spinner
    private lateinit var spinnerEfficiency: Spinner
    private lateinit var spinnerFleet: Spinner
    private lateinit var spinnerUnit: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var tvResult: TextView
    private lateinit var tvCustomNote: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        etBucketCapacity = findViewById(R.id.etBucketCapacity)
        etCycleTime = findViewById(R.id.etCycleTime)
        etCustomEfficiency = findViewById(R.id.etCustomEfficiency)
        spinnerBucketFactor = findViewById(R.id.spinnerBucketFactor)
        spinnerEfficiency = findViewById(R.id.spinnerEfficiency)
        spinnerFleet = findViewById(R.id.spinnerFleet)
        spinnerUnit = findViewById(R.id.spinnerUnit)
        btnCalculate = findViewById(R.id.btnCalculate)
        tvResult = findViewById(R.id.tvResult)
        tvCustomNote = findViewById(R.id.tvCustomNote)

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
        val bucketFactors = arrayOf("1.00", "0.95", "0.90")
        val efficiencies = arrayOf("0.7", "0.75", "0.8", "0.85", "0.9", "0.95", "1", "Custom")

        val layout = R.layout.custom_spinner_dropdown

        val fleetAdapter = ArrayAdapter(this, layout, fleets)
        fleetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFleet.adapter = fleetAdapter

        val unitAdapter = ArrayAdapter(this, layout, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = unitAdapter

        val bucketFactorAdapter = ArrayAdapter(this, layout, bucketFactors)
        bucketFactorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBucketFactor.adapter = bucketFactorAdapter

        val efficiencyAdapter = ArrayAdapter(this, layout, efficiencies)
        efficiencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEfficiency.adapter = efficiencyAdapter

        spinnerFleet.setPopupBackgroundResource(android.R.color.white)
        spinnerUnit.setPopupBackgroundResource(android.R.color.white)
        spinnerBucketFactor.setPopupBackgroundResource(android.R.color.white)
        spinnerEfficiency.setPopupBackgroundResource(android.R.color.white)

        spinnerEfficiency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val isCustom = efficiencies[position] == "Custom"
                etCustomEfficiency.visibility = if (isCustom) View.VISIBLE else View.GONE
                tvCustomNote.visibility = if (isCustom) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                etCustomEfficiency.visibility = View.GONE
                tvCustomNote.visibility = View.GONE
            }
        }
    }

    private fun calculateProductivity() {
        try {
            val selectedFleet = spinnerFleet.selectedItem.toString()
            val selectedUnit = spinnerUnit.selectedItem.toString()
            val q = etBucketCapacity.text.toString().toDouble()
            val k = spinnerBucketFactor.selectedItem.toString().toDouble()
            val ct = etCycleTime.text.toString().toDouble()
            val E = if (spinnerEfficiency.selectedItem.toString() == "Custom") {
                etCustomEfficiency.text.toString().toDouble()
            } else {
                spinnerEfficiency.selectedItem.toString().toDouble()
            }

            val productivity = ((q * k * 60) / ct) * E
            val resultText = "Produktivitas Loader: %.2f per jam".format(productivity)
            tvResult.text = resultText
            tvResult.setTextColor(resources.getColor(android.R.color.holo_green_dark))

            val timestamp = getCurrentTimestamp()
            val formattedRiwayat = """
                🕒 $timestamp
                Fleet: $selectedFleet
                Unit: $selectedUnit
                Kapasitas Bucket: $q m³
                Bucket Factor: $k
                Cycle Time: $ct menit
                Efisiensi: $E
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

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}