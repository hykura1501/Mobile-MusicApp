package com.example.mobile_musicapp.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.RetrofitClient

class SongPagingSource : PagingSource<Int, Song>() {
    val TAG = "ItemPagingSource"
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val page = params.key ?: 1

        try {
            Log.d(TAG, "load: ===> loadSize = ${params.loadSize}")
            val response = RetrofitClient.instance.getSongByPage(page, params.loadSize)
            if (response.isSuccessful) {
                val items = response.body()?.data
//                items?.forEach { Log.d(TAG, "load: ===> data = $it") }
                val nextKey = if (items?.isEmpty() == true) null else page + 1
                Log.d(TAG, "load: ===> nextKey = ${nextKey}")
                return LoadResult.Page(
                    data = items ?: emptyList(),
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = nextKey
                )
            } else {
                return LoadResult.Error(Exception("API error"))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?: 1
        }
    }
}
