package com.xinchaongaymoi.hotelbookingapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.activity.MainActivity

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM messages here.
        if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification!!.title ?: "Notification Title",
                remoteMessage.notification!!.body ?: "Notification Body"
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send the new token to your server or save it locally.
        Log.e("Token",token)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"
        val notificationId = 1001

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your app's notification icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
