package com.example.mobile_musicapp.services

import android.util.Log
import com.example.mobile_musicapp.models.LyricLine
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.parseLrcContent

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

        suspend fun getSongById(songId: String): Song? {
            return try {
                val response = RetrofitClient.instance.getSongById(songId)
                if (response.isSuccessful) {
                    response.body()?.data
                } else {
                    Log.e("SongDao", "Error: ${response.code()} - ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("SongDao", "Exception: ${e.message}")
                null
            }
        }

        suspend fun getAllSongs(page: Int, perPage: Int): List<Song> {
            return try {
                val response = RetrofitClient.instance.getAllSongs(page, perPage)
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

        suspend fun fetchLyrics(url: String): List<LyricLine> {
            return try {
                val response = RetrofitClient.instance.fetchLyrics(url)
                if (response.isSuccessful) {
                    val rawLyrics = response.body()?.string()
                    if (rawLyrics != null) {
                        parseLrcContent(rawLyrics)
                    } else {
                        Log.e("FetchLyrics", "Empty response body")
                        emptyList()
                    }
                } else {
                    Log.e("FetchLyrics", "Error: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("FetchLyrics", "Exception: ${e.message}")
                emptyList()
            }
        }


    }
}
