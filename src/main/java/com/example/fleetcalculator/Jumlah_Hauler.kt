package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

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

        // Ubah warna font hasil perhitungan menjadi hitam
        val black = ContextCompat.getColor(this, android.R.color.black)
        resultView.setTextColor(black)
        resultView2.setTextColor(black)

        // Ambil semua EditText dan set warna teksnya jadi hitam
        val etLoaderProductivity = findViewById<EditText>(R.id.etLoaderProductivity)
        val etHaulerProductivity = findViewById<EditText>(R.id.etHaulerProductivity)
        val etDistance1 = findViewById<EditText>(R.id.etDistance1)
        val etNumberOfHauler = findViewById<EditText>(R.id.etNumberOfHauler)
        val etHaulerProductivity2 = findViewById<EditText>(R.id.etHaulerProductivity2)
        val etDistance2 = findViewById<EditText>(R.id.etDistance2)

        etLoaderProductivity.setTextColor(black)
        etHaulerProductivity.setTextColor(black)
        etDistance1.setTextColor(black)
        etNumberOfHauler.setTextColor(black)
        etHaulerProductivity2.setTextColor(black)
        etDistance2.setTextColor(black)

        btnBack.setOnClickListener { finish() }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Jumlah_Hauler::class.java)
            startActivity(intent)
        }

        rgOptions.setOnCheckedChangeListener { _, checkedId ->
            layoutPoint1.visibility = if (checkedId == R.id.rbOption1) View.VISIBLE else View.GONE
            layoutPoint2.visibility = if (checkedId == R.id.rbOption2) View.VISIBLE else View.GONE
        }

        // Tombol Hitung untuk Form 1
        findViewById<Button>(R.id.btnCalculate1).setOnClickListener {
            val loaderProductivity = etLoaderProductivity.text.toString().toDoubleOrNull()
            val haulerProductivity = etHaulerProductivity.text.toString().toDoubleOrNull()
            val distance = etDistance1.text.toString().toDoubleOrNull()

            if (loaderProductivity != null && haulerProductivity != null && distance != null && haulerProductivity != 0.0) {
                val result = (loaderProductivity / haulerProductivity) * distance
                val resultText = "Jumlah Hauler: %.2f unit".format(result)
                resultView.text = resultText

                // Simpan Riwayat Form 1
                saveRiwayat("""
                    🕒 ${getCurrentTimestamp()}
                    [Form 1]
                    Produktivitas Loader: $loaderProductivity BCM/jam
                    Produktivitas Hauler: $haulerProductivity BCM/jam
                    Jarak: $distance m
                    Jumlah Hauler: %.2f unit
                    ------------------------------------
                """.trimIndent().format(result))
            } else {
                resultView.text = "Mohon isi semua input dengan benar."
            }
        }

        // Tombol Hitung untuk Form 2
        findViewById<Button>(R.id.btnCalculate2).setOnClickListener {
            val numberOfHauler = etNumberOfHauler.text.toString().toDoubleOrNull()
            val haulerProductivity2 = etHaulerProductivity2.text.toString().toDoubleOrNull()
            val distance2 = etDistance2.text.toString().toDoubleOrNull()

            if (numberOfHauler != null && haulerProductivity2 != null && distance2 != null && distance2 != 0.0) {
                val result = (numberOfHauler * haulerProductivity2) / distance2
                val resultText = "Produktivitas: %.2f BCM/jam".format(result)
                resultView2.text = resultText

                // Simpan Riwayat Form 2
                saveRiwayat("""
                    🕒 ${getCurrentTimestamp()}
                    [Form 2]
                    Jumlah Hauler: $numberOfHauler unit
                    Produktivitas Hauler: $haulerProductivity2 BCM/jam
                    Jarak: $distance2 m
                    Produktivitas: %.2f BCM/jam
                    ------------------------------------
                """.trimIndent().format(result))
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
