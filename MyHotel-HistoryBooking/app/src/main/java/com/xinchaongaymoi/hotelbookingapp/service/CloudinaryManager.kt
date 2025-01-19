package com.xinchaongaymoi.hotelbookingapp.service

import com.cloudinary.android.MediaManager
import com.xinchaongaymoi.hotelbookingapp.App

object CloudinaryManager {
    fun initCloudinary() {
        val config = mapOf(
            "cloud_name" to "dtjb7bepr",  // Thay bằng Cloud Name của bạn
            "api_key" to "957472666226116",        // Thay bằng API Key của bạn
            "api_secret" to "qJpRtuOr_JXygCWBSbxJ_BYROJk"  // Thay bằng API Secret của bạn
        )
        MediaManager.init(App.instance, config)
    }
}
