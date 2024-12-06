package com.example.mobile_musicapp.models

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val url: String
)