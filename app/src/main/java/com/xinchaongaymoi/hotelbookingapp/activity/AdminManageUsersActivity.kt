package com.xinchaongaymoi.hotelbookingapp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityAdminManageUsersBinding

class AdminManageUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminManageUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hiển thị loading
        binding.progressBar.visibility = View.VISIBLE

        // TODO: Sẽ thêm RecyclerView Adapter và logic load users sau
    }
} 