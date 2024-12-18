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
import com.example.mobile_musicapp.services.PlayerManager
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel

class QueueFragment : Fragment() {

    private lateinit var backButton: ImageButton
    private lateinit var quantitySongs: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var playerBarViewModel: PlayerBarViewModel
    private lateinit var shareViewModel: ShareViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_queue, container, false)
        backButton = view.findViewById(R.id.backButton)
        quantitySongs = view.findViewById(R.id.quantitySongs)
        recyclerView = view.findViewById(R.id.playlistRecyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]

        shareViewModel.removedSong.observe(viewLifecycleOwner) {
            setupRecyclerView()
        }

        playerBarViewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        playerBarViewModel.shuffleMode.observe(viewLifecycleOwner) {
            setupRecyclerView()
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Observe navigateToPlayMusicFragment LiveData
        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.navigateToPlayMusicFragment.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate == true) {
                val action = QueueFragmentDirections.actionQueueFragmentToPlayMusicFragment(null)
                findNavController().navigate(action)
                sharedViewModel.navigateToPlayMusicFragment.value = false // Reset
            }
        }
    }

    private fun setupRecyclerView() {
        val songs = Queue.getSongs()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SongAdapter(songs)
        recyclerView.adapter = adapter

        adapter.onItemClick = { song ->
            playerBarViewModel.updateSong(song)
            if (playerBarViewModel.isPlaying.value == false) {
                playerBarViewModel.togglePlayPause()
            }
            Queue.openSong(song)
            PlayerManager.prepare()
        }

        adapter.onOptionClick = { selectedItem ->
            val shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
            shareViewModel.selectedSong.value = selectedItem

            val options = listOf(
                Option.REMOVE_FROM_QUEUE.title,
                Option.SHARE.title
            )
            val actionDialogFragment = MenuOptionFragment.newInstance(options)
            actionDialogFragment.show(parentFragmentManager, "MenuOptionFragment")
        }

        "${songs.size} songs".also { quantitySongs.text = it }
    }
}