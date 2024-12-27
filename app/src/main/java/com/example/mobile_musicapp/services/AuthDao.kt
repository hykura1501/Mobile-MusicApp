package com.example.mobile_musicapp.services

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject


sealed class AuthApiResult {
    data class Success(val data: ApiResponseAuth?) : AuthApiResult()
    data class Error(val code: Int, val message: String) : AuthApiResult()
}

class AuthDao {
    companion object {
        suspend fun login(username: String, password: String): AuthApiResult? {
            return try {
                val loginRequest = LoginRequest(username, password)
                val response = RetrofitClient.instance.login(loginRequest)
                if (response.isSuccessful) {
                    AuthApiResult.Success(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = Gson().fromJson(errorBody, JsonObject::class.java)
                    val errorMessage = errorJson["message"]?.asString ?: "Unknown error"
                    val statusCode = errorJson["code"]?.asInt ?: -1
                    AuthApiResult.Error(statusCode, errorMessage)
                }
            } catch (e: Exception) {
                null
            }
        }
        suspend fun register(fullName: String, email: String, password: String): AuthApiResult? {
            return try {
                val registerRequest = RegisterRequest(fullName, email, password)
                val response = RetrofitClient.instance.register(registerRequest)
                if (response.isSuccessful) {
                    AuthApiResult.Success(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = Gson().fromJson(errorBody, JsonObject::class.java)
                    val errorMessage = errorJson["message"]?.asString ?: "Unknown error"
                    val statusCode = errorJson["code"]?.asInt ?: -1
                    AuthApiResult.Error(statusCode, errorMessage)
                }
            } catch (e: Exception) {
                null
            }
        }
        suspend fun loginGoogle(idToken: String): AuthApiResult? {
            return try {
                val response = RetrofitClient.instance.loginGoogle(GoogleLoginRequest(idToken))
                if (response.isSuccessful) {
                    AuthApiResult.Success(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = Gson().fromJson(errorBody, JsonObject::class.java)
                    val errorMessage = errorJson["message"]?.asString ?: "Unknown error"
                    val statusCode = errorJson["code"]?.asInt ?: -1
                    AuthApiResult.Error(statusCode, errorMessage)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}