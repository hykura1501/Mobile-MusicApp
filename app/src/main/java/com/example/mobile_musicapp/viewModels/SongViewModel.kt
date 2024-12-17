package com.example.mobile_musicapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.repository.SongRepository
import com.example.mobile_musicapp.services.ApiResponsePlayedRecently
import kotlinx.coroutines.launch

class SongViewModel : ViewModel() {
    val TAG = "SongViewModel"
    private val songRepository = SongRepository()
    private val _songsListLiveData = MutableLiveData(listOf<Song>())
    val songsListLiveData : LiveData<List<Song>> = _songsListLiveData

    private val _playRecentlyLiveData = MutableLiveData<ApiResponsePlayedRecently?>(null)
    private val playRecentlyLiveData : LiveData<ApiResponsePlayedRecently?> = _playRecentlyLiveData
    private val cachedPlayedRecently = mutableListOf<Song>()
    private val cachedPlaylist = mutableListOf<Playlist>()

    fun getCachedPlayedRecently() = cachedPlayedRecently
    fun getCachedPlaylist() = cachedPlaylist

    fun getAllPlayedRecently() {
        viewModelScope.launch {
            songRepository.getAllPlayedRecently()?.let {
                cachedPlayedRecently.clear()
                cachedPlayedRecently.addAll(it.data)
                _songsListLiveData.postValue(it.data)
                Log.d(TAG, "getAllPlayedRecently: ====> ${it.data.size}")
            }?.run {
                Log.d(TAG, "getAllPlayedRecently: ====> NULL")
            }
        }
    }

    fun addPlayedRecently(id : String) {
        viewModelScope.launch {
            songRepository.addPlayedRecently(id)?.let {
                _playRecentlyLiveData.postValue(it)
            }?.run {
                Log.d(TAG, "getAllPlayedRecently: ====> NULL")
                _playRecentlyLiveData.postValue(null)
            }
        }
    }
}