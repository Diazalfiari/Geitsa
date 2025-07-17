package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import com.example.fleetcalculator.utils.TelegramBotSender

class Hauler : AppCompatActivity() {

    private val efisiensiKerjaValues = arrayOf(0.83, 0.80, 0.75, 0.70)
    private val efisiensiKerjaLabels = arrayOf(
        "Good (0.83)",
        "Average (0.80)",
        "Rather poor (0.75)",
        "Poor (0.70)"
    )
    private val fleets = arrayOf(
        "EX3002", "EX2045", "EX2043", "EX2039", "EX2035",
        "EX2052", "EX2053", "EX2054", "EX2055", "EX1036", "EX1047"
    )

    // Variabel untuk simpan hasil perhitungan terakhir
    private var lastResultText: String? = null
    private var lastPayload: Double? = null
    private var lastJarak: Double? = null
    private var lastKecepatan: Double? = null
    private var lastTravelTime: Double? = null
    private var lastCmt: Double? = null
    private var lastEt: Double? = null
    private var lastProduktivitas: Double? = null
    private var lastFleet: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hauler)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnRiwayat = findViewById<ImageButton>(R.id.btnRiwayat)
        val btnGenerateTravelTime = findViewById<Button>(R.id.btnGenerateTravelTime)
        val btnHitung = findViewById<Button>(R.id.btnHitungHauler)
        val btnKirimTelegram = findViewById<Button>(R.id.btnKirimTelegram)
        val spinnerFleet = findViewById<Spinner>(R.id.spinnerFleetHauler)

        val edtPayload = findViewById<EditText>(R.id.edtPayload)
        val edtJarak = findViewById<EditText>(R.id.edtJarak)
        val edtKecepatan = findViewById<EditText>(R.id.edtKecepatan)
        val edtTravelTime = findViewById<EditText>(R.id.edtTravelTime)
        val spinnerEfisiensiKerja = findViewById<Spinner>(R.id.spinnerEfisiensiKerja)
        val txtResult = findViewById<TextView>(R.id.txtResult)

        val black = ContextCompat.getColor(this, android.R.color.black)

        edtPayload.setTextColor(black)
        edtJarak.setTextColor(black)
        edtKecepatan.setTextColor(black)
        edtTravelTime.setTextColor(black)
        txtResult.setTextColor(black)

        val adapterEfisiensi = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            efisiensiKerjaLabels
        )
        adapterEfisiensi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEfisiensiKerja.adapter = adapterEfisiensi

        // UPDATE: pakai layout spinner_item_black & spinner_dropdown_item_black untuk spinnerFleet
        val adapterFleet = ArrayAdapter(this, R.layout.spinner_item_black, fleets)
        adapterFleet.setDropDownViewResource(R.layout.spinner_dropdown_item_black)
        spinnerFleet.adapter = adapterFleet
        spinnerFleet.setPopupBackgroundResource(android.R.color.white)

        btnBack.setOnClickListener { finish() }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Hauler::class.java)
            startActivity(intent)
        }

        btnGenerateTravelTime.setOnClickListener {
            val jarak = edtJarak.text.toString().toDoubleOrNull()
            val kecepatan = edtKecepatan.text.toString().toDoubleOrNull()
            if (jarak == null || kecepatan == null || kecepatan == 0.0) {
                txtResult.text = "Mohon isi jarak dan kecepatan dengan benar."
                return@setOnClickListener
            }
            val kecepatanMeterPerMenit = (kecepatan * 1000) / 60.0
            val travelTime = (jarak * 2) / kecepatanMeterPerMenit
            edtTravelTime.setText("%.2f".format(travelTime))
            txtResult.text = "Travel Time Pulang-Pergi: %.2f menit".format(travelTime)
        }

        btnHitung.setOnClickListener {
            val payload = edtPayload.text.toString().toDoubleOrNull()
            val jarak = edtJarak.text.toString().toDoubleOrNull()
            val kecepatan = edtKecepatan.text.toString().toDoubleOrNull()
            val selectedEfisiensiKerja = spinnerEfisiensiKerja.selectedItemPosition
            val et = efisiensiKerjaValues.getOrElse(selectedEfisiensiKerja) { 0.80 }
            val fleet = spinnerFleet.selectedItem.toString()

            if (payload == null || jarak == null || kecepatan == null || kecepatan == 0.0) {
                txtResult.text = "Mohon isi semua field dan generate Travel Time terlebih dahulu."
                btnKirimTelegram.isEnabled = false
                return@setOnClickListener
            }

            val kecepatanMeterPerMenit = (kecepatan * 1000) / 60.0
            val travelTime = (jarak * 2) / kecepatanMeterPerMenit
            val cmt = 4 + travelTime
            val produktivitas = (payload / 2.39) * (60 / cmt) * et

            val resultText = """
Travel Time (Pulang-Pergi): %.2f menit
Cycle Time: %.2f menit
Produktivitas: %.2f ton/jam
Fleet: $fleet
            """.trimIndent().format(travelTime, cmt, produktivitas)
            txtResult.text = resultText

            lastResultText = resultText
            lastPayload = payload
            lastJarak = jarak
            lastKecepatan = kecepatan
            lastTravelTime = travelTime
            lastCmt = cmt
            lastEt = et
            lastProduktivitas = produktivitas
            lastFleet = fleet

            btnKirimTelegram.isEnabled = true
        }

        btnKirimTelegram.setOnClickListener {
            val userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val username = userPrefs.getString("username", "Tanpa Nama")
            val nrp = userPrefs.getString("nrp", "-")
            val timestamp = getCurrentTimestamp()

            val formattedTelegram = """
📋 LAPORAN PRODUKTIVITAS HAULER

👤 Nama           : $username
🔑 NRP            : $nrp
🚗 Fleet          : ${lastFleet}

⛽ Payload        : ${lastPayload} ton
🛣️ Jarak Tempuh   : ${lastJarak} m
🚚 Kecepatan      : ${lastKecepatan} km/h
⏱️ Travel Time    : %.2f menit
🔄 Cycle Time     : %.2f menit
💡 Efisiensi      : ${lastEt}

🚀 PRODUKTIVITAS HAULER   : %.2f ton/jam

$timestamp
            """.trimIndent().format(
                lastTravelTime ?: 0.0,
                lastCmt ?: 0.0,
                lastProduktivitas ?: 0.0
            )

            TelegramBotSender.sendToTelegram(formattedTelegram)

            val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
            val existingData = sharedPref.getString("riwayat_hauler", "") ?: ""
            val newData = "$formattedTelegram\n$existingData"
            sharedPref.edit().putString("riwayat_hauler", newData).apply()

            Toast.makeText(this, "Hasil berhasil dikirim ke Telegram!", Toast.LENGTH_SHORT).show()
        }

        btnKirimTelegram.isEnabled = false
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}