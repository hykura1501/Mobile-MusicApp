package com.example.mobile_musicapp.services
import android.content.Context
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.singletons.App
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class ApiResponsePlaylists(
    val code: Int,
    val data: List<Playlist>
)

data class ApiResponsePlaylist(
    val code: Int,
    val data: Playlist
)

data class CreatePlaylistRequest(
    val title: String
)

data class DeletePlaylistResponse(
    val code: Int,
    val message: String
)


interface ApiService {
    @GET("playlist")
    suspend fun getAllPlaylists(): Response<ApiResponsePlaylists>

    @GET("playlist/{id}")
    suspend fun getPlaylist(@Path("id") id: String): Response<ApiResponsePlaylist>

    @DELETE("playlist/{playlistId}")
    suspend fun deletePlaylist(@Path("playlistId") id: String): Response<DeletePlaylistResponse>

    @POST("playlist")
    suspend fun createPlaylist(
        @Body playlistRequest: CreatePlaylistRequest
    ): Response<ApiResponsePlaylist>
}



object RetrofitClient {
    private const val BASE_URL = "https://musicapp-api-fkq3.onrender.com/"

    private val authInterceptor = Interceptor { chain ->
        //val token = "Bearer ${getTokenFromPreferences()}"
        val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3NGNhMmQ2ZjMxYTY0Y2ViMTJiN2U2OCIsImlhdCI6MTczMzg4OTY4MSwiZXhwIjoxNzM0MDYyNDgxfQ.ic6ResHy20gLVqCKAkp9eHpVCJTY8qIBBMPXos33-_g"
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()
        chain.proceed(newRequest)
    }

    private fun getTokenFromPreferences(): String {
        val sharedPreferences = App.instance.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TOKEN", "") ?: ""
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
