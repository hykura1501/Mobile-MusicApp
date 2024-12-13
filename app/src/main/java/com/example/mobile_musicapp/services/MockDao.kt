package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song


class MockDao {
    fun getSampleSongList () : MutableList<Song>  {
        val song1 = Song(userId = "1", deleted = false, _id = "1", title = "Forget Me Now", artistName = "Fishy, Trí Dũng", artistId = "1", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/asucyl70ouc1mpfij7lb.mp3", lyric = "hello")
        val song2 = Song(userId = "1", deleted = false, _id = "2", title = "Tình đắng như ly cà phê", artistName = "nger, nân", artistId = "2", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/h9podlbqdzw4ll4lwcj7.mp3", lyric = "hello")
        val song3 = Song(userId = "1", deleted = false, _id = "2", title = "Lạc trôi", artistName = "Sơn Tùng MTP", artistId = "3", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233301/song/mgickvp2w8cr6fs5mtqv.mp3", lyric = "hello")

        val songs = mutableListOf(song1, song2, song3)
        return songs
    }

    fun getSamplePlaylist () : Playlist {
        val song1 = Song(userId = "1", deleted = false, _id = "1", title = "Forget Me Now", artistName = "Fishy, Trí Dũng", artistId = "1", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/asucyl70ouc1mpfij7lb.mp3", lyric = "hello")
        val song2 = Song(userId = "1", deleted = false, _id = "2", title = "Tình đắng như ly cà phê", artistName = "nger, nân", artistId = "2", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/h9podlbqdzw4ll4lwcj7.mp3", lyric = "hello")
        val song3 = Song(userId = "1", deleted = false, _id = "2", title = "Lạc trôi", artistName = "Sơn Tùng MTP", artistId = "3", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233301/song/mgickvp2w8cr6fs5mtqv.mp3", lyric = "hello")

        val playlist = Playlist(
            playlistId = "playlist",
            title = "My Favorite Songs",
            songs = mutableListOf(song1, song2, song3)
        )
        return playlist
    }

    fun getSamplePlaylists () : MutableList<Playlist> {
        val song1 = Song(userId = "1", deleted = false, _id = "1", title = "Forget Me Now", artistName = "Fishy, Trí Dũng", artistId = "1", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/asucyl70ouc1mpfij7lb.mp3", lyric = "hello")
        val song2 = Song(userId = "1", deleted = false, _id = "2", title = "Tình đắng như ly cà phê", artistName = "nger, nân", artistId = "2", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/h9podlbqdzw4ll4lwcj7.mp3", lyric = "hello")
        val song3 = Song(userId = "1", deleted = false, _id = "2", title = "Lạc trôi", artistName = "Sơn Tùng MTP", artistId = "3", thumbnail = "hello", duration = 210, album = "hello", like = 0, view = 0, path = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233301/song/mgickvp2w8cr6fs5mtqv.mp3", lyric = "hello")

        val playlist1 = Playlist(
            playlistId = "playlist1",
            title = "My Favorite Songs",
            songs = mutableListOf(song1, song2, song3)
        )

        val playlist2 = Playlist(
            playlistId = "playlist2",
            title = "My lofi songs",
            songs = mutableListOf(song1, song2, song3)
        )

        val playlist3 = Playlist(
            playlistId = "playlist3",
            title = "My rock songs",
            songs = mutableListOf(song1, song2, song3)
        )

        val playlist4 = Playlist(
            playlistId = "playlist4",
            title = "My indie songs",
            songs = mutableListOf(song1, song2, song3)
        )

        val playlist = Playlist(
            playlistId = "playlist0",
            title = "My super playlist",
            songs = mutableListOf(song1, song2, song3, song1, song2, song3, song1, song2, song3, song1, song2, song3, song1, song2, song3, song1, song2, song3, song1, song2, song3)
        )

        val playlists = mutableListOf(playlist, playlist1, playlist2, playlist3, playlist4)
        return playlists
    }
}
