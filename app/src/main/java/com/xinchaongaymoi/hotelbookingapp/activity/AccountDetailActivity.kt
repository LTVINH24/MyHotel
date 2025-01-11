package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_detail)

        name = findViewById(R.id.pro_name)
        email = findViewById(R.id.pro_email)
        password = findViewById(R.id.pro_password)
        phone = findViewById(R.id.pro_phone)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val _name = sharedPreferences.getString("name","Unknown")
        val _email = sharedPreferences.getString("email","Unknown")
        val _phone = sharedPreferences.getString("name","Unknown")
        name.setText(_name.toString())
        email.setText(_email.toString())
        phone.setText(_phone.toString())
    }
}