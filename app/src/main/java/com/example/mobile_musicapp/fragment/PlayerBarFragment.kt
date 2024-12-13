package com.example.mobile_musicapp.fragment

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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel

class PlayerBarFragment : Fragment() {
    private lateinit var viewModel: PlayerBarViewModel
    private lateinit var seekBar: SeekBar
    private lateinit var playPauseButton: ImageButton

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            val currentPosition = PlayerManager.getCurrentPosition()
            viewModel.updatePosition(currentPosition)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_player_bar, container, false)
        seekBar = view.findViewById(R.id.bottomSeekBar)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        seekBar.isEnabled = false
        PlayerManager.initialize()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]

        // Bind ViewModel data to UI
        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            seekBar.progress = position
        }

        viewModel.currentSong.observe(viewLifecycleOwner) { song ->
            if (song != null) {
                updateSongUI(song)

            }
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            playPauseButton.setImageResource(
                if (isPlaying) R.drawable.ic_pause_white else R.drawable.ic_play_white
            )
            if (isPlaying) {
                PlayerManager.play()
                handler.post(updateSeekBarRunnable)
            } else {
                PlayerManager.pause()
            }
        }

        // Play/Pause button logic
        playPauseButton.setOnClickListener {
            viewModel.togglePlayPause()
        }
    }

    private fun updateSongUI(song: Song) {
        val songThumbnail = requireView().findViewById<ImageView>(R.id.songThumbnail)
        val songTitle = requireView().findViewById<TextView>(R.id.songTitle)
        val songArtist = requireView().findViewById<TextView>(R.id.songArtist)

        songArtist.text = song.artistName
        songTitle.text = song.title
        Glide.with(this)
            .load(song.thumbnail)
            .placeholder(R.drawable.song)
            .error(R.drawable.song)
            .into(songThumbnail)

        seekBar.max = song.duration
        viewModel.updatePosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateSeekBarRunnable)
    }
}