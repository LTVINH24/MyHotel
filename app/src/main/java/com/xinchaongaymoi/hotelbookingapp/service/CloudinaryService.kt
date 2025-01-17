package com.xinchaongaymoi.hotelbookingapp.service

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class CloudinaryService {
    fun uploadImage(context: Context, imageUri: Uri,callback:(String?)->Unit){
        val requestId = MediaManager.get().upload(imageUri)
            .unsigned("hotel_rooms")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val imageUri = resultData?.get("url") as?String
                    callback(imageUri)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    callback(null)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    TODO("Not yet implemented")
                }
            }).dispatch()
    }

}