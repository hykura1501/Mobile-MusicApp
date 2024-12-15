@file:Suppress("RemoveExplicitTypeArguments")

package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel

class PlayerBarFragment : Fragment() {
    private lateinit var seekBar: SeekBar
    private lateinit var playPauseButton: ImageButton
    private lateinit var queueButton: ImageButton
    private lateinit var songThumbnail : ImageView
    private lateinit var songTitle : TextView
    private lateinit var songArtist : TextView
    private lateinit var playerBar: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_player_bar, container, false)
        seekBar = view.findViewById(R.id.bottomSeekBar)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        queueButton = view.findViewById(R.id.queueButton)
        songThumbnail = view.findViewById<ImageView>(R.id.songThumbnail)
        songTitle = view.findViewById<TextView>(R.id.songTitle)
        songArtist = view.findViewById<TextView>(R.id.songArtist)
        playerBar = view.findViewById(R.id.playerBar)
        seekBar.isEnabled = false
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]

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
            if (isPlaying) {
                PlayerManager.play()
                playPauseButton.setImageResource(R.drawable.ic_pause_white)
            } else {
                PlayerManager.pause()
                playPauseButton.setImageResource(R.drawable.ic_play_white)
            }
        }

        // Play/Pause button logic
        playPauseButton.setOnClickListener {
            viewModel.togglePlayPause()
        }

        if (Queue.getCurrentSong() != null) {
            playerBar.visibility = View.VISIBLE
            updateSongUI(Queue.getCurrentSong()!!)
        }
        else {
            playerBar.visibility = View.GONE
        }

        // Queue button logic
        queueButton.setOnClickListener {
            if (requireParentFragment() !is QueueFragment) {
                val navController = requireParentFragment().findNavController()
                navController.navigate(R.id.action_player_bar_to_queue)
            }
        }
    }

    private fun updateSongUI(song: Song) {
        songArtist.text = song.artistName
        songTitle.text = song.title

        Glide.with(this)
            .load(song.thumbnail)
            .placeholder(R.drawable.song)
            .error(R.drawable.song)
            .into(songThumbnail)

        seekBar.max = song.duration * 1000
    }
}