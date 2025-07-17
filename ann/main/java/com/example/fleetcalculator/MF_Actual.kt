package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.example.fleetcalculator.utils.TelegramBotSender

class MF_Actual : AppCompatActivity() {

    private lateinit var spinnerFleet: Spinner
    private lateinit var containerHauler: LinearLayout
    private lateinit var containerLoader: LinearLayout
    private lateinit var btnTambahHauler: Button
    private lateinit var btnTambahLoader: Button
    private lateinit var btnHitungMF: Button
    private lateinit var tvHasilMF: TextView
    private lateinit var btnKirimTelegramMF: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnRiwayat: ImageButton

    private val fleets = arrayOf(
        "EX3002", "EX2045", "EX2043", "EX2039", "EX2035",
        "EX2052", "EX2053", "EX2054", "EX2055", "EX1036", "EX1047"
    )

    private var lastTelegramMsg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mf_actual)

        spinnerFleet = findViewById(R.id.spinnerFleetActual)
        containerHauler = findViewById(R.id.containerHauler)
        containerLoader = findViewById(R.id.containerLoader)
        btnTambahHauler = findViewById(R.id.btnTambahHauler)
        btnTambahLoader = findViewById(R.id.btnTambahLoader)
        btnHitungMF = findViewById(R.id.btnHitungMF)
        tvHasilMF = findViewById(R.id.tvHasilMF)
        btnKirimTelegramMF = findViewById(R.id.btnKirimTelegramMF)
        btnBack = findViewById(R.id.btnBack)
        btnRiwayat = findViewById(R.id.btnRiwayat)

        // Setup fleet spinner - update pakai item hitam!
        val fleetAdapter = ArrayAdapter(this, R.layout.spinner_item_black, fleets)
        fleetAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerFleet.adapter = fleetAdapter
        spinnerFleet.setPopupBackgroundResource(android.R.color.white)

        tambahBaris(containerHauler)
        tambahBaris(containerLoader)

        btnTambahHauler.setOnClickListener { tambahBaris(containerHauler) }
        btnTambahLoader.setOnClickListener { tambahBaris(containerLoader) }
        btnHitungMF.setOnClickListener { hitungMF() }
        btnBack.setOnClickListener { finish() }

        btnKirimTelegramMF.setOnClickListener {
            lastTelegramMsg?.let {
                TelegramBotSender.sendToTelegram(it)
                Toast.makeText(this, "Hasil berhasil dikirim ke Telegram!", Toast.LENGTH_SHORT).show()
            }
        }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Match_Factor::class.java)
            startActivity(intent)
        }

        // Default: tombol Kirim Telegram hidden/unaktif
        btnKirimTelegramMF.isEnabled = false
        btnKirimTelegramMF.visibility = View.GONE
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
        val etPdtyAktual = buatInput("Pdty Aktual")

        baris.addView(etN)
        baris.addView(etWH)
        baris.addView(etPdtyAktual)

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
        tvHasilMF.text = "Match Factor Actual: %.3f".format(hasil)

        val firstHaulerData = ambilDataBaris(containerHauler)
        val firstLoaderData = ambilDataBaris(containerLoader)
        val fleet = spinnerFleet.selectedItem.toString()

        val timestamp = getCurrentTimestamp()
        val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = userPrefs.getString("username", "Tanpa Nama")
        val nrp = userPrefs.getString("nrp", "-")

        lastTelegramMsg = """
🔗 LAPORAN MATCH FACTOR (MF) ACTUAL

👤 Nama                : $username
🔑 NRP                 : $nrp
🚗 Fleet               : $fleet

🚚 Hauler (Baris 1)    : n Aktual=${firstHaulerData.first}, WH Aktual=${firstHaulerData.second}, Pdty Aktual=${firstHaulerData.third}
🛠️ Loader (Baris 1)    : n Aktual=${firstLoaderData.first}, WH Aktual=${firstLoaderData.second}, Pdty Aktual=${firstLoaderData.third}

🧮 Total Hauler        : ${"%.3f".format(totalHauler)}
🧮 Total Loader        : ${"%.3f".format(totalLoader)}
🔢 Match Factor Actual : ${"%.3f".format(hasil)}

🗓️ Tanggal             : $timestamp
        """.trimIndent()

        btnKirimTelegramMF.isEnabled = true
        btnKirimTelegramMF.visibility = View.VISIBLE

        // Simpan riwayat (versi CSV tetap)
        val formattedRiwayat = "$timestamp,,,,,${firstHaulerData.first},${firstHaulerData.second},${firstHaulerData.third},${firstLoaderData.first},${firstLoaderData.second},${"%.3f".format(hasil)},MF Actual"
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
            val pdtyAktual = (baris.getChildAt(2) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            total += n * wh * pdtyAktual
        }
        return total
    }

    private fun ambilDataBaris(container: LinearLayout): Triple<Double, Double, Double> {
        return if (container.childCount > 0) {
            val baris = container.getChildAt(0) as LinearLayout
            val n = (baris.getChildAt(0) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            val wh = (baris.getChildAt(1) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            val pdtyAktual = (baris.getChildAt(2) as EditText).text.toString().toDoubleOrNull() ?: 0.0
            Triple(n, wh, pdtyAktual)
        } else {
            Triple(0.0, 0.0, 0.0)
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}