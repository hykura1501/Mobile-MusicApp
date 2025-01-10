package com.example.mobile_musicapp.models

import com.example.mobile_musicapp.R

enum class Option(val title: String, val iconResId: Int) {
    ADD_TO_PLAYLIST("Add to Playlist", R.drawable.ic_add_to_playlist),
    ADD_TO_QUEUE("Add to Queue", R.drawable.ic_queue),
    COMMENT("Comment", R.drawable.ic_comment),
    REMOVE_FROM_PLAYLIST("Remove from Playlist", R.drawable.ic_remove_from_playlist),
    REMOVE_FROM_QUEUE("Remove from Queue", R.drawable.ic_remove_from_playlist),
    DOWNLOAD("Download", R.drawable.ic_download),
    REPEAT("Repeat", R.drawable.ic_repeat),
    SHARE("Share", R.drawable.ic_share),
    SLEEP_TIMER("Sleep Timer", R.drawable.ic_sleep_timer),
    GO_TO_QUEUE("Go to Queue", R.drawable.ic_queue);

    companion object {
        fun fromTitle(title: String): Option? {
            return entries.find { it.title == title }
        }

        fun getIconResId(title: String): Int {
            return fromTitle(title)?.iconResId ?: R.drawable.ic_add_to_playlist
        }
    }
}