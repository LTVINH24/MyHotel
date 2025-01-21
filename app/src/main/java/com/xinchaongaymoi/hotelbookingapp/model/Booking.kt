package com.xinchaongaymoi.hotelbookingapp.model

data class Booking(
    val id: String = "",
    val roomId: String = "",
    val userId: String = "",
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val status: String = "pending",
    val checkoutStatus:String ="unpaid",
    val createdAt:String="",
    val totalPrice:Double=0.0
)
