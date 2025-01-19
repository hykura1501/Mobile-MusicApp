package com.example.mobile_musicapp.services

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
    private const val MAX_COUNT = 6
    private var count = 0
    private val ad = Song(
        title = "Ad",
        artistName = "Spotify",
        path = "http://res.cloudinary.com/dw0acvowr/video/upload/v1737279106/oro8x9wtmioykcdtqqwg.mp3",
        duration = 30,
        thumbnail = "https://res.cloudinary.com/dnqege3qj/image/upload/v1737279956/Screenshot_2025-01-19_164441_ecsnbj.png",
        album = "Spotify Ad",
        artistId = "spotify"
    )
    private var currentSong: Song? = null

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

    fun getCurrentSong(): Song {
        return currentSong ?: Queue.getCurrentSong()!!
    }

    fun prepare() {
        var song = Queue.getCurrentSong()
        if (count >= MAX_COUNT) {
            count = 0

            // There is a promotion ad if user is not premium or premium expired
            if ((!UserManager.isPremium || UserManager.premiumExpiredAt.time < System.currentTimeMillis())) {
                song = ad
            }
        }

        if (song != null) {
            currentSong = song
            viewModel.updateSong(song)
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
        // reset media player
        handler.removeCallbacks(updateSeekBarRunnable)
        pause()
        mediaPlayer?.reset()
        count++
        if (count < MAX_COUNT) {
            Queue.nextSong()
        }
        prepare()
    }

    fun previous() {
        handler.removeCallbacks(updateSeekBarRunnable)
        pause()
        mediaPlayer?.reset()
        Queue.previousSong()
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