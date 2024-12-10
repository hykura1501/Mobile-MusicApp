package com.example.mobile_musicapp.singletons

import com.example.mobile_musicapp.models.Song

object Favorite {
    private var songs = mutableListOf<Song>()

    fun addToFavorites(song: Song) {
        songs.add(song)
    }

    fun removeFromFavorites(song: Song) {
        songs.remove(song)
    }

    fun getPlaylist(): List<Song> {
        return songs.toMutableList()
    }

    fun clearPlaylist() {
        songs.clear()
    }
}