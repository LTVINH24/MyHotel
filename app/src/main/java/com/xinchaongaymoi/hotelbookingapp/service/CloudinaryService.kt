package com.xinchaongaymoi.hotelbookingapp.service

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class CloudinaryService {
fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
    val requestId = MediaManager.get().upload(imageUri)
        .unsigned("hotel_rooms")
        .options(mapOf("folder" to "Mobile Project - XCNMHotelBookingApp","secure" to true))
        .callback(object : UploadCallback {
            override fun onStart(requestId: String) {}

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

            override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                val imageUrl = resultData?.get("url") as? String
                val secureUrl = imageUrl?.replace("http://", "https://")
                callback(secureUrl)
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                callback(null)
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {}
        })
        .dispatch()
}

}