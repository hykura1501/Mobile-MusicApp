package com.example.mobile_musicapp.singletons

import com.example.mobile_musicapp.models.Song

public object Queue {
    // Danh sách bài hát hiện tại
    private val playlist = mutableListOf<Song>()
    private var currentSongIndex = 0

    // Thêm bài hát vào danh sách phát
    fun addSongModel(song: Song) {
        playlist.add(song)
    }

    // Xóa bài hát khỏi danh sách phát
    fun removeSongModel(song: Song) {
        playlist.remove(song)
    }

    // Lấy toàn bộ danh sách phát
    fun getPlaylist(): List<Song> {
        return playlist.toList() // Trả về bản sao bất biến của danh sách
    }

    // Xóa toàn bộ danh sách phát
    fun clearPlaylist() {
        playlist.clear()
    }

    // Lấy bài hát hiện tại (nếu có)
    fun getCurrentSong(): Song? {
        return if (playlist.isNotEmpty()) playlist[currentSongIndex] else null
    }

    // Chuyển sang bài hát tiếp theo
    fun moveToNextSongModel() {
        if (playlist.isNotEmpty()) {
            playlist.removeAt(0) // Loại bỏ bài hát hiện tại
        }
    }

    fun nextSong() {
        if (playlist.isNotEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % playlist.size
        }
    }

    fun previousSong() {
        if (playlist.isNotEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + playlist.size) % playlist.size
        }
    }
}