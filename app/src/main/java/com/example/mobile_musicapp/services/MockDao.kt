package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song


class MockDao {
    fun getSampleSongList () : MutableList<Song>  {
        val song1 = Song(id = "1", title = "Forget Me Now", artist = "Fishy, Trí Dũng", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/asucyl70ouc1mpfij7lb.mp3")
        val song2 = Song(id = "2", title = "Tình đắng như ly cà phê", artist = "nger, nân", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/h9podlbqdzw4ll4lwcj7.mp3")
        val song3 = Song(id = "3", title = "Lạc trôi", artist = "Sơn Tùng MTP", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233301/song/mgickvp2w8cr6fs5mtqv.mp3")

        val songs = mutableListOf(song1, song2, song3)
        return songs
    }

    fun getSamplePlaylist () : Playlist {
        val song1 = Song(id = "1", title = "Forget Me Now", artist = "Fishy, Trí Dũng", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/asucyl70ouc1mpfij7lb.mp3")
        val song2 = Song(id = "2", title = "Tình đắng như ly cà phê", artist = "nger, nân", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/h9podlbqdzw4ll4lwcj7.mp3")
        val song3 = Song(id = "3", title = "Lạc trôi", artist = "Sơn Tùng MTP", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233301/song/mgickvp2w8cr6fs5mtqv.mp3")

        val playlist = Playlist(
            id = "playlist",
            name = "My Favorite Songs",
            description = "A collection of my favorite songs",
            songs = mutableListOf(song1, song2, song3)
        )
        return playlist
    }

    fun getSamplePlaylists () : MutableList<Playlist> {
        val song1 = Song(id = "1", title = "Forget Me Now", artist = "Fishy, Trí Dũng", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/asucyl70ouc1mpfij7lb.mp3")
        val song2 = Song(id = "2", title = "Tình đắng như ly cà phê", artist = "nger, nân", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/h9podlbqdzw4ll4lwcj7.mp3")
        val song3 = Song(id = "3", title = "Lạc trôi", artist = "Sơn Tùng MTP", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233301/song/mgickvp2w8cr6fs5mtqv.mp3")

        val playlist1 = Playlist(
            id = "playlist1",
            name = "My Favorite Songs",
            description = "A collection of my favorite songs",
            songs = mutableListOf(song1, song2, song3)
        )

        val playlist2 = Playlist(
            id = "playlist2",
            name = "My lofi songs",
            description = "A collection of lofi songs",
            songs = mutableListOf(song1, song2, song3)
        )

        val playlist3 = Playlist(
            id = "playlist3",
            name = "My rock songs",
            description = "A collection of rock songs",
            songs = mutableListOf(song1, song2, song3)
        )

        val playlist4 = Playlist(
            id = "playlist4",
            name = "My indie songs",
            description = "A collection of rock songs",
            songs = mutableListOf(song1, song2, song3)
        )

        val playlist = Playlist(
            id = "playlist0",
            name = "My super playlist",
            description = "A collection of super songs",
            songs = mutableListOf(song1, song2, song3, song1, song2, song3, song1, song2, song3, song1, song2, song3, song1, song2, song3, song1, song2, song3, song1, song2, song3)
        )

        val playlists = mutableListOf(playlist, playlist1, playlist2, playlist3, playlist4, playlist1, playlist2, playlist3, playlist4, playlist1, playlist2, playlist3, playlist4)
        return playlists
    }
}
