package com.example.mobile_musicapp.fragment

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.singletons.Favorite
import com.example.mobile_musicapp.singletons.Queue
import com.google.android.material.snackbar.Snackbar
import com.example.mobile_musicapp.helpers.ImageHelper  // Import the ImageHelper class if used for loading images

class PlayMusic : Fragment() {

    private val args: PlayMusicArgs by navArgs()

    private lateinit var playButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var addToFavoritesButton: ImageButton
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var artist: TextView
    private lateinit var songName: TextView
    private lateinit var songImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the song from the arguments
        val song = args.song
        Queue.songs = mutableListOf(song)  // Set the song in the queue as a mutable list
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_play_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI(view)
        updateUI()  // Update UI with the selected song data
        prepareMusic()

        playButton.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                playMusic()
            } else {
                pauseMusic()
            }
        }

        nextButton.setOnClickListener {
            nextSong()
        }

        previousButton.setOnClickListener {
            previousSong()
        }

        addToFavoritesButton.setOnClickListener {
            val song = Queue.getCurrentSong()!!
            Favorite.addToFavorites(song)

            Snackbar.make(view, "Added to Favorites!", Snackbar.LENGTH_SHORT)
                .setAction("UNDO") {
                    Favorite.removeFromFavorites(song)
                    Toast.makeText(context, "Removed from Favorites!", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    private fun prepareMusic() {
        val song = Queue.getCurrentSong()!!
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        playButton.setImageResource(R.drawable.ic_pause_black)
        isPlaying = true

        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(song.url)
            mediaPlayer?.prepareAsync()

            mediaPlayer?.setOnPreparedListener {
                it.start()
                updateSeekBarAndTime()
            }

            mediaPlayer?.setOnCompletionListener {
                nextSong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playMusic() {
        mediaPlayer?.start()
        playButton.setImageResource(R.drawable.ic_pause_black)
        updateSeekBarAndTime()
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        playButton.setImageResource(R.drawable.ic_play_black)
    }

    private fun nextSong() {
        pauseMusic()
        mediaPlayer?.reset()
        Queue.nextSong()
        prepareMusic()
    }

    private fun previousSong() {
        pauseMusic()
        mediaPlayer?.reset()
        Queue.previousSong()
        prepareMusic()
    }

    @SuppressLint("DefaultLocale")
    private fun updateUI() {
        val song = Queue.getCurrentSong()!!
        // Update UI with song data
        artist.text = song.artistName
        songName.text = song.title
        val imageHelper = ImageHelper()
        imageHelper.loadImage(song.thumbnail, songImage) // Assuming ImageHelper is used for loading images
    }

    private fun updateSeekBarAndTime() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                mediaPlayer?.let {
                    seekBar.progress = it.currentPosition

                    val minutes = (it.currentPosition / 1000) / 60
                    val seconds = (it.currentPosition / 1000) % 60
                    val time = String.format("%02d:%02d", minutes, seconds)
                    currentTime.text = time

                    if (isPlaying) {
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        })
    }

    private fun connectUI(view: View) {
        playButton = view.findViewById(R.id.playButton)
        seekBar = view.findViewById(R.id.seekBar)
        currentTime = view.findViewById(R.id.currentTime)
        totalTime = view.findViewById(R.id.totalTime)
        nextButton = view.findViewById(R.id.nextButton)
        previousButton = view.findViewById(R.id.previousButton)
        addToFavoritesButton = view.findViewById(R.id.addToFavoritesButton)
        artist = view.findViewById(R.id.artist)
        songName = view.findViewById(R.id.songName)
        songImage = view.findViewById(R.id.imageView)

        seekBar.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
