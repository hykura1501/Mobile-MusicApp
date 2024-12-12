package com.example.mobile_musicapp.models

import android.os.Parcel
import android.os.Parcelable

data class Song(
    val userId: String = "",
    var deleted: Boolean = false,
    val _id: String = "",
    val title: String = "",
    val artistName: String = "",
    val artistId: String = "",
    val thumbnail: String = "",
    val duration: Int = 0,
    val album: String = "",
    var like: Int = 0,
    var view: Int = 0,
    val url: String = "",
    val lyric: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeByte(if (deleted) 1 else 0)
        parcel.writeString(_id)
        parcel.writeString(title)
        parcel.writeString(artistName)
        parcel.writeString(artistId)
        parcel.writeString(thumbnail)
        parcel.writeInt(duration)
        parcel.writeString(album)
        parcel.writeInt(like)
        parcel.writeInt(view)
        parcel.writeString(url)
        parcel.writeString(lyric)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}
