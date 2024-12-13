package com.example.mobile_musicapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_musicapp.models.Song

@Suppress("RemoveExplicitTypeArguments")
class PlayerBarViewModel : ViewModel() {
    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _repeatMode = MutableLiveData<Boolean>(false)
    val repeatMode: LiveData<Boolean> = _repeatMode

    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> = _currentSong

    private val _currentPosition = MutableLiveData<Int>(0)
    val currentPosition: LiveData<Int> = _currentPosition

    fun togglePlayPause() {
        val currentState = _isPlaying.value ?: false
        _isPlaying.value = !currentState
    }

    fun updatePlayPause(state: Boolean)  {
        _isPlaying.value = state
    }

    fun toggleRepeatMode() {
        val currentMode = _repeatMode.value ?: false
        _repeatMode.value = !currentMode
    }

    fun updateSong(newSong: Song) {
        _currentSong.value = newSong
    }

    fun updatePosition(newPosition: Int) {
        _currentPosition.value = newPosition
    }
}