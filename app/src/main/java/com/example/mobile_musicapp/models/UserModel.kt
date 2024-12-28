package com.example.mobile_musicapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var _id: String = "",
    var fullName: String = "",
    var phone: String = "",
    var email: String = "",
    var deleted: Boolean = false,
    var favoriteSongs: MutableList<Song> = mutableListOf(),
    var avatar: String = ""
) : Parcelable