package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.SongAdapter
import com.example.mobile_musicapp.models.Option
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel

class PlaylistFragment : Fragment() {

    private lateinit var backButton: ImageButton
    private lateinit var playlistTitle: TextView
    private lateinit var quantitySongs: TextView
    private lateinit var playButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI(view)

        var playlist : Playlist? = null

        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.selectedPlaylist.observe(viewLifecycleOwner) { selectedPlaylist ->
            selectedPlaylist?.let {
                playlist = it
                setupRecyclerView(playlist!!)
            }
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        playButton.setOnClickListener {
            if (playlist != null) {
                Queue.openPlaylist(playlist!!)
                val playerBarViewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
                playerBarViewModel.updateSong(Queue.getCurrentSong()!!)
                playerBarViewModel.togglePlayPause()
                PlayerManager.prepare()
            }
        }
    }

    private fun setupRecyclerView(playlist: Playlist) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SongAdapter(playlist.songs)
        recyclerView.adapter = adapter

        adapter.onItemClick = {
            // TODO play song
        }

        adapter.onOptionClick = { selectedItem ->
            // TODO: resolve click event
            val options = listOf(
                Option.ADD_TO_PLAYLIST.title,
                Option.SHARE.title
            )
            val actionDialogFragment = MenuOptionFragment.newInstance(options)
            actionDialogFragment.show(parentFragmentManager, "MenuOptionFragment")
        }

        playlistTitle.text = playlist.title
        "${playlist.songs.size} songs".also { quantitySongs.text = it }
    }

    private fun connectUI(view: View) {
        backButton = view.findViewById(R.id.backButton)
        playlistTitle = view.findViewById(R.id.playlistTitle)
        quantitySongs = view.findViewById(R.id.quantitySongs)
        playButton = view.findViewById(R.id.playButton)
        recyclerView = view.findViewById(R.id.playlistRecyclerView)
    }
}