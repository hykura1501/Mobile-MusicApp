package com.example.mobile_musicapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaSessionCompat
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.singletons.Queue

class MediaPlaybackService : Service() {

    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        Log.d("MediaPlaybackService", "Service created")
        instance = this
        mediaSession = MediaSessionCompat(this, "MediaPlaybackService")
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                Log.d("MediaPlaybackService", "onPlay called")
                createMediaStyleNotification()
            }

            override fun onPause() {
                super.onPause()
                Log.d("MediaPlaybackService", "onPause called")
                stopForeground(false)
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MediaPlaybackService", "Service started")
        createMediaStyleNotification()
        return START_STICKY
    }

    companion object {
        private var instance: MediaPlaybackService? = null

        fun createMediaStyleNotification() {
            val service = instance ?: return
            Log.d("MediaPlaybackService", "Creating MediaStyle notification")
            val currentSong = Queue.getCurrentSong()
            val builder = NotificationCompat.Builder(service, "media_playback_channel")
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(currentSong?.title ?: "Unknown Title")
                .setContentText(currentSong?.artistName ?: "Unknown Artist")
                .setLargeIcon(BitmapFactory.decodeResource(service.resources, R.drawable.album_art))
                .setStyle(
                    MediaStyle()
                        .setMediaSession(service.mediaSession.sessionToken)
                        .setShowActionsInCompactView(0, 1, 2)
                )
                .addAction(R.drawable.ic_previous_music, "Previous", getPendingIntent("ACTION_PREVIOUS"))
                .addAction(R.drawable.ic_pause_black, "Pause", getPendingIntent("ACTION_PAUSE"))
                .addAction(R.drawable.ic_next_music, "Next", getPendingIntent("ACTION_NEXT"))

            service.startForeground(1, builder.build())
        }

        private fun getPendingIntent(action: String): PendingIntent {
            val service = instance ?: return PendingIntent.getService(null, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val intent = Intent(service, MediaPlaybackService::class.java).apply {
                this.action = action
            }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getService(service, 0, intent, flags)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}