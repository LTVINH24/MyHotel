package com.xinchaongaymoi.hotelbookingapp.model
data class Room(
    val id:String="",
    val roomName:String ="",
    val mainImage:String ="",
    val roomType:String ="",
    val location:String="",
    val area:Double=0.0,
    val bedType:String="",
    val totalBed:Int=0,
    val maxGuests:Int=0,
    val pricePerNight:Double =0.0,
    val pricePerHour:Double=0.0,
    val utilities:String="",
    val sale:Double=0.0,
    val rating: Double=0.0
)
