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
import com.example.mobile_musicapp.extension.queryTextChanges
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.SongListWithIndex
import com.example.mobile_musicapp.repository.SongPagingSource
import com.example.mobile_musicapp.viewModels.SearchViewModel
import com.example.mobile_musicapp.viewModels.SongViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeepSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeepSearch : Fragment() {

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
        SongPagingAdapter{
            val selectedIndex = SongPagingSource.cachedSongsSet.indexOfFirst { data -> data._id == it._id }
            if (selectedIndex != -1 ){
                val action = DeepSearchDirections.actionDeepSearchToPlayMusicFragment(
                    SongListWithIndex(SongPagingSource.cachedSongsSet.toList(), selectedIndex)
                )
                findNavController().navigate(action)
            }
        }
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

        songRecentlyAdapter.onItemClick = { song ->
            val selectedIndex = songPlayRecentlyList.indexOfFirst { data -> data._id == song._id }
            if (selectedIndex != -1 ){
                val action = DeepSearchDirections.actionDeepSearchToPlayMusicFragment(
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
                };
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
            if (b) {
//                searchViewModel.getAllPlayedList()
            }
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