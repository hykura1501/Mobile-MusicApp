package com.example.mobile_musicapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SongRepository {
    val TAG = "SongRepository"
    fun getPagedItems(): Flow<PagingData<Song>> {
        return Pager(
            config = PagingConfig(
                pageSize = 200,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                SongPagingSource()
            }
        ).flow
    }
    fun getPagedItemsSmaller(): Flow<PagingData<Song>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                SongPagingSource()
            }
        ).flow
    }

    suspend fun getAllPlayedRecently() = withContext(Dispatchers.IO){
        try {
            val dataResult = RetrofitClient.instance.getAllPlayedRecently()
            dataResult.body()
        }catch (e : Exception){
            Log.d(TAG, "getAllPlayedRecently: ==> error = ${e.message}")
            null
        }
    }
    suspend fun addPlayedRecently(id : String) = withContext(Dispatchers.IO){
        try {
            val dataResult = RetrofitClient.instance.addPlayedRecently(id)
            dataResult.body()
        }catch (e : Exception){
            Log.d(TAG, "getAllPlayedRecently: ==> error = ${e.message}")
            null
        }
    }
}
