package com.example.mobile_musicapp.helpers

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.mobile_musicapp.helpers.SharedPreferencesUtils.getLanguageCode
import java.util.Locale
import kotlin.math.log

object LocalHelper {
    fun setLanguageLocale(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales = LocaleList.forLanguageTags(getLanguageCode(context))
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(
                    getLanguageCode(context)
                )
            )
        }
    }
}