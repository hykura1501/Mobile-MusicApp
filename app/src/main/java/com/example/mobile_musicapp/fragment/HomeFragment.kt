package com.example.mobile_musicapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.adapters.SongAdapter
import com.example.mobile_musicapp.adapters.SongHorizontalAdapter
import com.example.mobile_musicapp.services.FavoriteSongDao
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.SongListWithIndex
import com.example.mobile_musicapp.services.SongDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var greetingTextView: TextView
    private lateinit var newReleaseSongsRecyclerView: RecyclerView
    private lateinit var popularSongsRecyclerView: RecyclerView
    private lateinit var favoriteSongsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        greetingTextView = view.findViewById(R.id.greetingTextView)
        newReleaseSongsRecyclerView = view.findViewById(R.id.newReleaseSongsRecyclerView)
        popularSongsRecyclerView = view.findViewById(R.id.popularSongsRecyclerView)
        favoriteSongsRecyclerView = view.findViewById(R.id.favouriteSongsRecyclerView)

        setupRecyclerViews()
        loadNewReleaseSongs(1, 10)
        loadPopularSongs(1, 10)
        loadFavoriteSongs()
        updateGreeting()
    }

    private fun setupRecyclerViews() {
        newReleaseSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        popularSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        favoriteSongsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun loadNewReleaseSongs(page: Int, perPage: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val newReleaseSongs = withContext(Dispatchers.IO) {
                SongDao.getNewReleaseSongs(page, perPage)
            }
            newReleaseSongsRecyclerView.adapter = SongHorizontalAdapter(newReleaseSongs) { song ->
                val songsArray = newReleaseSongs.toTypedArray()
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
                val songsArray = popularSongs.toTypedArray()
                val selectedIndex = popularSongs.indexOf(song)
                val action = HomeFragmentDirections.actionHomeFragmentToPlayMusicFragment(SongListWithIndex(popularSongs, selectedIndex))
                findNavController().navigate(action)
            }
        }
    }

    private fun loadFavoriteSongs() {
        CoroutineScope(Dispatchers.Main).launch {
            val favoriteSongs = withContext(Dispatchers.IO) {
                FavoriteSongDao.getFavoriteSongs()
            }
            Log.d("HomeFragment", "Favorite Songs: $favoriteSongs")
            favoriteSongsRecyclerView.adapter = SongHorizontalAdapter(favoriteSongs) { song ->
                val songsArray = favoriteSongs.toTypedArray()
                val selectedIndex = favoriteSongs.indexOf(song)
                val action = HomeFragmentDirections.actionHomeFragmentToPlayMusicFragment(SongListWithIndex(favoriteSongs, selectedIndex))
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
