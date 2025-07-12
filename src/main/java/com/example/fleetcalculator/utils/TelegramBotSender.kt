package com.example.fleetcalculator.utils

import android.util.Log
import okhttp3.*
import java.io.IOException

object TelegramBotSender {
    private const val BOT_TOKEN = "7669887482:AAHX_9IPBWl1diWtSY8gTac0zap7jUVR2qY"
    // Hilangkan chat_id default, supaya tidak salah kirim ke grup!

    // Kirim ke chatId manapun (userId atau groupId)
    fun sendToTelegram(chatId: String, message: String) {
        val url = "https://api.telegram.org/bot$BOT_TOKEN/sendMessage"
        val client = OkHttpClient()
        val body = FormBody.Builder()
            .add("chat_id", chatId)
            .add("text", message)
            .build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TelegramBotSender", "Failed to send Telegram message", e)
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("TelegramBotSender", "Telegram response not successful: ${response.code} ${response.message} ${response.body?.string()}")
                } else {
                    Log.i("TelegramBotSender", "Telegram sent: ${response.body?.string()}")
                }
                response.close()
            }
        })
    }
}