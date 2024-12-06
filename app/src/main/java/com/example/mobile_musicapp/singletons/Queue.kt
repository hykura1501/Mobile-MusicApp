package com.example.mobile_musicapp.singletons

import com.example.mobile_musicapp.helpers.RandomHelper
import com.example.mobile_musicapp.models.Song

val RandomHelper = RandomHelper()

object Queue {
    var songs = mutableListOf<Song>()
    private val playedSongs = mutableListOf<Int>()
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
        playedSongs.clear()
    }
    
    fun getCurrentSong(): Song? {
        return if (songs.isNotEmpty()) songs[currentSongIndex] else null
    }
    

    fun nextSong() {
        if (songs.isNotEmpty()) {
            playedSongs.add(currentSongIndex)
            currentSongIndex =
            if (shuffle) {
                RandomHelper.getRandomSongIndex(playedSongs, songs.size - 1)
            } else {
                (currentSongIndex + 1) % songs.size
            }
        }
    }

    fun previousSong() {
        if (songs.isNotEmpty()) {
            currentSongIndex =
            if (shuffle) {
                playedSongs.last()
            } else {
                (currentSongIndex - 1) % songs.size
            }
        }
    }
}