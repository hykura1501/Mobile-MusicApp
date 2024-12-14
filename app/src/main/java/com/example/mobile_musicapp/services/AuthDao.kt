package com.example.mobile_musicapp.services

class AuthDao {
    companion object {
        suspend fun login(username: String, password: String): String? {
            return try {
                val loginRequest = LoginRequest(username, password)
                val response = RetrofitClient.instance.login(loginRequest)
                if (response.isSuccessful) {
                    response.body()?.token
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
        suspend fun register(fullName: String, email: String, password: String): String? {
            return try {
                val registerRequest = RegisterRequest(fullName, email, password)
                val response = RetrofitClient.instance.register(registerRequest)
                if (response.isSuccessful) {
                    response.body()?.token
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}