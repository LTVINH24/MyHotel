package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityBookingBinding
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.service.BookingService
import com.xinchaongaymoi.hotelbookingapp.service.RoomService
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log
import com.bumptech.glide.Glide
import android.content.Intent
import android.os.Build
import android.view.View

class ReviewActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BookingActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}