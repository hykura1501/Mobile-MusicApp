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
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        backButton = view.findViewById(R.id.backButton)
        playlistTitle = view.findViewById(R.id.playlistTitle)
        quantitySongs = view.findViewById(R.id.quantitySongs)
        playButton = view.findViewById(R.id.playButton)
        recyclerView = view.findViewById(R.id.playlistRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var playlist : Playlist? = null

        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.selectedPlaylist.observe(viewLifecycleOwner) { selectedPlaylist ->
            selectedPlaylist?.let {
                playlist = it
                setupRecyclerView(playlist!!)
            }
        }

        val playerBarViewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        playerBarViewModel.isPlaying.observe(viewLifecycleOwner) {
            if (playerBarViewModel.isPlaying.value == true) {
                playButton.setImageResource(R.drawable.ic_pause_black)
            } else {
                playButton.setImageResource(R.drawable.ic_play_black)
            }

        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Observe navigateToPlayMusicFragment LiveData
        sharedViewModel.navigateToPlayMusicFragment.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate == true) {
                val action = PlaylistFragmentDirections.actionAlbumFragmentToPlayMusicFragment(null)
                findNavController().navigate(action)
                sharedViewModel.navigateToPlayMusicFragment.value = false // Reset
            }
        }

        sharedViewModel.removedSongInPlaylist.observe(viewLifecycleOwner) { removedSong ->
            removedSong?.let {
                if (playlist != null) {
                    val newPlaylist = playlist!!
                    newPlaylist.songs.remove(it)
                    (recyclerView.adapter as SongAdapter).submitList(newPlaylist.songs)
                    "${newPlaylist.songs.size} songs".also { quantitySongs.text = it }
                }
            }
        }
    }

    private fun setupRecyclerView(playlist: Playlist) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SongAdapter(playlist.songs)
        recyclerView.adapter = adapter

        adapter.onItemClick = {
            Queue.openPlaylist(playlist.songs.toMutableList(), playlist.songs.indexOf(it))
            Queue.openPlaylist(
                playlist.songs.toMutableList(),
                playlist.songs.indexOf(it)
            )
            val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
            viewModel.updateSong(Queue.getCurrentSong()!!)
            viewModel.togglePlayPause()
            PlayerManager.prepare()
            val action = PlaylistFragmentDirections.actionAlbumFragmentToPlayMusicFragment(null)
            findNavController().navigate(action)
        }

        adapter.onOptionClick = { selectedItem ->
            val shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            shareViewModel.selectedSong.value = selectedItem
            // TODO: resolve click event
            val options = listOf(
                Option.SHARE.title,
                Option.REMOVE_FROM_PLAYLIST.title,
                Option.ADD_TO_QUEUE.title
            )
            val actionDialogFragment = MenuOptionFragment.newInstance(options)
            actionDialogFragment.show(parentFragmentManager, "MenuOptionFragment")
        }

        playlistTitle.text = playlist.title
        "${playlist.songs.size} songs".also { quantitySongs.text = it }
    }
}