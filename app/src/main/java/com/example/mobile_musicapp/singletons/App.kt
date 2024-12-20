package com.example.mobile_musicapp.singletons

import android.app.Application
import android.content.Context
import com.example.mobile_musicapp.helpers.LocalHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalHelper.setLanguageLocale(this)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
