package com.example.mobile_musicapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.singletons.Favorite
import com.example.mobile_musicapp.singletons.Queue
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobile_musicapp.helpers.BackgroundHelper
import com.example.mobile_musicapp.models.Option
import com.example.mobile_musicapp.services.FavoriteSongDao
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.viewModels.FavoritesViewModel
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("RemoveExplicitTypeArguments")
class PlayMusicFragment : Fragment() {

    private lateinit var playButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var addToFavoritesButton: ImageButton
    private lateinit var minimizeButton: ImageButton
    private lateinit var shuffleButton: ImageButton
    private lateinit var optionsButton: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var artist: TextView
    private lateinit var songName: TextView
    private lateinit var album: TextView
    private lateinit var songThumbnail: ImageView
    private lateinit var playerBackground: ConstraintLayout
    private var isFavorite: Boolean = false

    private val args: PlayMusicFragmentArgs by navArgs()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the list of songs and selected index from the arguments
        val songListWithIndex = args.songListWithIndex
        Queue.openPlaylist(songListWithIndex.songs.toMutableList(), songListWithIndex.selectedIndex)
        val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        viewModel.updateSong(Queue.getCurrentSong()!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_music, container, false)
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect UI components
        connectUI(view)
        PlayerManager.prepare()
        updateUI()

        val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            seekBar.progress = position

            val minutes = (position / 1000) / 60
            val seconds = (position / 1000) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            currentTime.text = time
        }

        viewModel.currentSong.observe(viewLifecycleOwner) {
            updateUI()
        }

        // Play/Pause button handling
        playButton.setOnClickListener {
            viewModel.togglePlayPause()

            if (viewModel.isPlaying.value == true) {
                PlayerManager.play()
                playButton.setImageResource(R.drawable.ic_pause_black)
            } else {
                PlayerManager.pause()
                playButton.setImageResource(R.drawable.ic_play_black)
            }
        }

        nextButton.setOnClickListener {
            PlayerManager.next()
        }

        previousButton.setOnClickListener {
            PlayerManager.previous()
        }

        minimizeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        shuffleButton.setOnClickListener {
            viewModel.toggleShuffleMode()
            if (viewModel.shuffleMode.value == true) {
                shuffleButton.setImageResource(R.drawable.ic_shuffle_filled)
            } else {
                shuffleButton.setImageResource(R.drawable.ic_shuffle)
            }
        }

        optionsButton.setOnClickListener {
            val options = listOf(
                Option.ADD_TO_PLAYLIST.title,
                Option.SHARE.title,
                Option.REPEAT.title,
            )
            val actionDialogFragment = MenuOptionFragment.newInstance(options) { handleShare() }
            actionDialogFragment.show(parentFragmentManager, "MenuOptionFragment")
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

        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        PlayerManager.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    PlayerManager.pause()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    PlayerManager.play()
                }
            }
        )

        // Check and update the favorite icon on load
        updateFavoriteIcon()

        // Set background using BackgroundHelper
        val song = Queue.getCurrentSong()!!
        BackgroundHelper.updateBackgroundWithImageColor(requireContext(), song.thumbnail, playerBackground, cornerRadius = 0f)
    }

    @SuppressLint("DefaultLocale")
    private fun updateUI() {
        val song = Queue.getCurrentSong()!!
        album.text = song.album

        Glide.with(this)
            .load(song.thumbnail)
            .placeholder(R.drawable.song)
            .error(R.drawable.song)
            .into(songThumbnail)

        artist.text = song.artistName
        songName.text = song.title

        // Set max for SeekBar and display time of song
        seekBar.max = song.duration * 1000
        val minutes = (song.duration) / 60
        val seconds = (song.duration) % 60
        val time = String.format("%02d:%02d", minutes, seconds)
        totalTime.text = time

        updateFavoriteIcon()

        // Update background when song changes
        BackgroundHelper.updateBackgroundWithImageColor(requireContext(), song.thumbnail, playerBackground)
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

    private fun connectUI(view: View) {
        playButton = view.findViewById<ImageButton>(R.id.playButton) as ImageButton
        nextButton = view.findViewById<ImageButton>(R.id.nextButton) as ImageButton
        previousButton = view.findViewById<ImageButton>(R.id.previousButton) as ImageButton
        addToFavoritesButton = view.findViewById<ImageButton>(R.id.addToFavoritesButton) as ImageButton
        minimizeButton = view.findViewById<ImageButton>(R.id.minimizeButton) as ImageButton
        shuffleButton = view.findViewById<ImageButton>(R.id.shuffleButton) as ImageButton
        optionsButton = view.findViewById<ImageButton>(R.id.optionsButton) as ImageButton

        seekBar = view.findViewById<SeekBar>(R.id.seekBar) as SeekBar

        currentTime = view.findViewById<TextView>(R.id.currentTime) as TextView
        totalTime = view.findViewById<TextView>(R.id.totalTime) as TextView
        artist = view.findViewById<TextView>(R.id.artist) as TextView
        songName = view.findViewById<TextView>(R.id.songName) as TextView
        songThumbnail = view.findViewById<ImageView>(R.id.songThumbnail) as ImageView
        album = view.findViewById<TextView>(R.id.album) as TextView

        playerBackground = view.findViewById<ConstraintLayout>(R.id.playerBackground)
    }

    private fun handleShare() {
        val song = Queue.getCurrentSong()
        if (song != null) {
            // Construct the shareable link
            val appLink = "https://adsjgdskjsgdkjsd.web.app/songs/${song._id}"

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this song: ${song.title} by ${song.artistName} $appLink")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        } else {
            Toast.makeText(context, "No song is currently playing", Toast.LENGTH_SHORT).show()
        }
    }

}
