package com.example.mobile_musicapp.models

import com.example.mobile_musicapp.R

enum class Option(val title: String, val iconResId: Int) {
    ADD_TO_PLAYLIST("Add to Playlist", R.drawable.ic_add_to_playlist),
//    HIDE_THIS_SONG("Hide This Song", R.drawable.ic_hide),
//    GO_TO_ARTIST("Go to Artist", R.drawable.ic_artist),
//    DELETE_SONG("Delete Song", R.drawable.ic_delete),
    SHARE("Share", R.drawable.ic_share);

    companion object {
        fun fromTitle(title: String): Option? {
            return entries.find { it.title == title }
        }

        fun getIconResId(title: String): Int {
            return fromTitle(title)?.iconResId ?: R.drawable.ic_add_to_playlist
        }
    }
}