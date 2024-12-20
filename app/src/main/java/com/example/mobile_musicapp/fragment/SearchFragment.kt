package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.SongPagingAdapter
import com.example.mobile_musicapp.models.SongListWithIndex
import com.example.mobile_musicapp.repository.SongPagingSource
import com.example.mobile_musicapp.viewModels.SearchViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel
import kotlinx.coroutines.FlowPreview

class SearchFragment : Fragment() {

    private lateinit var rcvSong : RecyclerView
    private lateinit var searchData : Button
    private lateinit var searchViewModel: SearchViewModel

    private val songAdapter by lazy {
        SongPagingAdapter {
            val selectedIndex = SongPagingSource.cachedSongsSet.indexOfFirst { data -> data._id == it._id }
            if (selectedIndex != -1 ){
                val action = SearchFragmentDirections.actionSearchFragmentToPlayMusicFragment(
                    SongListWithIndex(SongPagingSource.cachedSongsSet.toList(), selectedIndex)
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectView(view)
        setUpRecyclerview()

        searchViewModel =  ViewModelProvider(requireActivity())[SearchViewModel::class.java]

        searchViewModel.pagedSongsSmaller.observe(requireActivity()) {
            songAdapter.submitData(lifecycle,it)
        }

        searchData.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_deepSearchFragment)
        }

        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.navigateToPlayMusicFragment.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate == true) {
                val action = SearchFragmentDirections.actionSearchFragmentToPlayMusicFragmentNoArgs(null)
                findNavController().navigate(action)
                sharedViewModel.navigateToPlayMusicFragment.value = false // Reset
            }
        }
    }
    private fun setUpRecyclerview(){
        rcvSong.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = songAdapter
        }
    }

    private fun connectView(view: View) {
        rcvSong = view.findViewById(R.id.genreRecyclerView)
        searchData = view.findViewById(R.id.iconMaterialButton)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Search.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}