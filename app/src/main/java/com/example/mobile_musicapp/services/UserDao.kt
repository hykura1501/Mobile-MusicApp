package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.User

class UserDao {
    companion object {
        suspend fun getMe(): User? {
            return try {
                val response = RetrofitClient.instance.getMe()
                if (response.isSuccessful) {
//                    response.body()?.data
                    null
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}