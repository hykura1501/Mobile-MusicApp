package com.example.mobile_musicapp.services

import android.util.Log
import com.example.mobile_musicapp.models.Song

class SongDao {
    companion object {

        suspend fun getNewReleaseSongs(page: Int, perPage: Int): List<Song> {
            return try {
                val response = RetrofitClient.instance.getNewReleaseSongs(page, perPage)
                if (response.isSuccessful) {
                    response.body()?.data ?: emptyList()
                } else {
                    Log.e("SongDao", "Error: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("SongDao", "Exception: ${e.message}")
                emptyList()
            }
        }

        suspend fun getTopLikesSongs(page: Int, perPage: Int): List<Song> {
            return try {
                val response = RetrofitClient.instance.getTopLikesSongs(page, perPage)
                if (response.isSuccessful) {
                    response.body()?.data ?: emptyList()
                } else {
                    Log.e("SongDao", "Error: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("SongDao", "Exception: ${e.message}")
                emptyList()
            }
        }

        suspend fun getPopularSongs(page: Int, perPage: Int): List<Song> {
            return try {
                val response = RetrofitClient.instance.getPopularSongs(page, perPage)
                if (response.isSuccessful) {
                    response.body()?.data ?: emptyList()
                } else {
                    Log.e("SongDao", "Error: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("SongDao", "Exception: ${e.message}")
                emptyList()
            }
        }
    }
}
