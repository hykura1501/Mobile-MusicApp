package com.example.mobile_musicapp.models

data class Artist(
    val deleted: Boolean,
    val _id: String,
    val artistId: String,
    val name: String,
    val avatar: String,
    val description: String,
    val birthday: String,
    val followers: String
)