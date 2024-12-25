package com.example.mobile_musicapp.services

import android.util.Log
import com.example.mobile_musicapp.models.Playlist

class PlaylistDao {
    companion object {

        suspend fun getAllPlaylists(): List<Playlist> {
            return try {
                val response = RetrofitClient.instance.getAllPlaylists()
                if (response.isSuccessful) {
                    response.body()?.data ?: emptyList()
                } else {
                    Log.e("PlaylistDao", "Error: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("PlaylistDao", "Exception: ${e.message}")
                emptyList()
            }
        }

        suspend fun getPlaylist(id: String): Playlist? {
            return try {
                val response = RetrofitClient.instance.getPlaylist(id)
                if (response.isSuccessful) {
                    response.body()?.data
                } else {
                    Log.e("PlaylistDao", "Error: ${response.code()} - ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("PlaylistDao", "Exception: ${e.message}")
                null
            }
        }

        suspend fun deletePlaylist(id: String): Boolean {
            return try {
                val response = RetrofitClient.instance.deletePlaylist(id)
                if (response.isSuccessful) {
                    Log.d("PlaylistDao", "Message: ${response.body()?.message}")
                    true
                } else {
                    Log.e("PlaylistDao", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("PlaylistDao", "Exception: ${e.message}")
                false
            }
        }

        suspend fun createPlaylist(title: String): Playlist? {
            return try {
                val response = RetrofitClient.instance.createPlaylist(CreatePlaylistRequest(title))
                if (response.isSuccessful) {
                    response.body()?.data
                } else {
                    Log.e("PlaylistDao", "Error: ${response.code()} - ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("PlaylistDao", "Exception: ${e.message}")
                null
            }
        }

        suspend fun addSongToPlaylist(playlistId: String, songId: String) {
            try {
                RetrofitClient.instance.addSongToPlaylist(
                    playlistId,
                    AddSongToPlaylistRequest(songId)
                )
            } catch (e: Exception) {
                Log.e("PlaylistDao", "Exception: ${e.message}")
            }
        }
    }
}