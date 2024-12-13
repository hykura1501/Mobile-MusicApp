package com.example.mobile_musicapp.services

import android.util.Log
import com.example.mobile_musicapp.models.Song
import retrofit2.HttpException

class FavoriteSongDao {
    companion object {
        suspend fun getFavoriteSongs(): List<Song> {
            return try {
                val response = RetrofitClient.instance.getFavoriteSongs()
                if (response.isSuccessful) {
                    val favoriteSongsResponse = response.body()
                    val favoriteSongs = favoriteSongsResponse?.favoriteSongs ?: emptyList()
                    Log.d("FavoriteSongDao", "Fetched favorite songs: $favoriteSongs")
                    favoriteSongs
                } else {
                    Log.e("FavoriteSongDao", "Error: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("FavoriteSongDao", "Exception: ${e.message}")
                emptyList()
            }
        }

        suspend fun addOrRemoveFavoriteSong(songId: String): Result<String> {
            return try {
                val response = RetrofitClient.instance.addFavoriteSong(songId)
                if (response.isSuccessful) {
                    Result.success("Added to Favorites!")
                } else {
                    val removeResponse = RetrofitClient.instance.removeFavoriteSong(songId)
                    if (removeResponse.isSuccessful) {
                        Result.success("Removed from Favorites!")
                    } else {
                        Result.failure(Throwable("Failed to remove from Favorites"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Throwable("Exception: ${e.message}"))
            }
        }
    }
}
