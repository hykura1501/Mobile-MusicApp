package com.example.mobile_musicapp.repository

import android.util.Log
import com.example.mobile_musicapp.models.CommentRequest
import com.example.mobile_musicapp.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

class CommentRepository {
    val TAG = "CommentRepository"
    suspend fun addComment(songId : String, content : String) = withContext(Dispatchers.IO) {
        try {
            val dataResponse = RetrofitClient.instance.addComment(songId, CommentRequest(content))
            if (dataResponse.isSuccessful) {
                dataResponse.body()
            }else {
                null
            }
        }catch (e : Exception){
            Log.d(TAG, "addComment: ====> e = ${e.message}")
            null
        }
      
    }
    suspend fun getAllCommentsById(id : String) = withContext(Dispatchers.IO){
        try {
            val dataResponse = RetrofitClient.instance.getAllCommentsById(id)
            if (dataResponse.isSuccessful){
                dataResponse.body()
            }else {
                null
            }
        }catch (e : Exception ){
            Log.d(TAG, "getAllCommentsById: ====> e = ${e.message}")
            null
        }
       
    }
}