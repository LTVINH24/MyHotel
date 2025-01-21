package com.xinchaongaymoi.hotelbookingapp.model

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: String = "",
    var isBanned: Boolean = false
) 