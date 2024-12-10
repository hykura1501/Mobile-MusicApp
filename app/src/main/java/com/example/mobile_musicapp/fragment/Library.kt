package com.example.mobile_musicapp.fragment

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.PlaylistAdapter
import com.example.mobile_musicapp.models.Option
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.services.MockDao
import com.example.mobile_musicapp.viewModels.ShareViewModel

@Suppress("RemoveExplicitTypeArguments")
class Library : Fragment() {

    private lateinit var createPlaylistButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectUI(view)

        // Get sample playlists
        val playlists = MockDao().getSamplePlaylists()

        // Get new playlist from view model
        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.addedPlaylist.observe(viewLifecycleOwner) { addedPlaylist ->
            if (addedPlaylist != null){
                playlists.add(addedPlaylist)
                setupRecyclerView(playlists)
            }
        }

        sharedViewModel.deletedPlaylist.observe(viewLifecycleOwner) { deletedPlaylist ->
            if (deletedPlaylist != null && playlists.contains(deletedPlaylist)) {
                playlists.remove(deletedPlaylist)
                setupRecyclerView(playlists)
            }
        }

        // Set up recycler view
        setupRecyclerView(playlists)

        createPlaylistButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_library_to_newPlaylist)
        }
    }

    private fun setupRecyclerView(playlists: MutableList<Playlist>) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PlaylistAdapter(playlists)
        recyclerView.adapter = adapter



        // Short click
        adapter.onItemClick = { playlist ->
            val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            sharedViewModel.selectedPlaylist.value = playlist
            val navController = findNavController()
            navController.navigate(R.id.action_library_to_playlist)
        }

        // Long click
        adapter.onItemLongClick = { selectedItem ->
            // Send data to bottom sheet dialog fragment
            val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            sharedViewModel.longSelectedPlaylist.value = selectedItem

            val options = listOf(
                Option.DELETE_PLAYLIST.title,
                Option.DOWNLOAD.title,
                Option.SHARE.title
            )
            val actionDialogFragment = MenuOptionFragment.newInstance(options)
            actionDialogFragment.show(parentFragmentManager, "MenuOptionFragment")
        }
    }

    private fun connectUI(view: View) {
        createPlaylistButton = view.findViewById<ImageButton>(R.id.createPlaylistButton)!!
        recyclerView = view.findViewById(R.id.playlistRecyclerView)
    }
}