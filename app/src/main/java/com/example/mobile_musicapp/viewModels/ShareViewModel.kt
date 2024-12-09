package com.example.mobile_musicapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_musicapp.models.Playlist

class ShareViewModel : ViewModel() {
    // library - new playlist
    private val _shareDataPlaylist = MutableLiveData<MutableList<Playlist>>(mutableListOf())
    val shareDataPlaylist: MutableLiveData<MutableList<Playlist>> get() = _shareDataPlaylist
    fun add(playlist: Playlist) {
        _shareDataPlaylist.value?.apply {
            add(playlist)
            _shareDataPlaylist.value = this
        }
    }

    // library - playlist
    val selectedPlaylist = MutableLiveData<Playlist>()

}
