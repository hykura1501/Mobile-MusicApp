package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song


class MockDao : IDao {
    override fun openSong(song: Song): String {
        val filename = "${song.id}.mp3"
        return filename
    }

    override fun openPlaylist (id : String) : MutableList<Song>  {
        val song1 = Song(id = "1", title = "Forget Me Now", artist = "Fishy, Trí Dũng", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/asucyl70ouc1mpfij7lb.mp3")
        val song2 = Song(id = "2", title = "Tình đắng như ly cà phê", artist = "nger, nân", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233292/song/h9podlbqdzw4ll4lwcj7.mp3")
        val song3 = Song(id = "3", title = "Lạc trôi", artist = "Sơn Tùng MTP", duration = 210, url = "https://res.cloudinary.com/dixonjeab/video/upload/v1733233301/song/mgickvp2w8cr6fs5mtqv.mp3")

        val songs = mutableListOf(song1, song2, song3)
        return songs
    }
}
