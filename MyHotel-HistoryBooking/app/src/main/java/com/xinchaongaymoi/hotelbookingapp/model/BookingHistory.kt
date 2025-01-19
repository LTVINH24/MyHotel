package com.xinchaongaymoi.hotelbookingapp.model

data class BookingHistory(
    val booking: Booking = Booking(),
    val room:Room = Room()
)
