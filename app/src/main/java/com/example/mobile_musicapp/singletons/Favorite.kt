package com.example.mobile_musicapp.singletons

import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.FavoriteSongDao
import com.example.mobile_musicapp.viewModels.FavoritesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

object Favorite {
    private var songs = mutableListOf<Song>()

    fun addToFavorites(song: Song) {
        songs.add(song)
    }

    fun removeFromFavorites(song: Song) {
        songs.remove(song)
    }

    fun getPlaylist(): List<Song> {
        return songs.toMutableList()
    }

    fun clearPlaylist() {
        songs.clear()
    }

    fun fetchFavoriteSongs(viewModel: FavoritesViewModel) {
        viewModel.setLoading(true)
        CoroutineScope(Dispatchers.Main).launch {
            val favoriteSongs = withContext(Dispatchers.IO) {
                FavoriteSongDao.getFavoriteSongs()
            }
            Log.d("Favorite", "Fetched Favorite Songs: $favoriteSongs")
            clearPlaylist()
            favoriteSongs.forEach { song ->
                addToFavorites(song)
            }
            viewModel.setFavoriteSongs(favoriteSongs)
            Log.d("Favorite", "Favorite Singleton Updated: ${getPlaylist()}")
        }
    }
}
