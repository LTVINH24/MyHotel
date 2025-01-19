package com.xinchaongaymoi.hotelbookingapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.xinchaongaymoi.hotelbookingapp.R

class ReviewActivity : AppCompatActivity() {
    private lateinit var postButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_review)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val roomID = intent.getStringExtra("roomid")
        Log.d("ReviewActivity", "Room ID: $roomID")
        if (roomID.isNullOrEmpty()) {
            Log.e("Error", "roomID is null or empty")
        }
        postButton = findViewById(R.id.postBtn)
        postButton.setOnClickListener {
            Log.d("ReviewActivity", "Button clicked")
            val database = Firebase.database
            val myRef = database.getReference("rooms/$roomID")
            Log.d("ReviewActivity", "Database reference: $myRef")
            val rating = findViewById<RatingBar>(R.id.RatingBar).rating
            val content = findViewById<EditText>(R.id.multiLineEditText).text.toString()
            val review = mapOf(
                "rating" to rating,
                "content" to content
            )
            myRef.child("reviews").push().setValue(review)
                .addOnSuccessListener {
                    Log.d("Firebase", "Review added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to add review", e)
                }
            Toast.makeText(this, "Review added successfully", Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }
    }
}