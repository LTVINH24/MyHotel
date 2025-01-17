package com.xinchaongaymoi.hotelbookingapp.service

import com.cloudinary.android.MediaManager
import com.xinchaongaymoi.hotelbookingapp.App

object CloudinaryManager {
    fun initCloudinary() {
        val config = mapOf(
            "cloud_name" to "dtjb7bepr"  // Thay bằng Cloud Name của bạn
        )
        MediaManager.init(App.instance, config)
    }
}
