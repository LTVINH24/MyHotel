package com.xinchaongaymoi.hotelbookingapp

import android.app.Application
import com.xinchaongaymoi.hotelbookingapp.service.CloudinaryManager

class App : Application() {

    public lateinit var language: String

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        language = getString(R.string.english)
        instance = this
        CloudinaryManager.initCloudinary()
    }
}
