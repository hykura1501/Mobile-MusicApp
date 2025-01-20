package com.example.mobile_musicapp.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object UserManager {
    var _id: String = ""
    var fullName: String = ""
    var phone: String = ""
    var email: String = ""
    var deleted: Boolean = false
    var favoriteSongs: MutableList<Song> = mutableListOf()
    var avatar: String = ""
    var isPremium: Boolean = false
    var premiumExpiredAt: String = ""

    /**
     * Clear user data when user logs out or data reset is required.
     */
     fun clear() {
        _id = ""
        fullName = ""
        phone = ""
        email = ""
        deleted = false
        favoriteSongs.clear()
        avatar = ""
        isPremium = false
        premiumExpiredAt = ""
    }

    private fun setUserInfo(user: User?) {
        _id = user!!._id
        fullName = user.fullName
        phone = user.phone
        email = user.email
        deleted = user.deleted
        favoriteSongs = user.favoriteSongs.toMutableList() // Copy to avoid reference issues
        avatar = user.avatar
        isPremium = user.isPremium
        premiumExpiredAt = user.premiumExpiredAt
    }

    /**
     * Fetch current user asynchronously.
     * This requires being called from a Coroutine Scope.
     */
    suspend fun fetchCurrentUser() {
        try {
            val userResponse = withContext(Dispatchers.IO) {
                UserDao.getMe()
            }

            if (userResponse != null) {
                setUserInfo(userResponse)
            } else {
                clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            clear()
        }
    }
}
