package com.example.mobile_musicapp.repository

import android.util.Log
import com.example.mobile_musicapp.models.CommentModel
import com.example.mobile_musicapp.models.CommentRequest
import com.example.mobile_musicapp.models.CommentResponse
import com.example.mobile_musicapp.models.Data
import com.example.mobile_musicapp.models.UserInfo
import com.example.mobile_musicapp.models.UserResponse
import com.example.mobile_musicapp.services.ApiResponseComment
import com.example.mobile_musicapp.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import java.util.Date

class CommentRepository {
    val TAG = "CommentRepository"
    suspend fun addComment(songId : String, contentData : String) = withContext(Dispatchers.IO) {
        try {
            val apiComment = async {
                RetrofitClient.instance.addComment(songId, CommentRequest(contentData))
            }
            val apiInfo = async {
                RetrofitClient.instance.getInformationUser()
            }
            val (dataCommentResponse , dataInfoResponse) = awaitAll(apiComment, apiInfo)
            val commentModel = CommentModel(UserInfo("",""),"")
            commentModel.apply {
                if (dataCommentResponse.isSuccessful) {
                    dataCommentResponse.body()?.let {

                            content = (it as? ApiResponseComment)?.data?.content ?: ""
                            createdAt = (it as? ApiResponseComment)?.data?.createdAt ?: Date()

                    }
                }
                if (dataInfoResponse.isSuccessful) {
                    dataInfoResponse.body()?.let {
                        val dataUser = it as? UserResponse
                        userInfo._id = dataUser?.data?._id ?: ""
                        userInfo.fullName = dataUser?.data?.fullName ?: ""

                    }
                }
            }

            Log.d(TAG, "addComment: =====> comment = $commentModel")
            CommentResponse(code = dataCommentResponse.code(), data = listOf(commentModel))
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