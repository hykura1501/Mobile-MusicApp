package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.PlaylistAdapter
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.PlaylistDao
import com.example.mobile_musicapp.viewModels.ShareViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ChoosingPlaylistFragment : BottomSheetDialogFragment() {

    private lateinit var songThumbnail: ImageView
    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var recyclerView: RecyclerView

    private var selectedSong : Song? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_choosing_playlist, container, false)
        songThumbnail = view.findViewById(R.id.songThumbnail)
        songTitle = view.findViewById(R.id.songTitle)
        songArtist = view.findViewById(R.id.songArtist)
        recyclerView = view.findViewById(R.id.playlists_recycler_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.selectedSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                songArtist.text = song.artistName
                songTitle.text = song.title

                Glide.with(this)
                    .load(song.thumbnail)
                    .placeholder(R.drawable.song)
                    .error(R.drawable.song)
                    .into(songThumbnail)

                selectedSong = song
            }
        }

        val playlists = sharedViewModel.getAllPlaylists()

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = PlaylistAdapter()
        (recyclerView.adapter as PlaylistAdapter).submitList(playlists)

        (recyclerView.adapter as PlaylistAdapter).onItemClick = { playlist ->
            lifecycleScope.launch {
                PlaylistDao.addSongToPlaylist(playlist.playlistId, selectedSong!!._id)
                sharedViewModel.updatePlaylist(playlist.playlistId)
            }
            dismiss()
        }
    }
}