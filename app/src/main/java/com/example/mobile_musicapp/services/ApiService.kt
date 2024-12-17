package com.example.mobile_musicapp.services
import android.content.Context
import android.content.SharedPreferences
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.User
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
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class ApiResponseSong(
    val code: Int,
    val data: Song
)

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

data class ApiResponseSongs(
    val code: Int,
    val data: List<Song>
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName : String,
    val email: String,
    val password: String
)

data class ApiResponseAuth(
    val code: Int,
    val token: String,
    val message: String
)
data class FavoriteSongsResponse(
    val code: Int,
    val favoriteSongs: List<Song>,
    val page: Int,
    val perPage: Int,
    val total: Int,
    val totalPage: Int
)

data class UserResponse(
    val code: Int,
    val data: User
)



interface ApiService {
    // playlist ----------------------------------------------------------------
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

    // song ----------------------------------------------------------------
    @GET("song/detail/{songId}")
    suspend fun getSongById(@Path("songId") songId: String): Response<ApiResponseSong>

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

    // auth ----------------------------------------------------------------
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<ApiResponseAuth>

    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<ApiResponseAuth>

    @GET("user/me")
    suspend fun getMe(): Response<UserResponse>
}

object TokenManager {
    private const val PREFS_NAME = "MyApp"
    private const val TOKEN_KEY = "TOKEN"

    fun saveToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getToken(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, "") ?: ""
    }
}


object RetrofitClient {
    private const val BASE_URL = "https://musicapp-api-fkq3.onrender.com/"

    private val authInterceptor = Interceptor { chain ->
        val token = "Bearer ${TokenManager.getToken(App.instance)}"
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()
        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)  // Increase connect timeout
        .readTimeout(60, TimeUnit.SECONDS)     // Increase read timeout
        .writeTimeout(60, TimeUnit.SECONDS)    // Increase write timeout
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
