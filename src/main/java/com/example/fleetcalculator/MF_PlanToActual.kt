package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.example.fleetcalculator.utils.TelegramBotSender

class MF_PlanToActual : AppCompatActivity() {

    private lateinit var containerHauler: LinearLayout
    private lateinit var containerLoader: LinearLayout
    private lateinit var btnTambahHauler: Button
    private lateinit var btnTambahLoader: Button
    private lateinit var btnHitungMF: Button
    private lateinit var tvHasilMF: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnRiwayat: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mf_plan_to_actual)

        containerHauler = findViewById(R.id.containerHauler)
        containerLoader = findViewById(R.id.containerLoader)
        btnTambahHauler = findViewById(R.id.btnTambahHauler)
        btnTambahLoader = findViewById(R.id.btnTambahLoader)
        btnHitungMF = findViewById(R.id.btnHitungMF)
        tvHasilMF = findViewById(R.id.tvHasilMF)
        btnBack = findViewById(R.id.btnBack)
        btnRiwayat = findViewById(R.id.btnRiwayat)

        tambahBaris(containerHauler)
        tambahBaris(containerLoader)

        btnTambahHauler.setOnClickListener { tambahBaris(containerHauler) }
        btnTambahLoader.setOnClickListener { tambahBaris(containerLoader) }
        btnHitungMF.setOnClickListener { hitungMF() }
        btnBack.setOnClickListener { finish() }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Match_Factor::class.java)
            startActivity(intent)
        }
    }

    private fun tambahBaris(container: LinearLayout) {
        val baris = LinearLayout(this)
        baris.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        baris.orientation = LinearLayout.HORIZONTAL

        val etN = buatInput("n Aktual")
        val etWH = buatInput("WH Aktual")
        val etPdtyPlan = buatInput("Pdty Plan")

        baris.addView(etN)
        baris.addView(etWH)
        baris.addView(etPdtyPlan)

        container.addView(baris)
    }

    private fun buatInput(hint: String): EditText {
        val et = EditText(this)
        et.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        et.hint = hint
        et.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        et.setTextColor(resources.getColor(android.R.color.black))
        et.setHintTextColor(resources.getColor(android.R.color.black))
        return et
    }

    private fun hitungMF() {
        val totalHauler = hitungTotal(containerHauler)
        val totalLoader = hitungTotal(containerLoader)

        val hasil = if (totalLoader != 0.0) totalHauler / totalLoader else 0.0
        tvHasilMF.text = "Matching Factor Plan to Actual: %.3f".format(hasil)

        val firstHaulerData = ambilDataBaris(containerHauler)
        val firstLoaderData = ambilDataBaris(containerLoader)

        val timestamp = getCurrentTimestamp()

        // Format Telegram (lebih rapi & jelas)
        val formattedTelegram = """
            🕒 $timestamp
            [MF Plan to Actual]
            Hauler (Baris Pertama): n Aktual=${firstHaulerData.first}, WH Aktual=${firstHaulerData.second}, Pdty Plan=${firstHaulerData.third}
            Loader (Baris Pertama): n Aktual=${firstLoaderData.first}, WH Aktual=${firstLoaderData.second}, Pdty Plan=${firstLoaderData.third}
            Total Hauler: ${"%.3f".format(totalHauler)}
            Total Loader: ${"%.3f".format(totalLoader)}
            Matching Factor Plan to Actual: ${"%.3f".format(hasil)}
            ------------------------------------
        """.trimIndent()

        // Ambil telegramId user dari SharedPreferences
        val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val telegramId = userPrefs.getString("telegramId", null)

        if (telegramId != null) {
            TelegramBotSender.sendToTelegram(telegramId, formattedTelegram)
        } else {
            Toast.makeText(this, "Gagal mengirim ke Telegram: ID user tidak ditemukan.", Toast.LENGTH_SHORT).show()
        }

        // Format CSV riwayat (sesuai permintaan awal)
        val formattedRiwayat = "$timestamp,,,,,${firstHaulerData.first},${firstHaulerData.second},${firstHaulerData.third},${firstLoaderData.first},${firstLoaderData.second},${"%.3f".format(hasil)},MF Plan to Actual"

        val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
        val existingData = sharedPref.getString("riwayat_matching_factor", "") ?: ""
        val newData = "$formattedRiwayat\n$existingData"
        sharedPref.edit().putString("riwayat_matching_factor", newData).apply()
    }

    private fun hitungTotal(container: LinearLayout): Double {
        var total = 0.0
        for (i in 0 until container.childCount) {
            val baris = container.getChildAt(i) as LinearLayout
            val n = (baris.getChildAt(0) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            val wh = (baris.getChildAt(1) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            val pdtyPlan = (baris.getChildAt(2) as EditText).text.toString().toDoubleOrNull() ?: 0.0

            total += n * wh * pdtyPlan
        }
        return total
    }

    private fun ambilDataBaris(container: LinearLayout): Triple<Double, Double, Double> {
        return if (container.childCount > 0) {
            val baris = container.getChildAt(0) as LinearLayout
            val n = (baris.getChildAt(0) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            val wh = (baris.getChildAt(1) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            val pdtyPlan = (baris.getChildAt(2) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            Triple(n, wh, pdtyPlan)
        } else {
            Triple(0.0, 0.0, 0.0)
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}