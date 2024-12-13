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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.singletons.Favorite
import com.example.mobile_musicapp.singletons.Queue
import com.google.android.material.snackbar.Snackbar
import com.example.mobile_musicapp.helpers.ImageHelper
import com.example.mobile_musicapp.services.FavoriteSongDao
import com.example.mobile_musicapp.viewModels.FavoritesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayMusic : Fragment() {

    private val args: PlayMusicArgs by navArgs()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()

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
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the list of songs and selected index from the arguments
        val songListWithIndex = args.songListWithIndex
        Queue.songs = songListWithIndex.songs.toMutableList()
        Queue.currentSongIndex = songListWithIndex.selectedIndex
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
            CoroutineScope(Dispatchers.Main).launch {
                val result = withContext(Dispatchers.IO) {
                    FavoriteSongDao.addOrRemoveFavoriteSong(song._id)
                }
                result.fold(
                    onSuccess = { message ->
                        if (isFavorite) {
                            Favorite.removeFromFavorites(song)
                            favoritesViewModel.removeFavorite(song)
                        } else {
                            Favorite.addToFavorites(song)
                            favoritesViewModel.addFavorite(song)
                        }
                        updateFavoriteIcon()
                        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
                    },
                    onFailure = { throwable ->
                        Toast.makeText(context, throwable.message ?: "Failed to update Favorites!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Check and update the favorite icon on load
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        val song = Queue.getCurrentSong()!!
        isFavorite = Favorite.getPlaylist().any { it._id == song._id }
        if (isFavorite) {
            addToFavoritesButton.setImageResource(R.drawable.ic_heart_filled)
        } else {
            addToFavoritesButton.setImageResource(R.drawable.ic_heart)
        }
    }

    private fun prepareMusic() {
        val song = Queue.getCurrentSong()!!
        println("Preparing music with URL: ${song.path}")  // Log the path

        if (song.path.isEmpty()) {
            // Handle empty path
            Toast.makeText(context, "Error: Song path is empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        playButton.setImageResource(R.drawable.ic_pause_black)
        isPlaying = true

        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(song.path)
            mediaPlayer?.prepareAsync()

            mediaPlayer?.setOnPreparedListener {
                it.start()
                updateUI()
                updateSeekBarAndTime()
            }

            mediaPlayer?.setOnCompletionListener {
                nextSong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Log the error
            println("Error preparing MediaPlayer: ${e.message}")
            // Show a message to the user
            Toast.makeText(context, "Error playing music: ${e.message}", Toast.LENGTH_SHORT).show()
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
        updateUI()  // Update UI with the new song data
        prepareMusic()
    }

    private fun previousSong() {
        pauseMusic()
        mediaPlayer?.reset()
        Queue.previousSong()
        updateUI()  // Update UI with the previous song data
        prepareMusic()
    }

    @SuppressLint("DefaultLocale")
    private fun updateUI() {
        val song = Queue.getCurrentSong()!!
        artist.text = song.artistName
        songName.text = song.title
        val imageHelper = ImageHelper()
        imageHelper.loadImage(song.thumbnail, songImage) // Assuming ImageHelper is used for loading images

        mediaPlayer?.let {
            seekBar.max = it.duration
            val minutes = (it.duration / 1000) / 60
            val seconds = (it.duration / 1000) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            totalTime.text = time
        }

        // Update the favorite icon when the song changes
        updateFavoriteIcon()
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
}
