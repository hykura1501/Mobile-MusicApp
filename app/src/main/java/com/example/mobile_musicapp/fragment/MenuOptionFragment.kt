package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.OptionAdapter
import com.example.mobile_musicapp.components.CommentsBottomSheet
import com.example.mobile_musicapp.models.Option
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.PlayerBarViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuOptionFragment : BottomSheetDialogFragment() {

    private lateinit var optionsAdapter: OptionAdapter
    private var options: List<Option> = emptyList()
    private var shareCallback: (() -> Unit)? = null

    private lateinit var songThumbnail: ImageView
    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView

//
    companion object {
        fun newInstance(options: List<String>, shareCallback: (() -> Unit)? = null): MenuOptionFragment {
            val fragment = MenuOptionFragment()
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
        options = receivedOptions.mapNotNull { Option.fromTitle(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu_option, container, false)
        songThumbnail = view.findViewById(R.id.songThumbnail)
        songTitle = view.findViewById(R.id.songTitle)
        songArtist = view.findViewById(R.id.songArtist)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.options_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        optionsAdapter = OptionAdapter(options)
        optionsAdapter.onItemClick = { option ->
            handleOptionClick(option)
        }
        recyclerView.adapter = optionsAdapter

        val shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        shareViewModel.selectedSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                updateSongUI(song)
            }
        }
    }

    private fun handleOptionClick(option: Option) {
        when (option) {
            // TODO: handle option click for each option
            Option.ADD_TO_PLAYLIST -> { /* Handle add to playlist */ }
            Option.REMOVE_FROM_PLAYLIST -> { /* Handle remove from playlist */ }
            Option.REMOVE_FROM_QUEUE -> { removeSongFromQueue() }
            Option.DOWNLOAD -> { /* Handle download */ }
            Option.SHARE -> { shareCallback?.invoke() }
            Option.REPEAT -> { toggleRepeatMode() }
            Option.COMMENT -> {
                dismiss()
                val bottomSheet = CommentsBottomSheet()
                bottomSheet.show(requireActivity().supportFragmentManager,bottomSheet.tag)
                return
            }
            Option.GO_TO_QUEUE -> { navigateToQueue() }
        }
        dismiss()
    }

    private fun updateSongUI(song: Song) {
        songArtist.text = song.artistName
        songTitle.text = song.title

        Glide.with(this)
            .load(song.thumbnail)
            .placeholder(R.drawable.song)
            .error(R.drawable.song)
            .into(songThumbnail)
    }

    private fun toggleRepeatMode() {
        val viewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        viewModel.toggleRepeatMode()
    }

    private fun removeSongFromQueue() {
        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        val playerBarViewModel = ViewModelProvider(requireActivity())[PlayerBarViewModel::class.java]
        sharedViewModel.selectedSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                Queue.removeSong(it)
                playerBarViewModel.deleteSong(song)
            }
        }
    }

    private fun navigateToQueue() {
        val action = PlayMusicFragmentDirections.actionPlayMusicFragmentToQueueFragment()
        findNavController().navigate(action)
    }
}