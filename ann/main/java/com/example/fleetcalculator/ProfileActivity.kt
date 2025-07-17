package com.example.fleetcalculator

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1001

    // Form Sign In & Sign Up
    private lateinit var formSignIn: LinearLayout
    private lateinit var formSignUp: LinearLayout
    private lateinit var profileLayout: LinearLayout

    private lateinit var etSignInUsername: EditText
    private lateinit var etSignInNRP: EditText
    private lateinit var btnSignIn: Button
    private lateinit var btnGoToSignUp: Button

    private lateinit var etSignUpUsername: EditText
    private lateinit var etSignUpNRP: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnGoToSignIn: Button

    // Profile views
    private lateinit var imgProfile: ImageView
    private lateinit var btnEditProfilePic: FloatingActionButton
    private lateinit var tvNamaUser: TextView
    private lateinit var tvNRP: TextView
    private lateinit var btnTelegram: Button
    private lateinit var btnLogout: Button
    private lateinit var tvAppVersion: TextView

    // Firebase
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private var currentUsername: String? = null
    private var currentNrp: String? = null
    private var currentDocId: String? = null // Firestore doc reference for update

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Find views
        formSignIn = findViewById(R.id.formSignIn)
        formSignUp = findViewById(R.id.formSignUp)
        profileLayout = findViewById(R.id.profileLayout)

        etSignInUsername = findViewById(R.id.etSignInUsername)
        etSignInNRP = findViewById(R.id.etSignInNRP)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp)

        etSignUpUsername = findViewById(R.id.etSignUpUsername)
        etSignUpNRP = findViewById(R.id.etSignUpNRP)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnGoToSignIn = findViewById(R.id.btnGoToSignIn)

        imgProfile = findViewById(R.id.imgProfile)
        btnEditProfilePic = findViewById(R.id.btnEditProfilePic)
        tvNamaUser = findViewById(R.id.tvNamaUser)
        tvNRP = findViewById(R.id.tvNRP)
        btnTelegram = findViewById(R.id.btnTelegram)
        btnLogout = findViewById(R.id.btnLogout)
        tvAppVersion = findViewById(R.id.tvAppVersion)

        // Pastikan warna teks dan hint username/NRP input selalu hitam
        etSignInUsername.setTextColor(Color.BLACK)
        etSignInUsername.setHintTextColor(Color.BLACK)
        etSignUpUsername.setTextColor(Color.BLACK)
        etSignUpUsername.setHintTextColor(Color.BLACK)
        etSignInNRP.setTextColor(Color.BLACK)
        etSignInNRP.setHintTextColor(Color.BLACK)
        etSignUpNRP.setTextColor(Color.BLACK)
        etSignUpNRP.setHintTextColor(Color.BLACK)

        // Pastikan warna TextView username dan NRP di profil juga selalu hitam
        tvNamaUser.setTextColor(Color.BLACK)
        tvNRP.setTextColor(Color.BLACK)

        // Default: tampilkan Sign In
        showSignIn()

        // --- SIGN IN ---
        btnSignIn.setOnClickListener {
            val username = etSignInUsername.text.toString().trim()
            val nrp = etSignInNRP.text.toString().trim()
            if (username.isEmpty() || nrp.isEmpty()) {
                Toast.makeText(this, "Username dan NRP wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            firestore.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("nrp", nrp)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val doc = snapshot.documents.first()
                        currentDocId = doc.id
                        currentUsername = username
                        currentNrp = nrp
                        prefs.edit()
                            .putString("username", username)
                            .putString("nrp", nrp)
                            .putString("photoPath", doc.getString("photoPath") ?: "")
                            .apply()
                        showProfile(username, nrp, doc.getString("photoPath"))
                    } else {
                        Toast.makeText(this, "Username atau NRP salah / belum terdaftar!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // --- SIGN UP ---
        btnSignUp.setOnClickListener {
            val username = etSignUpUsername.text.toString().trim()
            val nrp = etSignUpNRP.text.toString().trim()
            if (username.isEmpty() || nrp.isEmpty()) {
                Toast.makeText(this, "Username dan NRP wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Cek dulu apakah sudah terdaftar
            firestore.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("nrp", nrp)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        // Belum ada, simpan data
                        val userMap = hashMapOf(
                            "username" to username,
                            "nrp" to nrp,
                            "photoPath" to ""
                        )
                        firestore.collection("users").add(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Berhasil daftar! Silakan login.", Toast.LENGTH_SHORT).show()
                                showSignIn()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal daftar: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Username & NRP sudah terdaftar!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // --- Switch antar form ---
        btnGoToSignUp.setOnClickListener { showSignUp() }
        btnGoToSignIn.setOnClickListener { showSignIn() }

        // --- Profile actions ---
        btnEditProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnTelegram.setOnClickListener {
            val groupUrl = "https://t.me/Master_Fleet"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(groupUrl))
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            currentUsername = null
            currentNrp = null
            currentDocId = null
            showSignIn()
        }

        // Jika sudah login, langsung tampil profile
        val savedUsername = prefs.getString("username", null)
        val savedNrp = prefs.getString("nrp", null)
        val savedPhotoPath = prefs.getString("photoPath", null)
        if (!savedUsername.isNullOrEmpty() && !savedNrp.isNullOrEmpty()) {
            currentUsername = savedUsername
            currentNrp = savedNrp
            showProfile(savedUsername, savedNrp, savedPhotoPath)
            // Update docId supaya bisa update photo saat ganti profile pic
            firestore.collection("users")
                .whereEqualTo("username", savedUsername)
                .whereEqualTo("nrp", savedNrp)
                .get()
                .addOnSuccessListener { snapshot ->
                    val doc = snapshot.documents.firstOrNull()
                    if (doc != null) currentDocId = doc.id
                }
        }
    }

    private fun showSignIn() {
        formSignIn.visibility = LinearLayout.VISIBLE
        formSignUp.visibility = LinearLayout.GONE
        profileLayout.visibility = LinearLayout.GONE
    }

    private fun showSignUp() {
        formSignUp.visibility = LinearLayout.VISIBLE
        formSignIn.visibility = LinearLayout.GONE
        profileLayout.visibility = LinearLayout.GONE
    }

    private fun showProfile(username: String, nrp: String, photoPath: String?) {
        formSignIn.visibility = LinearLayout.GONE
        formSignUp.visibility = LinearLayout.GONE
        profileLayout.visibility = LinearLayout.VISIBLE

        tvNamaUser.text = username
        tvNRP.text = "NRP: $nrp"

        // Pastikan warna tetap hitam saat update profile
        tvNamaUser.setTextColor(Color.BLACK)
        tvNRP.setTextColor(Color.BLACK)

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        tvAppVersion.text = "Versi $versionName"

        if (!photoPath.isNullOrEmpty() && File(photoPath).exists()) {
            Glide.with(this)
                .load(File(photoPath))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(imgProfile)
        } else {
            imgProfile.setImageResource(R.drawable.ic_profile)
        }
    }

    // --- Perbaikan utama: Simpan gambar di HP, bukan upload ke cloud ---
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri = data.data!!
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val username = currentUsername ?: prefs.getString("username", null)
            val nrp = currentNrp ?: prefs.getString("nrp", null)
            val docId = currentDocId

            if (username.isNullOrEmpty() || nrp.isNullOrEmpty()) return

            // Simpan gambar ke internal storage HP
            val fileName = "profile_${username}_${nrp}.jpg"
            val file = File(filesDir, fileName)
            try {
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Gagal menyimpan file: ${e.message}", Toast.LENGTH_SHORT).show()
                return
            }

            // Update path di SharedPreferences
            prefs.edit().putString("photoPath", file.absolutePath).apply()
            // Update path di Firestore (optional, agar sync antar device user sendiri)
            if (docId != null) {
                firestore.collection("users").document(docId)
                    .update("photoPath", file.absolutePath)
                    .addOnSuccessListener {
                        showProfile(username, nrp, file.absolutePath)
                        Toast.makeText(this, "Foto profil berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal update Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                showProfile(username, nrp, file.absolutePath)
                Toast.makeText(this, "Foto profil berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}