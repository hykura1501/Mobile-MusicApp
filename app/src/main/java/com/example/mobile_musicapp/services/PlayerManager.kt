package com.example.mobile_musicapp.services

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel

// This object uses to manage the media player
object PlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var viewModel = PlayerBarViewModel()
    private val handler = Handler(Looper.getMainLooper())

    private val sleepHandler = Handler(Looper.getMainLooper())
    private var sleepRunnable : Runnable? = null

    fun startSleepCountdown(minutes: Int) {
        val sleepTimeMillis = minutes * 60 * 1000L // transfer to milliseconds
        if (sleepRunnable != null) {
            sleepHandler.removeCallbacks(sleepRunnable!!)
        }

        sleepRunnable = Runnable {
            // Enough time to sleep
            viewModel.updatePlayPause(false)
            pause()
        }

        sleepHandler.postDelayed(sleepRunnable!!, sleepTimeMillis)
    }

    fun cancelSleepCountdown() {
        if (sleepRunnable != null) {
            sleepHandler.removeCallbacks(sleepRunnable!!)
        }
        sleepRunnable = null
    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val currentPosition = it.currentPosition
                viewModel.updatePosition(currentPosition.toLong())

                if (it.isPlaying) {
                    handler.postDelayed(this, 100)
                }
            }
        }
    }

    fun initialize(viewModel: PlayerBarViewModel) {
        this.viewModel = viewModel
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setOnCompletionListener {
                    if (PlayerManager.viewModel.repeatMode.value == false) {
                        handler.postDelayed({ next()}, 500)
                    }
                    else{
                        // If repeat mode is on, keep playing the current song
                        handler.postDelayed({
                            seekTo(0)
                            play()
                        }, 500)
                    }
                }
            }
        }
    }

    fun prepare() {
        val song = Queue.getCurrentSong()
        if (song != null) {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(song.path)
            mediaPlayer?.prepare()
            mediaPlayer?.setOnPreparedListener {
                it.start()
                viewModel.updateWaiting(false)
                handler.post(updateSeekBarRunnable)
            }
        }

        MediaPlaybackService.createMediaStyleNotification()
    }

    fun play() {
        mediaPlayer?.start()
        handler.post(updateSeekBarRunnable)
    }

    fun pause() {
        mediaPlayer?.pause()
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    fun next() {
        handler.removeCallbacks(updateSeekBarRunnable)
        pause()
        mediaPlayer?.reset()
        Queue.nextSong()
        if (Queue.getCurrentSong() != null) {
            viewModel.updateSong(Queue.getCurrentSong()!!)
            prepare()
        }
        else{
            viewModel.updateWaiting(true)
        }
    }

    fun previous() {
        handler.removeCallbacks(updateSeekBarRunnable)
        pause()
        mediaPlayer?.reset()
        Queue.previousSong()
        viewModel.updateSong(Queue.getCurrentSong()!!)
        prepare()
    }

    private fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        viewModel.updatePosition(getCurrentPosition().toLong())
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}