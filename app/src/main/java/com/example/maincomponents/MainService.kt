package com.example.maincomponents

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.maincomponents.core.util.NotificationUtils
import com.example.maincomponents.core.util.NotificationUtils.createNotificationChannel
import com.example.maincomponents.core.util.NotificationUtils.getForegroundServiceNotification


class MainService : Service() {
    private var musicPlayer: MediaPlayer? = null
    private var isPaused: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)

        try {
            musicPlayer = MediaPlayer.create(this, R.raw.quran)
            musicPlayer?.isLooping = true
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing MediaPlayer", Toast.LENGTH_LONG).show()
            stopSelf()
        }

        val filter = IntentFilter().apply {
            addAction("com.example.maincomponents.PAUSE_MUSIC")
            addAction("com.example.maincomponents.RESUME_MUSIC")
        }
        registerReceiver(musicControlReceiver, filter, RECEIVER_EXPORTED)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Music Service started by user.", Toast.LENGTH_LONG).show()
        val notification = getForegroundServiceNotification(this).build()
        startForeground(1, notification)
        musicPlayer!!.start()
        return START_STICKY
    }

    private fun pauseMusic() {
        if (musicPlayer!!.isPlaying) {
            musicPlayer!!.pause()
            isPaused = true
            Toast.makeText(this, "Music Paused", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resumeMusic() {
        if (isPaused) {
            musicPlayer!!.start()
            isPaused = false
            Toast.makeText(this, "Music Resumed", Toast.LENGTH_SHORT).show()
        }
    }

    private val musicControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.maincomponents.PAUSE_MUSIC" -> pauseMusic()
                "com.example.maincomponents.RESUME_MUSIC" -> resumeMusic()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer!!.stop()
        musicPlayer!!.release()
        Toast.makeText(this, "Music Service destroyed by user.", Toast.LENGTH_LONG).show()
        unregisterReceiver(musicControlReceiver)
        stopForeground(true)
    }
}
