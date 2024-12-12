package com.example.mobile_musicapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongListWithIndex(
    val songs: List<Song>,
    val selectedIndex: Int
) : Parcelable
