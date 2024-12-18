package com.example.mobile_musicapp.models

import com.example.mobile_musicapp.R

enum class PlaylistOption(val title: String, val iconResId: Int) {

    DELETE_PLAYLIST("Delete Playlist", R.drawable.ic_remove_playlist);


    companion object {
        fun fromTitle(title: String): PlaylistOption? {
            return entries.find { it.title == title }
        }

        fun getIconResId(title: String): Int {
            return fromTitle(title)?.iconResId ?: R.drawable.ic_add_to_playlist
        }
    }
}