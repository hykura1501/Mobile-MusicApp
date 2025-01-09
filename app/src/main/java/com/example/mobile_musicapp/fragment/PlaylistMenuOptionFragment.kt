package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.PlaylistOptionAdapter
import com.example.mobile_musicapp.models.PlaylistOption
import com.example.mobile_musicapp.viewModels.ShareViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaylistMenuOptionFragment : BottomSheetDialogFragment() {

    private lateinit var optionsAdapter: PlaylistOptionAdapter
    private var options: List<PlaylistOption> = emptyList()
    private var shareCallback: (() -> Unit)? = null

    private lateinit var playlistThumbnail: ImageView
    private lateinit var playlistTitle: TextView

    companion object {
        fun newInstance(options: List<String>, shareCallback: (() -> Unit)? = null): PlaylistMenuOptionFragment {
            val fragment = PlaylistMenuOptionFragment()
            val args = Bundle()
            args.putStringArrayList("OPTIONS", ArrayList(options))
            fragment.arguments = args
            fragment.shareCallback = shareCallback
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedOptions = arguments?.getStringArrayList("OPTIONS") ?: emptyList()
        options = receivedOptions.mapNotNull { PlaylistOption.fromTitle(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_playlist_menu_option, container, false)
        playlistThumbnail = view.findViewById(R.id.playlistThumbnail)
        playlistTitle = view.findViewById(R.id.playlistTitle)
        //playlist = view.findViewById(R.id.playlist)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.options_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        optionsAdapter = PlaylistOptionAdapter(options)
        optionsAdapter.onItemClick = { option ->
            handleOptionClick(option)
        }
        recyclerView.adapter = optionsAdapter

        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.longSelectedPlaylist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                playlistTitle.text = it.title
            }
        }

//        Glide.with(this)
//            .load(song.thumbnail)
//            .placeholder(R.drawable.song)
//            .error(R.drawable.song)
//            .into(songThumbnail)
    }

    private fun handleOptionClick(option: PlaylistOption) {
        when (option) {
            PlaylistOption.DELETE_PLAYLIST -> { deletePlaylist() }
        }
        dismiss()
    }

    private fun deletePlaylist() {
        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.longSelectedPlaylist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                sharedViewModel.removePlaylist(it)
            }
        }
    }
}