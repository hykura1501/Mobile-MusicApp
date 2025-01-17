package com.example.mobile_musicapp.helpers

import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.SongDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecommendationManager(private val favoriteSongs: List<Song>) {

    suspend fun getRecommendedSongs(): List<Song> {
        val allSongs = withContext(Dispatchers.IO) {
            SongDao.getAllSongs(page = 1, perPage = 1000000)
        }
        val recommendedSongs = mutableListOf<Song>()

        if (allSongs.isEmpty() || favoriteSongs.isEmpty()) {
            return recommendedSongs
        }

        favoriteSongs.forEach { favoriteSong ->
            val sameArtistSongs = allSongs.filter { it.artistId == favoriteSong.artistId && it.title != favoriteSong.title }
            if (sameArtistSongs.isNotEmpty()) {
                val randomNumber = (3..7).random()
                recommendedSongs.addAll(sameArtistSongs.take(randomNumber))
            }
        }

        recommendedSongs.shuffle()
        return recommendedSongs
    }
}