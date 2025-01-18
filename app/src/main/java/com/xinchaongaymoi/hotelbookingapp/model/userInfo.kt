package com.xinchaongaymoi.hotelbookingapp.model

import com.google.android.gms.auth.api.identity.SignInPassword

data class UserInfo(
    var email: String = "",
    var name: String = "",
    var phone: String = "",
)
data class UserAccount(
    val userId: String,
    val email: String,
    val displayName: String,
    val loginType: String // "email-password" or "google"
)
