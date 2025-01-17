package com.xinchaongaymoi.hotelbookingapp

import android.app.Application
import com.xinchaongaymoi.hotelbookingapp.service.CloudinaryManager

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CloudinaryManager.initCloudinary()
    }
}
