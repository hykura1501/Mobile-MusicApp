package com.example.mobile_musicapp.playlist

data class Playlist(
    val id: String,
    val name: String,
    val description: String? = null,
    val songs: MutableList<Song> = mutableListOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val album: String? = null,
    val url: String         // Đường dẫn đến bài hát (local hoặc trực tuyến)
)

fun main() {

    val song1 = Song(id = "1", title = "Song A", artist = "Artist A", duration = 210, url = "http://example.com/song1")
    val song2 = Song(id = "2", title = "Song B", artist = "Artist B", duration = 180, url = "http://example.com/song2")

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
