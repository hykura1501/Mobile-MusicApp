package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.PlaylistAdapter
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.services.MockDao
import com.example.mobile_musicapp.viewModels.ShareViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Library.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("RemoveExplicitTypeArguments")
class Library : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var createPlaylistButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Library.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Library().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectUI(view)

        // Get sample playlists
        val playlists = MockDao().getSamplePlaylists()

        // Get new playlist from view model
        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.shareDataPlaylist.observe(viewLifecycleOwner) { updatedPlaylists ->
            playlists.addAll(updatedPlaylists)
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

        adapter.onItemClick = { playlist ->
            val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            sharedViewModel.selectedPlaylist.value = playlist
            val navController = findNavController()
            navController.navigate(R.id.action_library_to_playlist)
        }

        adapter.onItemLongClick = { selectedItem ->
            Toast.makeText(context, "Long clicked: $selectedItem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectUI(view: View) {
        createPlaylistButton = view.findViewById<ImageButton>(R.id.createPlaylistButton)!!
        recyclerView = view.findViewById(R.id.playlistRecyclerView)
    }
}