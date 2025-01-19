package com.xinchaongaymoi.hotelbookingapp.model

data class Review(
    val id: String = "",
    val roomId: String = "",
    val userId: String = "",
    val comment: String = "",
    val rating: Float = 0f,
)
