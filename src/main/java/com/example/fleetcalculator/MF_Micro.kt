package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

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

                // Simpan ke Riwayat Matching Factor (dengan format sesuai permintaan)
                val timestamp = getCurrentTimestamp()
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
