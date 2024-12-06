package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song

interface IDao {
    fun openSong(song: Song) : String
    fun openPlaylist(id : String) : Playlist
}