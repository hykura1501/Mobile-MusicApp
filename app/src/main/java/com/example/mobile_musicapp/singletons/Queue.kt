package com.example.mobile_musicapp.singletons

import com.example.mobile_musicapp.helpers.RandomHelper
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song

val RandomHelper = RandomHelper()

object Queue {
    var title = ""
    private var songs = mutableListOf<Song>()
    private var playedSongs = mutableListOf<Int>()
    private var currentSongIndex = 0
    private var shuffle = false
    
    fun addSong(song: Song) {
        songs.add(song)
    }
    
    fun removeSong(song: Song) {
        if (playedSongs.contains(songs.indexOf(song))) {
            playedSongs.remove(songs.indexOf(song))
        }
        if (songs.contains(song)) {
            songs.remove(song)
        }
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
            if (playedSongs.isNotEmpty()) {
                if (shuffle) {
                    currentSongIndex = playedSongs.last()
                    playedSongs.remove(currentSongIndex)
                } else {
                    currentSongIndex = (currentSongIndex - 1) % songs.size
                }
            }
        }
    }

    private fun clearQueue() {
        songs.clear()
        playedSongs.clear()
        currentSongIndex = 0
    }

    fun openPlaylist(playlist: Playlist) {
        clearQueue()
        songs.addAll(playlist.songs)
        title = playlist.title
    }
}