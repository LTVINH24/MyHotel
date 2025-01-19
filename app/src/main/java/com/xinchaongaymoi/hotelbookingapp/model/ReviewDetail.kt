package com.xinchaongaymoi.hotelbookingapp.model

import androidx.compose.material3.DatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReviewDetail(
    val review: Review,
    val user: User,
    val room: Room
)
{
    fun getFormattedDate():String
    {
        val sdf = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
        return sdf.format(Date(review.createdAt))
    }
}

