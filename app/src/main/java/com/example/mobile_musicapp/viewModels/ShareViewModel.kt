package com.example.mobile_musicapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobile_musicapp.models.Playlist

class ShareViewModel : ViewModel() {
    // library - new playlist
    val deletedPlaylist = MutableLiveData<Playlist>()

    val addedPlaylist = MutableLiveData<Playlist>()

    // library - playlist
    val selectedPlaylist = MutableLiveData<Playlist>()

    // library - bottom sheet dialog fragment
    val longSelectedPlaylist = MutableLiveData<Playlist>()
}
