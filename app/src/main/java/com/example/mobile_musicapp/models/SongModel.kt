package com.example.mobile_musicapp.models

data class Song(
    val userId : String,
    var deleted : Boolean,
    val _id: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val thumbnail: String,
    val duration: Int,
    val album : String,
    var like : Int,
    var view : Int,
    val url: String,
    val lyric : String
)