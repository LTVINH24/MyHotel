package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityAuthenBinding

class AccountDetailActivity:AppCompatActivity() {
    private lateinit var name:EditText
    private lateinit var email:EditText
    private lateinit var phone:EditText
    private lateinit var password:EditText
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_detail)

        name = findViewById(R.id.pro_name)
        email = findViewById(R.id.pro_email)
        password = findViewById(R.id.pro_password)
        phone = findViewById(R.id.pro_phone)

        name.setText("")
        email.setText("")
        password.setText("")
        phone.setText("")
    }
}