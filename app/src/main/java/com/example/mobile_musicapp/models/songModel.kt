package com.example.mobile_musicapp.models

data class SongModel(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val url: String
)