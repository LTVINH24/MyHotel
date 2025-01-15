package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Lấy email của admin và hiển thị
        val currentUser = firebaseAuth.currentUser
        binding.welcomeText.text = "Xin chào, ${currentUser?.email ?: "Admin"}!"

        // Xử lý sự kiện click button quản lý users
        binding.btnManageUsers.setOnClickListener {
            startActivity(Intent(this, AdminManageUsersActivity::class.java))
        }
    }
} 