package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.SongAdapter
import com.example.mobile_musicapp.models.Option
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.repository.SongRepository
import com.example.mobile_musicapp.services.FavoriteSongDao
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongsFragment : Fragment() {

    private lateinit var backButton: ImageButton
    private lateinit var playlistTitle: TextView
    private lateinit var quantitySongs: TextView
    private lateinit var playButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var songs : List<Song>
    private val args: SongsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_songs, container, false)
        backButton = view.findViewById(R.id.backButton)
        playlistTitle = view.findViewById(R.id.title)
        quantitySongs = view.findViewById(R.id.quantitySongs)
        playButton = view.findViewById(R.id.playButton)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = args.title
        when (title) {
            "Recently Played" -> {
                lifecycleScope.launch {
                    songs = SongRepository().getAllPlayedRecently()!!.data
                    withContext(Dispatchers.Main) {
                        setupRecyclerView()
                    }
                }
            }
            "Favorite Songs" -> {
                lifecycleScope.launch {
                    songs = FavoriteSongDao.getFavoriteSongs()
                    withContext(Dispatchers.Main) {
                        setupRecyclerView()
                    }
                }
            }
            else -> {
                lifecycleScope.launch {

                    withContext(Dispatchers.Main) {
                        setupRecyclerView()
                    }
                }
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
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SongAdapter(songs)
        recyclerView.adapter = adapter

        adapter.onItemClick = {
            Queue.openPlaylist(songs.toMutableList(), songs.indexOf(it))
            Queue.openPlaylist(
                songs.toMutableList(),
                songs.indexOf(it)
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
            val options = listOf(
                Option.SHARE.title,
                Option.ADD_TO_QUEUE.title
            )
            val actionDialogFragment = MenuOptionFragment.newInstance(options)
            actionDialogFragment.show(parentFragmentManager, "MenuOptionFragment")
        }

        playlistTitle.text = args.title
        "${songs.size} songs".also { quantitySongs.text = it }
    }
}