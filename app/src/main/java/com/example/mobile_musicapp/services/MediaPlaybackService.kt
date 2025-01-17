package com.example.mobile_musicapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.singletons.Queue
import com.bumptech.glide.request.transition.Transition

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
                PlayerManager.play()
                createMediaStyleNotification()
            }

            override fun onPause() {
                super.onPause()
                PlayerManager.pause()
                createMediaStyleNotification()
                stopForeground(false)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                PlayerManager.next()
                createMediaStyleNotification(true)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                PlayerManager.previous()
                createMediaStyleNotification(true)
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MediaPlaybackService", "Service started")
        val action = intent?.action
        when (action) {
            "ACTION_PLAY" -> {
                mediaSession.controller.transportControls.play()
            }
            "ACTION_PAUSE" -> {
                mediaSession.controller.transportControls.pause()
            }
            "ACTION_NEXT" -> {
                mediaSession.controller.transportControls.skipToNext()
                PlayerManager.play() // Ensure the song is played
            }
            "ACTION_PREVIOUS" -> {
                mediaSession.controller.transportControls.skipToPrevious()
                PlayerManager.play() // Ensure the song is played
            }
        }
        createMediaStyleNotification(action == "ACTION_NEXT" || action == "ACTION_PREVIOUS")
        return START_STICKY
    }

    companion object {
        private var instance: MediaPlaybackService? = null

        fun createMediaStyleNotification(forcePause: Boolean = false) {
            val service = instance ?: return
            val currentSong = Queue.getCurrentSong()
            val thumbnailUrl = currentSong?.thumbnail

            val isPlaying = PlayerManager.isPlaying() || forcePause

            val playPauseAction = if (isPlaying) {
                NotificationCompat.Action(
                    R.drawable.ic_pause_black, "Pause",
                    getPendingIntent("ACTION_PAUSE")
                )
            } else {
                NotificationCompat.Action(
                    R.drawable.ic_play_black, "Play",
                    getPendingIntent("ACTION_PLAY")
                )
            }

            val builder = NotificationCompat.Builder(service, "media_playback_channel")
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(currentSong?.title ?: "Unknown Title")
                .setContentText(currentSong?.artistName ?: "Unknown Artist")
                .setOngoing(isPlaying)
                .setStyle(
                    MediaStyle()
                        .setMediaSession(service.mediaSession.sessionToken)
                        .setShowActionsInCompactView(0, 1, 2)
                )
                .addAction(R.drawable.ic_previous_music, "Previous", getPendingIntent("ACTION_PREVIOUS"))
                .addAction(playPauseAction)
                .addAction(R.drawable.ic_next_music, "Next", getPendingIntent("ACTION_NEXT"))

            if (thumbnailUrl != null) {
                Glide.with(service)
                    .asBitmap()
                    .load(thumbnailUrl)
                    .override(4096, 4096) // Load the image with higher resolution
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .fitCenter()
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            builder.setLargeIcon(resource)
                            service.startForeground(1, builder.build())
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // Handle placeholder if needed
                        }
                    })
            } else {
                service.startForeground(1, builder.build())
            }
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MediaPlaybackService", "Service destroyed")
        stopForeground(true)
        mediaSession.release()
    }
}