package com.example.fleetcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fleetcalculator.ui.login.LoginViewModel
import com.example.fleetcalculator.ui.login.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvStatus: TextView
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Check if user is already logged in
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val currentUser = sharedPreferences.getString("current_user", null)
        if (currentUser != null) {
            // User is already logged in, go to main activity
            startMainActivity()
            return
        }

        initializeViews()
        setupViewModel()
        setupListeners()
    }

    private fun initializeViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvStatus = findViewById(R.id.tvStatus)
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(this))
            .get(LoginViewModel::class.java)

        loginViewModel.loginResult.observe(this) { loginResult ->
            loginResult?.let {
                if (it.success != null) {
                    // Login successful
                    tvStatus.text = "Login successful! Welcome ${it.success.displayName}"
                    tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
                    startMainActivity()
                } else {
                    // Login failed
                    tvStatus.text = "Login failed. Please check your credentials."
                    tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
                }
                btnLogin.isEnabled = true
            }
        }

        loginViewModel.loginFormState.observe(this) { loginFormState ->
            loginFormState?.let {
                btnLogin.isEnabled = it.isDataValid
                
                if (it.usernameError != null) {
                    etUsername.error = getString(it.usernameError)
                }
                if (it.passwordError != null) {
                    etPassword.error = getString(it.passwordError)
                }
            }
        }
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                tvStatus.text = "Please enter both username and password"
                tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
                return@setOnClickListener
            }
            
            btnLogin.isEnabled = false
            tvStatus.text = "Logging in..."
            tvStatus.setTextColor(getColor(android.R.color.holo_blue_dark))
            
            loginViewModel.login(username, password)
        }

        // Enable login button when both fields have text
        val textWatcher = object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val username = etUsername.text.toString().trim()
                val password = etPassword.text.toString().trim()
                loginViewModel.loginDataChanged(username, password)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        
        etUsername.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("from_splash", true)
        startActivity(intent)
        finish()
    }
}