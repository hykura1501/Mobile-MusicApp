package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.adapters.SongHorizontalAdapter
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.helpers.RandomHelper
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.SongListWithIndex
import com.example.mobile_musicapp.services.SongDao
import com.example.mobile_musicapp.viewModels.FavoritesViewModel
import com.example.mobile_musicapp.viewModels.ShareViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var greetingTextView: TextView
    private lateinit var favoriteSongsRecyclerView: RecyclerView
    private lateinit var newReleaseSongsRecyclerView: RecyclerView
    private lateinit var popularSongsRecyclerView: RecyclerView
    private lateinit var topLikesSongsRecyclerView: RecyclerView
    private lateinit var recommendedSongsRecyclerView: RecyclerView

    private val favoritesViewModel: FavoritesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteSongsRecyclerView = view.findViewById(R.id.favouriteSongsRecyclerView)
        greetingTextView = view.findViewById(R.id.greetingTextView)
        newReleaseSongsRecyclerView = view.findViewById(R.id.newReleaseSongsRecyclerView)
        popularSongsRecyclerView = view.findViewById(R.id.popularSongsRecyclerView)
        topLikesSongsRecyclerView = view.findViewById(R.id.topLikesSongsRecyclerView)
        recommendedSongsRecyclerView = view.findViewById(R.id.recommendedSongsRecyclerView)

        setupRecyclerViews()

        loadNewReleaseSongs(1, 13)
        loadPopularSongs(1, 7)
        loadTopLikesSongs(1, 23)

        val randomHelper = RandomHelper()
        val perPage = randomHelper.getRandomNumber(11, 19)
        val page = randomHelper.getRandomNumber(5, 37)
        loadRecommendedSongs(page, perPage)

        // Observe loading state and favorite songs from ViewModel
        favoritesViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                val favoriteSongs = favoritesViewModel.favoriteSongs.value ?: emptyList()
                loadFavoriteSongs(favoriteSongs)
            }
        })

        // Observe navigateToPlayMusicFragment LiveData
        val sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.navigateToPlayMusicFragment.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate == true) {
                val action = HomeFragmentDirections.actionHomeFragmentToPlayMusicFragment(null)
                findNavController().navigate(action)
                sharedViewModel.navigateToPlayMusicFragment.value = false // Reset
            }
        }


        updateGreeting()
    }

    private fun setupRecyclerViews() {
        favoriteSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newReleaseSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        popularSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        topLikesSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recommendedSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun loadNewReleaseSongs(page: Int, perPage: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val newReleaseSongs = withContext(Dispatchers.IO) {
                SongDao.getNewReleaseSongs(page, perPage)
            }
            newReleaseSongsRecyclerView.adapter = SongHorizontalAdapter(newReleaseSongs) { song ->
                val selectedIndex = newReleaseSongs.indexOf(song)
                val action = HomeFragmentDirections.actionHomeFragmentToPlayMusicFragment(SongListWithIndex(newReleaseSongs, selectedIndex))
                findNavController().navigate(action)
            }
        }
    }

    private fun loadPopularSongs(page: Int, perPage: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val popularSongs = withContext(Dispatchers.IO) {
                SongDao.getPopularSongs(page, perPage)
            }
            popularSongsRecyclerView.adapter = SongHorizontalAdapter(popularSongs) { song ->
                val selectedIndex = popularSongs.indexOf(song)
                val action = HomeFragmentDirections.actionHomeFragmentToPlayMusicFragment(SongListWithIndex(popularSongs, selectedIndex))
                findNavController().navigate(action)
            }
        }
    }

    private fun loadTopLikesSongs(page: Int, perPage: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val topLikesSongs = withContext(Dispatchers.IO) {
                SongDao.getTopLikesSongs(page, perPage)
            }
            topLikesSongsRecyclerView.adapter = SongHorizontalAdapter(topLikesSongs) { song ->
                val selectedIndex = topLikesSongs.indexOf(song)
                val action = HomeFragmentDirections.actionHomeFragmentToPlayMusicFragment(SongListWithIndex(topLikesSongs, selectedIndex))
                findNavController().navigate(action)
            }
        }
    }

    private fun loadFavoriteSongs(favoriteSongs: List<Song>) {
        favoriteSongsRecyclerView.adapter = SongHorizontalAdapter(favoriteSongs) { song ->
            val selectedIndex = favoriteSongs.indexOf(song)
            val action = HomeFragmentDirections.actionHomeFragmentToPlayMusicFragment(SongListWithIndex(favoriteSongs, selectedIndex))
            findNavController().navigate(action)
        }
    }

    private fun loadRecommendedSongs(page: Int, perPage: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val recommendedSongs = withContext(Dispatchers.IO) {
                // currently using random songs from list of all songs
                SongDao.getAllSongs(page, perPage)
            }
            recommendedSongsRecyclerView.adapter = SongHorizontalAdapter(recommendedSongs) { song ->
                val selectedIndex = recommendedSongs.indexOf(song)
                val action = HomeFragmentDirections.actionHomeFragmentToPlayMusicFragment(SongListWithIndex(recommendedSongs, selectedIndex))
                findNavController().navigate(action)
            }
        }
    }

    private fun updateGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        greetingTextView.text = greeting
    }
}
