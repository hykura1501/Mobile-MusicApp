package com.example.mobile_musicapp.models

data class Playlist(
    val id: String,
    var name: String,
    val description: String? = null,
    val songs: MutableList<Song> = mutableListOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

