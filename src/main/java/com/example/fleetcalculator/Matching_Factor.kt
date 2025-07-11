package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Matching_Factor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_factor2)

        val btnMicro = findViewById<ImageButton>(R.id.btnMicro)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMacro = findViewById<ImageButton>(R.id.btnMacro)
        val btnPlan = findViewById<ImageButton>(R.id.btnPlan)
        val btnActualToPlan = findViewById<ImageButton>(R.id.btnActualToPlan)
        val btnActual = findViewById<ImageButton>(R.id.btnActual)
        val btnRiwayat = findViewById<ImageButton>(R.id.btnRiwayat)

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Match_Factor::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish() // Kembali ke halaman Matching Factor
        }



        btnMicro.setOnClickListener {
            startActivity(Intent(this, MF_Micro::class.java))
        }

        btnMacro.setOnClickListener {
            startActivity(Intent(this, MF_Macro::class.java))
        }

        btnPlan.setOnClickListener {
            startActivity(Intent(this, MF_Plan::class.java))
        }

        btnActualToPlan.setOnClickListener {
            startActivity(Intent(this, MF_PlanToActual::class.java))
        }

        btnActual.setOnClickListener {
            startActivity(Intent(this, MF_Actual::class.java))
        }
    }
}
