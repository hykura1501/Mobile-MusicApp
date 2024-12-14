package com.example.mobile_musicapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_musicapp.models.Song

class FavoritesViewModel : ViewModel() {

    private val _favoriteSongs = MutableLiveData<List<Song>>()
    val favoriteSongs: LiveData<List<Song>> get() = _favoriteSongs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setFavoriteSongs(songs: List<Song>) {
        _favoriteSongs.value = songs
        _isLoading.value = false
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun addFavorite(song: Song) {
        val currentFavorites = _favoriteSongs.value?.toMutableList() ?: mutableListOf()
        currentFavorites.add(song)
        _favoriteSongs.value = currentFavorites
    }

    fun removeFavorite(song: Song) {
        val currentFavorites = _favoriteSongs.value?.toMutableList() ?: mutableListOf()
        currentFavorites.remove(song)
        _favoriteSongs.value = currentFavorites
    }
}
