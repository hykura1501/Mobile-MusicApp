package com.example.mobile_musicapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.SongPagingAdapter
import com.example.mobile_musicapp.extension.queryTextChanges
import com.example.mobile_musicapp.viewModels.SearchViewModel
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
    private lateinit var searchData : SearchView
    private val songAdapter by lazy {
        SongPagingAdapter()
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
         val searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]

        connectView(view)
        setUpRecyclerview()
        searchViewModel.searchedSongs.observe(requireActivity()) { pagingData ->
            lifecycleScope.launch {
                songAdapter.submitData(lifecycle, pagingData)
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
    }

    private fun setUpRecyclerview(){
        rcvSong.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = songAdapter
        }
    }

    private fun connectView(view: View) {
        rcvSong = view.findViewById(R.id.topArtistRecyclerView)
        searchData = view.findViewById(R.id.searchView)
    }
}