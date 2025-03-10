package com.example.mobile_musicapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.singletons.Favorite
import com.example.mobile_musicapp.singletons.Queue
import com.google.android.material.snackbar.Snackbar
import kotlin.text.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.adapters.LyricsAdapter
import com.example.mobile_musicapp.helpers.BackgroundHelper
import com.example.mobile_musicapp.itemDecoration.FadeEdgeItemDecoration
import com.example.mobile_musicapp.models.LyricLine
import com.example.mobile_musicapp.models.Option
import com.example.mobile_musicapp.services.FavoriteSongDao
import com.example.mobile_musicapp.services.MediaPlaybackService
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.services.SongDao
import com.example.mobile_musicapp.viewModels.FavoritesViewModel
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import com.example.mobile_musicapp.viewModels.SongViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel
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
    private lateinit var repeatButton: ImageButton
    private lateinit var optionsButton: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var artist: TextView
    private lateinit var songName: TextView
    private lateinit var album: TextView
    private lateinit var songThumbnail: ImageView
    private lateinit var playerBackground: ConstraintLayout
    private lateinit var recyclerLyrics: RecyclerView
    private lateinit var lyricContainer: CardView

    private var isFavorite: Boolean = false

    private val args: PlayMusicFragmentArgs by navArgs()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val songViewModel : SongViewModel by viewModels()
    private var lyrics = emptyList<LyricLine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the list of songs and selected index from the arguments
        val songListWithIndex = args.songListWithIndex
        if (songListWithIndex != null) {
            Queue.openPlaylist(
                songListWithIndex.songs.toMutableList(),
                songListWithIndex.selectedIndex
            )
            val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
            viewModel.updateSong(Queue.getCurrentSong()!!)
            viewModel.updatePlayPause(true)
            PlayerManager.prepare()
        }

        // Start the MediaPlaybackService
        val intent = Intent(requireContext(), MediaPlaybackService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        Log.d("PlayMusicFragment", "MediaPlaybackService startForegroundService called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Button
        val view = inflater.inflate(R.layout.fragment_play_music, container, false)
        playButton = view.findViewById<ImageButton>(R.id.playButton) as ImageButton
        nextButton = view.findViewById<ImageButton>(R.id.nextButton) as ImageButton
        previousButton = view.findViewById<ImageButton>(R.id.previousButton) as ImageButton
        addToFavoritesButton = view.findViewById<ImageButton>(R.id.addToFavoritesButton) as ImageButton
        minimizeButton = view.findViewById<ImageButton>(R.id.minimizeButton) as ImageButton
        shuffleButton = view.findViewById<ImageButton>(R.id.shuffleButton) as ImageButton
        optionsButton = view.findViewById<ImageButton>(R.id.optionsButton) as ImageButton
        repeatButton = view.findViewById<ImageButton>(R.id.repeatButton) as ImageButton

        seekBar = view.findViewById<SeekBar>(R.id.seekBar) as SeekBar

        // Textview
        currentTime = view.findViewById<TextView>(R.id.currentTime) as TextView
        totalTime = view.findViewById<TextView>(R.id.totalTime) as TextView
        artist = view.findViewById<TextView>(R.id.artist) as TextView
        songName = view.findViewById<TextView>(R.id.songName) as TextView
        album = view.findViewById<TextView>(R.id.album) as TextView

        songThumbnail = view.findViewById<ImageView>(R.id.songThumbnail) as ImageView

        playerBackground = view.findViewById<ConstraintLayout>(R.id.playerBackground)
        recyclerLyrics = view.findViewById<RecyclerView>(R.id.lyric) as RecyclerView
        lyricContainer = view.findViewById<CardView>(R.id.lyricContainer) as CardView

        return view
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect UI components
        val song = PlayerManager.getCurrentSong()

        if (song.title != "Ad") {
            Queue.getCurrentSong()?.let {
                songViewModel.addPlayedRecently(it._id)
            }
        }
        updateUI()

        val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]

        // Update playing time of song
        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            seekBar.progress = position!!.toInt()

            val minutes = (position / 1000) / 60
            val seconds = (position / 1000) % 60
            val time = String.format("%02d:%02d", minutes, seconds)
            currentTime.text = time
        }

        viewModel.currentSong.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                getLyrics()
                withContext(Dispatchers.Main) {
                    loadLyrics()
                }
            }
            updateUI()
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) {
            if (viewModel.isPlaying.value == true) {
                playButton.setImageResource(R.drawable.ic_pause_black)
            } else {
                playButton.setImageResource(R.drawable.ic_play_black)
            }
        }

        // Play/Pause button handling
        playButton.setOnClickListener {
            viewModel.togglePlayPause()
        }

        nextButton.setOnClickListener {
            if (PlayerManager.getCurrentSong().title != "Advertisement") {
                PlayerManager.next()
            }
        }

        previousButton.setOnClickListener {
            if (PlayerManager.getCurrentSong().title != "Advertisement") {
                PlayerManager.previous()
            }
        }

        minimizeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        shuffleButton.setOnClickListener {
            if (PlayerManager.getCurrentSong().title != "Advertisement") {
                viewModel.toggleShuffleMode()
                updateShuffleIcon()
                Queue.toggleShuffleMode()
            }
        }

        repeatButton.setOnClickListener {
            if (PlayerManager.getCurrentSong().title != "Advertisement") {
                viewModel.toggleRepeatMode()
                updateRepeatIcon()
            }
        }

        optionsButton.setOnClickListener {
            val shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            shareViewModel.selectedSong.value = song
            val options = listOf(
                Option.COMMENT.title,
                Option.SHARE.title,
                Option.GO_TO_QUEUE.title,
                Option.SLEEP_TIMER.title,
                Option.ARTIST_INFO.title,
                Option.ADD_TO_PLAYLIST.title,
                Option.DOWNLOAD.title,
            )
            val actionDialogFragment = MenuOptionFragment.newInstance(options) { handleShare() }
            actionDialogFragment.show(parentFragmentManager, "MenuOptionFragment")
        }

        addToFavoritesButton.setOnClickListener {
            if (PlayerManager.getCurrentSong().title != "Advertisement") {
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
                            Toast.makeText(
                                context,
                                throwable.message ?: "Failed to update Favorites!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }

        seekBar.isEnabled = song.title != "Advertisement"
        seekBar.setOnSeekBarChangeListener (
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser && PlayerManager.getCurrentSong().title != "Advertisement") {
                        PlayerManager.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    if (PlayerManager.getCurrentSong().title != "Advertisement") {
                        PlayerManager.pause()
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (PlayerManager.getCurrentSong().title != "Advertisement") {
                        PlayerManager.play()
                        viewModel.updatePlayPause(true)
                    }
                }
            }
        )
        // Check and update the favorite icon on load
        updateFavoriteIcon()

        // Set background using BackgroundHelper
        BackgroundHelper.updateBackgroundWithImageColor(requireContext(), song.thumbnail, playerBackground, cornerRadius = 0f)
    }

    private fun loadLyrics() {
        val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]

        val adapter = LyricsAdapter(lyrics)
        recyclerLyrics.adapter = adapter
        recyclerLyrics.layoutManager = LinearLayoutManager(requireContext())

        val fadeHeight = 20
        recyclerLyrics.addItemDecoration(FadeEdgeItemDecoration(fadeHeight))

        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            val currentIndex = lyrics.indexOfLast { it.timestamp <= position }
            if (currentIndex != adapter.highlightedPosition) {
                adapter.highlightedPosition = currentIndex
                adapter.notifyItemChanged(adapter.highlightedPosition)
                if (currentIndex != -1) {
                    if (currentIndex + 1 < lyrics.size) {
                        recyclerLyrics.smoothScrollToPosition(currentIndex + 2)
                    }
                    else {
                        recyclerLyrics.smoothScrollToPosition(currentIndex)
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateUI() {
        val song = PlayerManager.getCurrentSong()
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
        updateShuffleIcon()
        updateRepeatIcon()

        // Update background when song changes
        BackgroundHelper.updateBackgroundWithImageColor(requireContext(), song.thumbnail, playerBackground)
    }

    private fun updateShuffleIcon() {
        val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        if (viewModel.shuffleMode.value == true) {
            shuffleButton.setImageResource(R.drawable.ic_shuffle_filled)
        }
        else {
            shuffleButton.setImageResource(R.drawable.ic_shuffle)
        }
    }

    private fun updateRepeatIcon() {
        val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        if (viewModel.repeatMode.value == true) {
            repeatButton.setImageResource(R.drawable.ic_repeat_filled)
        }
        else {
            repeatButton.setImageResource(R.drawable.ic_repeat)
        }
    }

    private fun updateFavoriteIcon() {
        val song = PlayerManager.getCurrentSong()
        if (song.title == "Advertisement") {
            addToFavoritesButton.visibility = View.GONE
        }
        else {
            addToFavoritesButton.visibility = View.VISIBLE
        }
        isFavorite = Favorite.getPlaylist().any { it._id == song._id }
        if (isFavorite) {
            addToFavoritesButton.setImageResource(R.drawable.ic_heart_filled)
        } else {
            addToFavoritesButton.setImageResource(R.drawable.ic_heart)
        }
    }

    private fun handleShare() {
        val song = PlayerManager.getCurrentSong()

        if (song.title == "Advertisement") {
            Toast.makeText(requireContext(), "This is an ad song!", Toast.LENGTH_SHORT).show()
            return
        }
        // Construct the shareable link
        val appLink = "https://adsjgdskjsgdkjsd.web.app/songs/${song._id}"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this song: ${song.title} by ${song.artistName} $appLink")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private suspend fun getLyrics() {
        val song = PlayerManager.getCurrentSong()
        if (song.title == "Advertisement") {
            lyricContainer.visibility = View.INVISIBLE
            return
        }
        lyricContainer.visibility = View.VISIBLE
        lyrics = SongDao.fetchLyrics(song.lyric)
    }
}