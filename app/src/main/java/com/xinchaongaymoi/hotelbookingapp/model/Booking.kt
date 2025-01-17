package com.xinchaongaymoi.hotelbookingapp.model

data class Booking(
    val id: String = "",
    val roomId: String = "",
    val userId: String = "",
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val status: String = "",
    val checkoutStatus:String ="",
    val createdAt:String="",
    val totalPrice:Double=0.0
)
