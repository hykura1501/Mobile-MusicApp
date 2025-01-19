package com.example.mobile_musicapp.services

import android.widget.Toast
import com.example.mobile_musicapp.models.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

sealed class UserApiResult {
    data class Success(val code: Int, val message: String) : UserApiResult()
    data class Error(val code: Int, val message: String) : UserApiResult()
}


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
        suspend fun upgradeToPremium(day: Int): Void? {
            return try {
                var premiumRequest = PremiumRequest(day)
                val response = RetrofitClient.instance.upgradeToPremium(premiumRequest)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        suspend fun changePassword(oldPassword: String, newPassword: String, confirmNewPassword: String): UserApiResult? {
            return try {
                var changePasswordRequest = ChangePasswordRequest(oldPassword, newPassword, confirmNewPassword)
                val response = RetrofitClient.instance.changePassword(changePasswordRequest)
                if (response.isSuccessful) {
                    UserApiResult.Success(response.code(), response.message())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = Gson().fromJson(errorBody, JsonObject::class.java)
                    val errorMessage = errorJson["message"]?.asString ?: "Unknown error"
                    val statusCode = errorJson["code"]?.asInt ?: -1
                    UserApiResult.Error(statusCode, errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }
}