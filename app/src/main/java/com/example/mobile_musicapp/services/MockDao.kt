package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.SongModel


class MockDao : IDao {
    override fun openSong(song: SongModel): String {
        val filename = "${song.id}.mp3"
        return filename
    }

    override fun openPlaylist (id : String) : Playlist {
        val song1 = SongModel(id = "1", title = "Forget Me Now", artist = "Fishy, Trí Dũng", duration = 210, url = "http://example.com/song1")
        val song2 = SongModel(id = "2", title = "Nơi này có anh", artist = "Sơn Tùng MTP", duration = 210, url = "http://example.com/song2")
        val song3 = SongModel(id = "3", title = "Em của ngày hôm qua", artist = "Sơn Tùng MTP", duration = 210, url = "http://example.com/song3")

        // sample playlist
        val playlist = Playlist(
            id = "playlist",
            name = "My Favorite Songs",
            description = "A collection of my favorite songs",
            songs = mutableListOf(song1, song2, song3)
        )
        return playlist
    }




}
