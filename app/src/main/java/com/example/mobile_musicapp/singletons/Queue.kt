package com.example.mobile_musicapp.singletons

import com.example.mobile_musicapp.helpers.RandomHelper
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song

val RandomHelper = RandomHelper()

object Queue {
    private var songs = mutableListOf<Song>()
    private var playedSongs = mutableListOf<String>()
    private var currentSongIndex = 0
    private var shuffle = false
    
    fun addSong(song: Song) {
        songs.add(song)
    }
    
    fun removeSong(song: Song) {
        if (playedSongs.contains(song._id)) {
            playedSongs.remove(song._id)
        }
        if (songs.contains(song)) {
            songs.remove(song)
        }
    }

    fun getSongs(): MutableList<Song> {
        return songs
    }

    fun getCurrentSong(): Song? {
        return if (songs.isNotEmpty()) songs[currentSongIndex] else null
    }

    fun nextSong() {
        if (songs.isNotEmpty()) {
            playedSongs.add(songs[currentSongIndex]._id)

            if (shuffle) {
//              RandomHelper.getRandomSongIndex(playedSongs, songs.size - 1)
            } else {
                currentSongIndex = (currentSongIndex + 1) % songs.size
            }
        }
    }

    fun previousSong() {
        if (songs.isNotEmpty()) {
            if (shuffle) {
                if (playedSongs.isNotEmpty()) {
//                    currentSongIndex = playedSongs.last()
                }
            } else {
                if (currentSongIndex == 0) {
                    currentSongIndex = songs.size - 1
                } else {
                    currentSongIndex -= 1
                }
            }
        }
    }

    private fun clearQueue() {
        songs.clear()
        playedSongs.clear()
        currentSongIndex = 0
    }

    fun openPlaylist(playlist: MutableList<Song>, index: Int) {
        clearQueue()
        currentSongIndex = index
        songs.addAll(playlist)
    }

    fun openSong(song: Song) {
        if (songs.contains(song)) {
            currentSongIndex = songs.indexOf(song)
        }
    }

    fun openPlaylist(playlist: Playlist) {
        clearQueue()
        songs.addAll(playlist.songs)
        currentSongIndex = 0
    }
}