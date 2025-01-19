package com.example.mobile_musicapp.services

import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
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
    var premiumExpiredAt: Date = Date()

    /**
     * Clear user data when user logs out or data reset is required.
     */
    private fun clear() {
        _id = ""
        fullName = ""
        phone = ""
        email = ""
        deleted = false
        favoriteSongs.clear()
        avatar = ""
        isPremium = false
        premiumExpiredAt = Date()
    }

    /**
     * Set user information.
     */

    private fun parseIsoDate(isoDate: String): Date? {
        return try {
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            isoFormatter.parse(isoDate)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
        premiumExpiredAt = parseIsoDate(user.premiumExpiredAt) ?: Date()
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
