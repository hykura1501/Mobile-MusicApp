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
import com.example.mobile_musicapp.viewModels.ShareViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Playlist.newInstance] factory method to
 * create an instance of this fragment.
 */
class Playlist : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var backButton: ImageButton
    private lateinit var playlistTitle: TextView
    private lateinit var quantitySongs: TextView
    private lateinit var playButton: ImageButton
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
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Playlist.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Playlist().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI(view)

        var playlist : com.example.mobile_musicapp.models.Playlist

        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.selectedPlaylist.observe(viewLifecycleOwner) { selectedPlaylist ->
            selectedPlaylist?.let {
                playlist = it
                setupRecyclerView(playlist)
            }
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView(playlist: com.example.mobile_musicapp.models.Playlist) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SongAdapter(playlist.songs)
        recyclerView.adapter = adapter

        adapter.onItemClick = {
            val navController = findNavController()
            navController.navigate(R.id.action_library_to_playlist)
        }

        playlistTitle.text = playlist.name
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