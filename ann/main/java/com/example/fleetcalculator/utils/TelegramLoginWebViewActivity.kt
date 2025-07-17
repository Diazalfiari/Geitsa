package com.example.fleetcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.WebStorage

class TelegramLoginWebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    inner class JSBridge {
        @JavascriptInterface
        fun openDeepLink(url: String) {
            runOnUiThread {
                try {
                    Log.d("WebViewJSBridge", "JSBridge menerima deep link: $url")
                    if (url.startsWith("fleetcalc://telegramlogin")) {
                        val uri = Uri.parse(url)
                        val telegramId = uri.getQueryParameter("id") ?: ""
                        val telegramName = uri.getQueryParameter("name") ?: ""

                        Log.d("WebViewJSBridge", "Parsed id=$telegramId, name=$telegramName")

                        val intent = Intent(this@TelegramLoginWebViewActivity, InputNamaActivity::class.java)
                        intent.putExtra("telegramId", telegramId)
                        intent.putExtra("telegramName", telegramName)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("WebViewJSBridge", "Error on openDeepLink: ${e.message}", e)
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)
        webView.settings.javaScriptEnabled = true

        webView.addJavascriptInterface(JSBridge(), "AndroidBridge")

        val loginUrl = "https://graceful-cactus-d564db.netlify.app/"
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d("WebViewDeepLink", "URL: $url")
                // Deteksi hasil login Telegram dari hash URL (fallback jika JS gagal)
                if (url != null && url.contains("#tgAuthResult=")) {
                    handleTelegramAuthResultFromUrl(url)
                    return true
                }
                return false
            }
        }
        webView.loadUrl(loginUrl)
    }

    private fun handleTelegramAuthResultFromUrl(url: String) {
        try {
            val hashIndex = url.indexOf("#tgAuthResult=")
            if (hashIndex != -1) {
                val hashPart = url.substring(hashIndex + "#tgAuthResult=".length)
                val splitAmp = hashPart.split('&')[0] // handle multiple hash params

                val decodedBytes = Base64.decode(splitAmp, Base64.DEFAULT)
                val jsonStr = String(decodedBytes, Charsets.UTF_8)
                Log.d("WebViewDeepLink", "Decoded tgAuthResult: $jsonStr")

                val json = JSONObject(jsonStr)
                val telegramId = json.optString("id")
                val telegramName = json.optString("first_name")
                Log.d("WebViewDeepLink", "Parsed id=$telegramId, name=$telegramName (from tgAuthResult)")
                val intent = Intent(this@TelegramLoginWebViewActivity, InputNamaActivity::class.java)
                intent.putExtra("telegramId", telegramId)
                intent.putExtra("telegramName", telegramName)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            Log.e("WebViewDeepLink", "Error parsing tgAuthResult from url", e)
        }
    }

    override fun onDestroy() {
        // Otomatis clear cookies dan local storage WebView saat destroy
        try {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } catch (e: Exception) {
            Log.e("WebViewCleanup", "Error clearing cookies", e)
        }
        try {
            WebStorage.getInstance().deleteAllData()
        } catch (e: Exception) {
            Log.e("WebViewCleanup", "Error clearing WebStorage", e)
        }
        webView.apply {
            stopLoading()
            clearHistory()
            clearCache(true)
            removeAllViews()
            destroy()
        }
        super.onDestroy()
    }
}