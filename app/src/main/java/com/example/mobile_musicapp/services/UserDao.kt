package com.example.mobile_musicapp.services

import android.widget.Toast
import com.example.mobile_musicapp.models.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserDao {
    companion object {
        suspend fun getMe(): User? {
            return try {
                val response = RetrofitClient.instance.getMe()
                if (response.isSuccessful) {
                    response.body()?.data
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        suspend fun updateMe(fullName: String, email: String, phone: String, avatarFile: File?): User? {
            return try {
                val fullNamePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())

                val avatarPart = avatarFile?.let {
                    val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("avatar", it.name, requestFile)
                }

                val response = RetrofitClient.instance.updateMe(fullNamePart, emailPart, phonePart, avatarPart)
                if (response.isSuccessful) {
                    response.body()?.data
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        suspend fun updateMeWithoutAvatar(fullName: String, email: String, phone: String): User? {
            return try {
                val fullNamePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.instance.updateMeWithoutAvatar(fullNamePart, emailPart, phonePart)

                if (response.isSuccessful) {
                    response.body()?.data
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }
}