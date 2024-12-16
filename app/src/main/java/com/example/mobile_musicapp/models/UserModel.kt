package com.example.mobile_musicapp.models

import android.os.Parcel
import android.os.Parcelable

data class User (
    var _id: String = "",
    var fullName: String = "",
    var email: String = "",
    var deleted: Boolean = false,
    var favoriteSongs: MutableList<Song> = mutableListOf(),
    var avatar: String = ""
)