package com.example.mobile_musicapp.singletons

import com.example.mobile_musicapp.models.Song

object Queue {
    var songs = mutableListOf<Song>()
    private val playedSongs = mutableListOf<Song>()
    private var currentSongIndex = 0
    private var shuffle = false
    
    fun addSongModel(song: Song) {
        songs.add(song)
    }
    
    fun removeSongModel(song: Song) {
        songs.remove(song)
    }
    
    fun getPlaylist(): List<Song> {
        return songs.toMutableList()
    }
    
    fun clearPlaylist() {
        songs.clear()
    }
    
    fun getCurrentSong(): Song? {
        return if (songs.isNotEmpty()) songs[currentSongIndex] else null
    }
    

    fun nextSong() {
        if (songs.isNotEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % songs.size
        }
    }

    fun previousSong() {
        if (songs.isNotEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + songs.size) % songs.size
        }
    }
}