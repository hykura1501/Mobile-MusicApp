package com.example.mobile_musicapp.services

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import okhttp3.internal.wait

// This object uses to manage the media player
object PlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var viewModel = PlayerBarViewModel()
    private val handler = Handler(Looper.getMainLooper())

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val currentPosition = it.currentPosition
                viewModel.updatePosition(currentPosition)

                if (it.isPlaying) {
                    handler.postDelayed(this, 1000)
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
    }

    fun play() {
        mediaPlayer?.start()
        handler.post(updateSeekBarRunnable)
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    fun next() {
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
        pause()
        mediaPlayer?.reset()
        Queue.previousSong()
        viewModel.updateSong(Queue.getCurrentSong()!!)
        prepare()
    }

    private fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        viewModel.updatePosition(getCurrentPosition())
    }
}