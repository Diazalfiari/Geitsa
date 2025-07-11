package com.example.fleetcalculator.service

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Service to handle Telegram Bot API communication
 */
class TelegramService(private val context: Context) {

    companion object {
        private const val TAG = "TelegramService"
        private const val TELEGRAM_API_BASE_URL = "https://api.telegram.org/bot"
    }

    /**
     * Send message to Telegram chat
     */
    suspend fun sendMessage(
        botToken: String,
        chatId: String,
        message: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (botToken.isBlank() || chatId.isBlank()) {
                return@withContext Result.failure(
                    IllegalArgumentException("Bot token and chat ID are required")
                )
            }

            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val urlString = "$TELEGRAM_API_BASE_URL$botToken/sendMessage"
            val postData = "chat_id=$chatId&text=$encodedMessage&parse_mode=HTML"

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                setRequestProperty("Content-Length", postData.length.toString())
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }

            connection.outputStream.use { outputStream ->
                outputStream.write(postData.toByteArray(StandardCharsets.UTF_8))
                outputStream.flush()
            }

            val responseCode = connection.responseCode
            val responseMessage = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { inputStream ->
                    inputStream.bufferedReader().use { reader ->
                        reader.readText()
                    }
                }
            } else {
                connection.errorStream?.use { errorStream ->
                    errorStream.bufferedReader().use { reader ->
                        reader.readText()
                    }
                } ?: "HTTP Error $responseCode"
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "Message sent successfully: $responseMessage")
                Result.success(responseMessage)
            } else {
                Log.e(TAG, "Failed to send message: $responseCode - $responseMessage")
                Result.failure(Exception("Failed to send message: $responseCode - $responseMessage"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error sending message to Telegram", e)
            Result.failure(e)
        }
    }

    /**
     * Test the Telegram configuration by sending a simple test message
     */
    suspend fun testConfiguration(botToken: String, chatId: String): Result<String> {
        val testMessage = "🔧 Test message from Geitsa Fleet Calculator\n\nYour Telegram integration is working correctly!"
        return sendMessage(botToken, chatId, testMessage)
    }
}