package com.example.fleetcalculator

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fleetcalculator.service.TelegramService
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var etBotToken: EditText
    private lateinit var etChatId: EditText
    private lateinit var switchTelegramEnabled: Switch
    private lateinit var btnTestConnection: Button
    private lateinit var btnSave: Button
    private lateinit var tvStatus: TextView

    private lateinit var telegramService: TelegramService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeViews()
        telegramService = TelegramService(this)
        loadSettings()
        setupListeners()
    }

    private fun initializeViews() {
        etBotToken = findViewById(R.id.etBotToken)
        etChatId = findViewById(R.id.etChatId)
        switchTelegramEnabled = findViewById(R.id.switchTelegramEnabled)
        btnTestConnection = findViewById(R.id.btnTestConnection)
        btnSave = findViewById(R.id.btnSave)
        tvStatus = findViewById(R.id.tvStatus)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
    }

    private fun setupListeners() {
        switchTelegramEnabled.setOnCheckedChangeListener { _, isChecked ->
            enableTelegramFields(isChecked)
        }

        btnTestConnection.setOnClickListener {
            testTelegramConnection()
        }

        btnSave.setOnClickListener {
            saveSettings()
        }
    }

    private fun enableTelegramFields(enabled: Boolean) {
        etBotToken.isEnabled = enabled
        etChatId.isEnabled = enabled
        btnTestConnection.isEnabled = enabled
    }

    private fun loadSettings() {
        val sharedPreferences = getSharedPreferences("telegram_settings", MODE_PRIVATE)
        
        val isEnabled = sharedPreferences.getBoolean("telegram_enabled", false)
        val botToken = sharedPreferences.getString("bot_token", "") ?: ""
        val chatId = sharedPreferences.getString("chat_id", "") ?: ""

        switchTelegramEnabled.isChecked = isEnabled
        etBotToken.setText(botToken)
        etChatId.setText(chatId)
        
        enableTelegramFields(isEnabled)
    }

    private fun saveSettings() {
        val sharedPreferences = getSharedPreferences("telegram_settings", MODE_PRIVATE)
        
        with(sharedPreferences.edit()) {
            putBoolean("telegram_enabled", switchTelegramEnabled.isChecked)
            putString("bot_token", etBotToken.text.toString().trim())
            putString("chat_id", etChatId.text.toString().trim())
            apply()
        }

        tvStatus.text = "Settings saved successfully!"
        tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
    }

    private fun testTelegramConnection() {
        val botToken = etBotToken.text.toString().trim()
        val chatId = etChatId.text.toString().trim()

        if (botToken.isEmpty() || chatId.isEmpty()) {
            tvStatus.text = "Please enter both Bot Token and Chat ID"
            tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            return
        }

        tvStatus.text = "Testing connection..."
        tvStatus.setTextColor(getColor(android.R.color.holo_blue_dark))
        
        btnTestConnection.isEnabled = false

        lifecycleScope.launch {
            val result = telegramService.testConfiguration(botToken, chatId)
            
            runOnUiThread {
                btnTestConnection.isEnabled = true
                
                if (result.isSuccess) {
                    tvStatus.text = "✅ Connection successful! Test message sent."
                    tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
                } else {
                    tvStatus.text = "❌ Connection failed: ${result.exceptionOrNull()?.message}"
                    tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }
        }
    }
}