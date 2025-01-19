// RecommendationManager.kt
package com.example.mobile_musicapp.helpers

import com.example.mobile_musicapp.models.Artist
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class RecommendationManager(private val apiService: ApiService) {

    suspend fun getRecommendedSongs(followingArtists: List<Artist>): List<Song> {
        return withContext(Dispatchers.IO) {
            val allSongs = followingArtists.flatMap { artist ->
                val response = apiService.getArtistDetails(artist.artistId)
                if (response.isSuccessful) {
                    val songs = response.body()?.data?.songs ?: emptyList()
                    if (songs.size <= 2) songs else songs.shuffled().take(Random.nextInt(3, 7))
                } else {
                    emptyList()
                }
            }
            allSongs.shuffled()
        }
    }
}