package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.SongAdapter
import com.example.mobile_musicapp.adapters.SongPagingAdapter
import com.example.mobile_musicapp.models.Option
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.SongListWithIndex
import com.example.mobile_musicapp.repository.SongPagingSource
import com.example.mobile_musicapp.viewModels.SearchViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel
import com.example.mobile_musicapp.viewModels.SongViewModel
import kotlinx.coroutines.launch

class DeepSearchFragment : Fragment() {

    private lateinit var rcvSong : RecyclerView
    private lateinit var rcvPlayedRecently : RecyclerView
    private lateinit var rcvPlaylist : RecyclerView
    private lateinit var searchData : SearchView
    private lateinit var icBack : ImageButton
    private lateinit var rbSong : RadioButton
    private lateinit var rbPlayRecently : RadioButton
    private lateinit var rbPlaylist : RadioButton
    private lateinit var searchViewModel: SearchViewModel
    private val songViewModel: SongViewModel by viewModels()
    private val songPlayRecentlyList = mutableListOf<Song>()
    private val songAdapter by lazy {
        SongPagingAdapter (
            onClick = {
                val selectedIndex = SongPagingSource.cachedSongsSet.indexOfFirst { data -> data._id == it._id }
                if (selectedIndex != -1 ){
                    val action = DeepSearchFragmentDirections.actionDeepSearchFragmentToPlayMusicFragment(
                        SongListWithIndex(SongPagingSource.cachedSongsSet.toList(), selectedIndex)
                    )
                    findNavController().navigate(action)
                }
            },
            onOptionClick = {
                val shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
                shareViewModel.selectedSong.value = it
                // TODO: resolve click event
                val options = listOf(
                    Option.SHARE.title,
                    Option.ADD_TO_PLAYLIST.title,
                    Option.ADD_TO_QUEUE.title,
                    Option.DOWNLOAD.title,
                )
                val actionDialogFragment = MenuOptionFragment.newInstance(options)
                actionDialogFragment.show(parentFragmentManager, "MenuOptionFragment")
            }
        )
    }

    private val songRecentlyAdapter by lazy {
        SongAdapter(emptyList())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deep_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]

        connectView(view)
        setUpSongRecyclerview()
        setUpPlayRecentlyRecyclerview()
        setUpPlaylistRecyclerview()

        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.navigateToPlayMusicFragment.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate == true) {
                val action = DeepSearchFragmentDirections.actionDeepSearchFragmentToPlayMusicFragmentNoArgs(null)
                findNavController().navigate(action)
                sharedViewModel.navigateToPlayMusicFragment.value = false // Reset
            }
        }

        songRecentlyAdapter.onItemClick = { song ->
            val selectedIndex = songPlayRecentlyList.indexOfFirst { data -> data._id == song._id }
            if (selectedIndex != -1 ){
                val action = DeepSearchFragmentDirections.actionDeepSearchFragmentToPlayMusicFragment(
                    SongListWithIndex(songPlayRecentlyList, selectedIndex)
                )
                findNavController().navigate(action)
            }
        }

        icBack.setOnClickListener {
            findNavController().popBackStack()
        }
        rbSong.isChecked = true
        searchViewModel.searchedSongs.observe(requireActivity()) { pagingData ->

            rcvSong.visibility  = View.VISIBLE
            rcvPlayedRecently.visibility  = View.GONE
            rcvPlaylist.visibility  = View.GONE

            lifecycleScope.launch {
                songAdapter.submitData(lifecycle, pagingData)
            }
        }

        songViewModel.songsListLiveData.observe(requireActivity()){
            if (it != null){
                songPlayRecentlyList.clear()
                songPlayRecentlyList.addAll(it)
                rcvSong.visibility  = View.GONE
                rcvPlayedRecently.visibility  = View.VISIBLE
                rcvPlaylist.visibility  = View.GONE
                songRecentlyAdapter.submitList(it)
            }
        }

        searchData.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchViewModel.updateQuery(newText)
                }
                return true
            }
        })

        rbSong.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                rcvSong.visibility  = View.VISIBLE
                rcvPlayedRecently.visibility  = View.GONE
                rcvPlaylist.visibility  = View.GONE
                searchViewModel.updateQuery(searchData.query.toString())
            }
        }
        rbPlayRecently.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                songViewModel.getAllPlayedRecently()
            }
        }

        rbPlaylist.setOnCheckedChangeListener { compoundButton, b ->
//            if (b) {
////                searchViewModel.getAllPlayedList()
//            }
        }
    }

    private fun setUpSongRecyclerview(){
        rcvSong.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = songAdapter
        }
    }
    private fun setUpPlayRecentlyRecyclerview(){
        rcvPlayedRecently.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = songRecentlyAdapter
        }
    }
    private fun setUpPlaylistRecyclerview(){
        rcvPlaylist.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = songAdapter
        }
    }

    private fun connectView(view: View) {
        rcvSong = view.findViewById(R.id.rcv_song)
        icBack = view.findViewById(R.id.imageButton)
        rcvPlayedRecently = view.findViewById(R.id.rcv_song_recently)
        rcvPlaylist = view.findViewById(R.id.rcv_playlist)
        searchData = view.findViewById(R.id.searchView)
        rbSong = view.findViewById(R.id.songCategoryRadioButton)
        rbPlayRecently = view.findViewById(R.id.artistCategoryRadioButton)
        rbPlaylist = view.findViewById(R.id.playlistCategoryRadioButton)
    }
}