package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.SongModel

public object CurrentPlaylist {
    // Danh sách bài hát hiện tại
    private val playlist = mutableListOf<SongModel>()
    private var currentSongIndex = 0

    // Thêm bài hát vào danh sách phát
    fun addSongModel(song: SongModel) {
        playlist.add(song)
    }

    // Xóa bài hát khỏi danh sách phát
    fun removeSongModel(song: SongModel) {
        playlist.remove(song)
    }

    // Lấy toàn bộ danh sách phát
    fun getPlaylist(): List<SongModel> {
        return playlist.toList() // Trả về bản sao bất biến của danh sách
    }

    // Xóa toàn bộ danh sách phát
    fun clearPlaylist() {
        playlist.clear()
    }

    // Lấy bài hát hiện tại (nếu có)
    fun getCurrentSong(): SongModel? {
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