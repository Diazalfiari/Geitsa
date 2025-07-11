package com.example.fleetcalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.getBooleanExtra("from_splash", false).not()) {
            val splashIntent = Intent(this, splash_screen::class.java)
            splashIntent.putExtra("from_main", true)
            startActivity(splashIntent)
            finish()
            return
        }

        val btnLoader = findViewById<ImageButton>(R.id.btn_loader)
        val btnHauler = findViewById<ImageButton>(R.id.btn_hauler)
        val btnJumlahHauler = findViewById<ImageButton>(R.id.btn_jumlah_hauler)
        val btnMatchingFactor = findViewById<ImageButton>(R.id.btn_matching_factor)

        btnLoader.setOnClickListener {
            val intent = Intent(this, LoaderActivity::class.java)
            startActivity(intent)
        }

        btnHauler.setOnClickListener {
            val intent = Intent(this, Hauler::class.java)
            startActivity(intent)
        }

        btnJumlahHauler.setOnClickListener {
            val intent = Intent(this, Jumlah_Hauler::class.java)
            startActivity(intent)
        }

        btnMatchingFactor.setOnClickListener {
            val intent = Intent(this, Matching_Factor::class.java)
            startActivity(intent)
        }
    }
}
