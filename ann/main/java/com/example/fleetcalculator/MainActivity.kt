package com.example.fleetcalculator

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLoader = findViewById<ImageButton>(R.id.btn_loader)
        val btnHauler = findViewById<ImageButton>(R.id.btn_hauler)
        val btnJumlahHauler = findViewById<ImageButton>(R.id.btn_jumlah_hauler)
        val btnMatchingFactor = findViewById<ImageButton>(R.id.btn_matching_factor)
        val btnTopRight = findViewById<ImageButton>(R.id.btn_top_right)

        btnLoader.setOnClickListener {
            startActivity(Intent(this, LoaderActivity::class.java))
        }
        btnHauler.setOnClickListener {
            startActivity(Intent(this, Hauler::class.java))
        }
        btnJumlahHauler.setOnClickListener {
            startActivity(Intent(this, Jumlah_Hauler::class.java))
        }
        btnMatchingFactor.setOnClickListener {
            startActivity(Intent(this, Matching_Factor::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val btnTopRight = findViewById<ImageButton>(R.id.btn_top_right)

        val pref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = pref.getString("username", null)
        val nrp = pref.getString("nrp", null)

        // Hapus animasi dulu sebelum set gambar baru
        btnTopRight.clearAnimation()

        if (username.isNullOrEmpty() || nrp.isNullOrEmpty()) {
            // BELUM login: tombol jadi Sign In + animasi pulse
            btnTopRight.setImageResource(R.drawable.ic_signin)
            val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.btn_sign_in_pulse)
            btnTopRight.startAnimation(pulseAnim)
            btnTopRight.setOnClickListener {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        } else {
            // SUDAH login: tombol jadi Profile, tanpa animasi
            btnTopRight.setImageResource(R.drawable.ic_profile)
            btnTopRight.setOnClickListener {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }
    }
}