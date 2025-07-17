package com.example.fleetcalculator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

class Riwayat_Match_Factor : AppCompatActivity() {

    private val REQUEST_CODE_STORAGE = 100
    private var dataUntukDisimpan: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_match_factor)

        val table = findViewById<TableLayout>(R.id.tableRiwayatMF)
        val btnExport = findViewById<Button>(R.id.btnExport)
        val btnReset = findViewById<Button>(R.id.btnResetRiwayat)

        val sharedPref = getSharedPreferences("riwayat_data", MODE_PRIVATE)
        val riwayat = sharedPref.getString("riwayat_matching_factor", null)

        val headers = listOf(
            "Tanggal", "n Hauler", "Serving Time", "n Loader", "Cycle Time", "n",
            "WH", "Pdty", "n Loader", "WH Loader", "Hasil", "Tipe"
        )

        if (riwayat.isNullOrBlank()) {
            val row = TableRow(this)
            val tv = TextView(this)
            tv.text = "Belum ada riwayat yang ditampilkan."
            tv.setTextColor(resources.getColor(android.R.color.black))
            tv.setPadding(16, 8, 16, 8)
            row.addView(tv)
            table.addView(row)
        } else {
            // Tambahkan header
            val headerRow = TableRow(this)
            for (title in headers) {
                val tv = TextView(this)
                tv.text = title
                tv.setPadding(16, 8, 16, 8)
                tv.setTypeface(null, android.graphics.Typeface.BOLD)
                headerRow.addView(tv)
                tv.setTextColor(resources.getColor(android.R.color.black))

            }
            table.addView(headerRow)

            // Tambahkan baris data
            val lines = riwayat.split("\n").filter { it.isNotBlank() }
            for (line in lines) {
                val row = TableRow(this)
                val cells = line.split(",")
                for (cell in cells) {
                    val tv = TextView(this)
                    tv.text = cell.trim()
                    tv.setPadding(16, 8, 16, 8)
                    row.addView(tv)
                    tv.setTextColor(resources.getColor(android.R.color.black))
                }
                table.addView(row)
            }

            // Untuk ekspor
            dataUntukDisimpan = headers.joinToString(",") + "\n" + riwayat
        }

        btnExport.setOnClickListener {
            if (dataUntukDisimpan.isBlank()) {
                Toast.makeText(this, "Tidak ada data untuk diunduh.", Toast.LENGTH_SHORT).show()
            } else {
                cekDanMintaIzinStorage()
            }
        }

        btnReset.setOnClickListener {
            sharedPref.edit().remove("riwayat_matching_factor").apply()
            table.removeAllViews()

            val row = TableRow(this)
            val tv = TextView(this)
            tv.text = "Belum ada riwayat yang ditampilkan."
            tv.setTextColor(resources.getColor(android.R.color.black))
            tv.setPadding(16, 8, 16, 8)
            row.addView(tv)
            table.addView(row)

            dataUntukDisimpan = ""
            Toast.makeText(this, "Riwayat berhasil dihapus.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cekDanMintaIzinStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val izin = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, izin) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(izin), REQUEST_CODE_STORAGE)
            } else {
                simpanDataKeDokumen()
            }
        } else {
            simpanDataKeDokumen()
        }
    }

    private fun simpanDataKeDokumen() {
        val documentsDir = getExternalFilesDir(null)
        val file = File(documentsDir, "riwayat_matching_factor.csv")
        try {
            FileOutputStream(file).use {
                it.write(dataUntukDisimpan.toByteArray())
            }
            Toast.makeText(this, "Berhasil disimpan di: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            simpanDataKeDokumen()
        } else {
            Toast.makeText(this, "Izin penyimpanan ditolak.", Toast.LENGTH_SHORT).show()
        }
    }
}
