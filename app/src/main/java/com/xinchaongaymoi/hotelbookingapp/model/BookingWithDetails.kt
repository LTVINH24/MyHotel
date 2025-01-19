package com.xinchaongaymoi.hotelbookingapp.model

data class BookingWithDetails(
    val booking:Booking,
    val room:Room,
    val user:User
)
