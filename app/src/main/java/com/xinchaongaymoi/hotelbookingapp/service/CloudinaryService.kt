package com.xinchaongaymoi.hotelbookingapp.service

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager

class CloudinaryService {
    fun uploadImage(context: Context, imageUri: Uri,callback:(String?)->Unit){
        val requestId = MediaManager.get().upload(imageUri)
    }

}