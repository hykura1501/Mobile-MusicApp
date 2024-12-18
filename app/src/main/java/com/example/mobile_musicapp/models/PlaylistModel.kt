package com.example.mobile_musicapp.models

data class Playlist(
    val playlistId: String,
    var title: String,
    var songs: MutableList<Song> = mutableListOf(),
)

