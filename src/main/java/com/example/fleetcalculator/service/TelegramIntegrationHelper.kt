package com.example.fleetcalculator.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fleetcalculator.data.LoginRepository
import com.example.fleetcalculator.data.model.LoggedInUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Helper class to integrate Telegram messaging across activities
 */
class TelegramIntegrationHelper(private val context: Context) {

    companion object {
        private const val TAG = "TelegramIntegration"
    }

    private val telegramService = TelegramService(context)
    private val messageComposer = MessageComposer()

    /**
     * Check if Telegram integration is enabled
     */
    fun isTelegramEnabled(): Boolean {
        val sharedPreferences = context.getSharedPreferences("telegram_settings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("telegram_enabled", false)
    }

    /**
     * Get current logged in user
     */
    private fun getCurrentUser(): LoggedInUser? {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentUserId = sharedPreferences.getString("current_user", null)
        return if (currentUserId != null) {
            val displayName = sharedPreferences.getString("display_name_$currentUserId", currentUserId) ?: currentUserId
            LoggedInUser(currentUserId, displayName)
        } else {
            null
        }
    }

    /**
     * Send Hauler calculation result to Telegram
     */
    fun sendHaulerCalculation(
        scope: CoroutineScope,
        vesselCapacity: Double,
        jarak: Double,
        kecepatan: Double,
        travelTime: Double,
        cycleTime: Double,
        swellFactor: Double,
        efisiensiKerja: Double,
        productivity: Double,
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        if (!isTelegramEnabled()) {
            Log.d(TAG, "Telegram integration is disabled")
            return
        }

        val user = getCurrentUser()
        if (user == null) {
            Log.w(TAG, "No user logged in")
            onResult?.invoke(false, "No user logged in")
            return
        }

        val message = messageComposer.composeHaulerMessage(
            user, vesselCapacity, jarak, kecepatan, travelTime, cycleTime, swellFactor, efisiensiKerja, productivity
        )

        sendMessage(scope, message, onResult)
    }

    /**
     * Send Loader calculation result to Telegram
     */
    fun sendLoaderCalculation(
        scope: CoroutineScope,
        fleet: String,
        unit: String,
        bucketCapacity: Double,
        bucketFactor: Double,
        cycleTime: Double,
        efficiency: Double,
        productivity: Double,
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        if (!isTelegramEnabled()) {
            Log.d(TAG, "Telegram integration is disabled")
            return
        }

        val user = getCurrentUser()
        if (user == null) {
            Log.w(TAG, "No user logged in")
            onResult?.invoke(false, "No user logged in")
            return
        }

        val message = messageComposer.composeLoaderMessage(
            user, fleet, unit, bucketCapacity, bucketFactor, cycleTime, efficiency, productivity
        )

        sendMessage(scope, message, onResult)
    }

    /**
     * Send generic calculation result to Telegram
     */
    fun sendGenericCalculation(
        scope: CoroutineScope,
        calculationType: String,
        result: String,
        additionalInfo: String = "",
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        if (!isTelegramEnabled()) {
            Log.d(TAG, "Telegram integration is disabled")
            return
        }

        val user = getCurrentUser()
        if (user == null) {
            Log.w(TAG, "No user logged in")
            onResult?.invoke(false, "No user logged in")
            return
        }

        val message = messageComposer.composeGenericMessage(user, calculationType, result, additionalInfo)
        sendMessage(scope, message, onResult)
    }

    /**
     * Send message to Telegram
     */
    private fun sendMessage(
        scope: CoroutineScope,
        message: String,
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        val sharedPreferences = context.getSharedPreferences("telegram_settings", Context.MODE_PRIVATE)
        val botToken = sharedPreferences.getString("bot_token", "") ?: ""
        val chatId = sharedPreferences.getString("chat_id", "") ?: ""

        if (botToken.isEmpty() || chatId.isEmpty()) {
            Log.w(TAG, "Bot token or chat ID is empty")
            onResult?.invoke(false, "Telegram not configured")
            return
        }

        scope.launch {
            val result = telegramService.sendMessage(botToken, chatId, message)
            
            if (result.isSuccess) {
                Log.d(TAG, "Message sent successfully")
                onResult?.invoke(true, "Message sent to Telegram")
            } else {
                Log.e(TAG, "Failed to send message: ${result.exceptionOrNull()?.message}")
                onResult?.invoke(false, "Failed to send: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    /**
     * Show toast message about Telegram status
     */
    fun showTelegramStatus(success: Boolean, message: String) {
        val displayMessage = if (success) {
            "📤 $message"
        } else {
            "❌ $message"
        }
        
        Toast.makeText(context, displayMessage, Toast.LENGTH_SHORT).show()
    }
}