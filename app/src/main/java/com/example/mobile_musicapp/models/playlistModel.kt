package com.example.mobile_musicapp.models

data class Playlist(
    val id: String,
    var name: String,
    val description: String? = null,
    val songs: MutableList<Song> = mutableListOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun main() {

    val song1 = Song(id = "1", title = "Forget Me Now", artist = "Fishy, Trí Dũng", duration = 210, url = "http://example.com/song1")
    val song2 = Song(id = "2", title = "Nơi này có anh", artist = "Sơn Tùng MTP", duration = 210, url = "http://example.com/song2")

    // Tạo playlist
    val playlist = Playlist(
        id = "playlist1",
        name = "My Favorite Songs",
        description = "A collection of my favorite songs",
        songs = mutableListOf(song1, song2)
    )

    // In thông tin playlist
    println("Playlist: ${playlist.name}")
    println("Description: ${playlist.description}")
    println("Songs:")
    playlist.songs.forEach { song ->
        println("- ${song.title} by ${song.artist}")
    }
}
