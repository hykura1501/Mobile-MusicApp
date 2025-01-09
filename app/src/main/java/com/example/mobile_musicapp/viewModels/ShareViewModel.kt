package com.example.mobile_musicapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.PlaylistDao
import kotlinx.coroutines.launch

class ShareViewModel : ViewModel() {
    // Playlist
    val playlists = MutableLiveData<MutableList<Playlist>>(mutableListOf())

    fun addPlaylist(newPlaylistTitle: String) {
        val currentPlaylists = playlists.value ?: mutableListOf()
        viewModelScope.launch {
            val newPlaylist = PlaylistDao.createPlaylist(newPlaylistTitle)
            currentPlaylists.add(newPlaylist!!)
            playlists.value = currentPlaylists
        }
    }

    fun updatePlaylist(playlistId: String) {
        val currentPlaylists = playlists.value ?: mutableListOf()
        viewModelScope.launch {
            val updatedPlaylist = PlaylistDao.getPlaylist(playlistId)
            if (updatedPlaylist != null) {
                val index = currentPlaylists.indexOfFirst { it.playlistId == playlistId }
                if (index != -1) {
                    currentPlaylists[index] = updatedPlaylist
                    playlists.postValue(currentPlaylists)
                }
            }
        }
    }

    fun addAllPlaylists(playlists: MutableList<Playlist>) {
        this.playlists.value = playlists
    }

    fun removePlaylist(playlistToRemove: Playlist?) {
        playlistToRemove?.let {
            viewModelScope.launch {
                val isDeleted = PlaylistDao.deletePlaylist(playlistToRemove.playlistId)
                if (isDeleted) {
                    val currentPlaylists = playlists.value?.toMutableList() ?: mutableListOf()
                    currentPlaylists.remove(it)
                    playlists.value = currentPlaylists
                }
            }
        }
    }

    fun getAllPlaylists() : MutableList<Playlist> {
        return playlists.value ?: mutableListOf()
    }

    // Song
    val removedSong = MutableLiveData<Song>()

    // Short press playlist
    val selectedPlaylist = MutableLiveData<Playlist>()

    // Long press playlist
    val longSelectedPlaylist = MutableLiveData<Playlist>()

    // Long press song
    val selectedSong = MutableLiveData<Song>()

    // Go to PlayMusicFragment
    val navigateToPlayMusicFragment = MutableLiveData<Boolean>()

    // Removed song in playlist
    val removedSongInPlaylist = MutableLiveData<Song>()

}
