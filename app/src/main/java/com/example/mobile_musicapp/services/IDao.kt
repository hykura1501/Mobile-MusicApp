package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.SongModel

interface IDao {
    fun openSong(song: SongModel) : String
    fun openPlaylist(id : String) : Playlist
}