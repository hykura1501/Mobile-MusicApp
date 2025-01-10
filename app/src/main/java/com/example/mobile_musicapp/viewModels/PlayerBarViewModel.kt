package com.example.mobile_musicapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_musicapp.models.Song

@Suppress("RemoveExplicitTypeArguments")
class PlayerBarViewModel : ViewModel() {
    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _shuffleMode = MutableLiveData<Boolean>(false)
    val shuffleMode: LiveData<Boolean> = _shuffleMode

    private val _repeatMode = MutableLiveData<Boolean>(false)
    val repeatMode: LiveData<Boolean> = _repeatMode

    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> = _currentSong

    private val _currentPosition = MutableLiveData<Long>(0)
    val currentPosition: LiveData<Long> = _currentPosition

    private val _deleteSong = MutableLiveData<Song>()
    val deleteSong: LiveData<Song> = _deleteSong

    private val _waiting = MutableLiveData<Boolean>(false)
    val waiting: LiveData<Boolean> = _waiting

    fun updateWaiting(waiting: Boolean) {
        _waiting.value = waiting
    }

    fun togglePlayPause() {
        val currentState = _isPlaying.value ?: false
        _isPlaying.value = !currentState
    }

    fun updatePlayPause(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun toggleRepeatMode() {
        val currentMode = _repeatMode.value ?: false
        _repeatMode.value = !currentMode
    }

    fun toggleShuffleMode() {
        val currentMode = _shuffleMode.value ?: false
        _shuffleMode.value = !currentMode
    }

    fun updateSong(newSong: Song) {
        _currentSong.value = newSong
    }

    fun updatePosition(newPosition: Long) {
        _currentPosition.value = newPosition
    }

    fun deleteSong(deleteSong: Song) {
        _deleteSong.value = deleteSong
    }
}