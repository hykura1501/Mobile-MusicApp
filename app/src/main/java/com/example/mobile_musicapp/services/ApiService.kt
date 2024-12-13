package com.example.mobile_musicapp.services
import android.content.Context
import com.example.mobile_musicapp.models.CommentModel
import com.example.mobile_musicapp.models.CommentRequest
import com.example.mobile_musicapp.models.CommentResponse
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.singletons.App
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
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
import retrofit2.http.Query
import java.time.Duration
import java.util.concurrent.TimeUnit


data class ApiResponsePlaylists(
    val code: Int,
    val data: List<Playlist>
)
data class ApiResponseSong(
    val code: Int,
    val data: List<Song>
)

data class ApiResponsePlayedRecently(
    val code: Int,
    val message: String
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

data class ApiResponseSongs(
    val code: Int,
    val data: List<Song>
)

data class FavoriteSongsResponse(
    val code: Int,
    val favoriteSongs: List<Song>,
    val page: Int,
    val perPage: Int,
    val total: Int,
    val totalPage: Int
)



data class ApiResponseComment(
    val code: Int,
    val data : CommentModel
)
interface ApiService {
    @GET("playlist")
    suspend fun getAllPlaylists(): Response<ApiResponsePlaylists>
    @GET("song")
    suspend fun getSongByPage(@Query("page") page: Int, @Query("perPage") size: Int): Response<ApiResponseSong>

    @GET("playlist/{id}")
    suspend fun getPlaylist(@Path("id") id: String): Response<ApiResponsePlaylist>

    @DELETE("playlist/{playlistId}")
    suspend fun deletePlaylist(@Path("playlistId") id: String): Response<DeletePlaylistResponse>

    @POST("playlist")
    suspend fun createPlaylist(
        @Body playlistRequest: CreatePlaylistRequest
    ): Response<ApiResponsePlaylist>

    @GET("song/new-release")
    suspend fun getNewReleaseSongs(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseSongs>

    @GET("song/popular")
    suspend fun getPopularSongs(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseSongs>

    @GET("song/top-likes")
    suspend fun getTopLikesSongs(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseSongs>

    @GET("/song/favorite")
    suspend fun getFavoriteSongs(): Response<FavoriteSongsResponse>

    @POST("/song/favorite/add/{songId}")
    suspend fun addFavoriteSong(@Path("songId") songId: String): Response<Void>

    @DELETE("/song/favorite/remove/{songId}")
    suspend fun removeFavoriteSong(@Path("songId") songId: String): Response<Void>

    @POST("comment/{id}")
    suspend fun addComment( @Path("id") id: String, @Body body : CommentRequest): Response<ApiResponseComment>

    @GET("comment/{id}")
    suspend fun getAllCommentsById( @Path("id") id: String): Response<CommentResponse>

    @GET("other/recently-played")
    suspend fun getAllPlayedRecently(): Response<ApiResponseSong>

    @POST("other/recently-played/{id}")
    suspend fun addPlayedRecently(@Path("id") id : String): Response<ApiResponsePlayedRecently>

}



object RetrofitClient {
    private const val BASE_URL = "https://musicapp-api-fkq3.onrender.com/"

    private val authInterceptor = Interceptor { chain ->
        //val token = "Bearer ${getTokenFromPreferences()}"
        val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3NGNhMmQ2ZjMxYTY0Y2ViMTJiN2U2OCIsImlhdCI6MTczNDA5NjM2NCwiZXhwIjoxNzM0MjY5MTY0fQ.a6rq2ZIuZs2D4bNvs6lt-oYTUAXQEO50hIitBr_JM9Q"
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
        .connectTimeout(10, TimeUnit.SECONDS)  // Increase connect timeout
        .readTimeout(10, TimeUnit.SECONDS)     // Increase read timeout
        .writeTimeout(10, TimeUnit.SECONDS)    // Increase write timeout
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
