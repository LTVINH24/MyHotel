package com.xinchaongaymoi.hotelbookingapp.service

import com.cloudinary.android.MediaManager
import com.xinchaongaymoi.hotelbookingapp.App

object CloudinaryManager {
    fun initCloudinary() {
        val config = mapOf(
            "cloud_name" to "dtjb7bepr",
            "api_key" to "957472666226116",
            "api_secret" to "qJpRtuOr_JXygCWBSbxJ_BYROJk"
        )
        MediaManager.init(App.instance, config)
    }
}
