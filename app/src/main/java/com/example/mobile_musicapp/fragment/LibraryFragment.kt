package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.PlaylistAdapter
import com.example.mobile_musicapp.models.PlaylistOption
import com.example.mobile_musicapp.services.PlaylistDao
import com.example.mobile_musicapp.viewModels.ShareViewModel

@Suppress("RemoveExplicitTypeArguments")
class LibraryFragment : Fragment() {

    private lateinit var createPlaylistButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        createPlaylistButton = view.findViewById<ImageButton>(R.id.createPlaylistButton)!!
        recyclerView = view.findViewById(R.id.playlistRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]

        lifecycleScope.launch {
            try {
                val fetchedPlaylists = PlaylistDao.getAllPlaylists().toMutableList()
                sharedViewModel.addAllPlaylists(fetchedPlaylists)
            } catch (e: Exception) {
                Log.e("PlaylistFragment", "Error loading playlists", e)
            }

            sharedViewModel.playlists.observe(viewLifecycleOwner) { updatedPlaylists ->
                (recyclerView.adapter as? PlaylistAdapter)?.submitList(updatedPlaylists)
            }
        }

        createPlaylistButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }

        // Observe navigateToPlayMusicFragment LiveData
        sharedViewModel.navigateToPlayMusicFragment.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate == true) {
                val action = LibraryFragmentDirections.actionLibraryFragmentToPlayMusicFragment(null)
                findNavController().navigate(action)
                sharedViewModel.navigateToPlayMusicFragment.value = false // Reset
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PlaylistAdapter()
        recyclerView.adapter = adapter

        // Short click
        adapter.onItemClick = { playlist ->
            val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            sharedViewModel.selectedPlaylist.value = playlist
            val navController = findNavController()
            navController.navigate(R.id.action_libraryFragment_to_playlistFragment)
        }

        // Long click
        adapter.onItemLongClick = { selectedItem ->
            val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            sharedViewModel.longSelectedPlaylist.value = selectedItem

            // Create options for bottom sheet dialog fragment
            val options = listOf(
                PlaylistOption.DELETE_PLAYLIST.title,
            )
            val actionDialogFragment = PlaylistMenuOptionFragment.newInstance(options)
            actionDialogFragment.show(parentFragmentManager, "PlaylistMenuOptionFragment")
        }
    }
}