package com.example.mobile_musicapp.services

import android.media.MediaPlayer
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel

object PlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var viewModel = PlayerBarViewModel()

    init {
        mediaPlayer?.setOnCompletionListener {
            if (viewModel.repeatMode.value == false) {
                Queue.nextSong()
                viewModel.updateSong(Queue.getCurrentSong()!!)
            }
            else{
                // TODO: Repeat the current song
            }
        }
    }

    fun initialize() {
        mediaPlayer = MediaPlayer()
    }

    fun prepare() {
        val song = Queue.getCurrentSong()!!
        mediaPlayer?.setDataSource(song.path)
        mediaPlayer?.prepare()
        mediaPlayer?.setOnPreparedListener {
            it.start()
        }
    }

    fun play() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }
}