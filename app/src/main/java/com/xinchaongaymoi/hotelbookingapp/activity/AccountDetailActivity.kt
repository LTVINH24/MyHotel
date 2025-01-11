package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.xinchaongaymoi.hotelbookingapp.R

class AccountDetailActivity:AppCompatActivity() {
    private lateinit var name:EditText
    private lateinit var email:EditText
    private lateinit var phone:EditText
    private lateinit var password:EditText
    private lateinit var save:Button
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
        save = findViewById(R.id.savebtn)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val _name = sharedPreferences.getString("name","Unknown")
        val _email = sharedPreferences.getString("email","Unknown")
        val _phone = sharedPreferences.getString("name","Unknown")
        name.setText(_name.toString())
        email.setText(_email.toString())
        phone.setText(_phone.toString())
        save.setOnClickListener(){
            var newName = name.text.toString()
            var newEmail = email.text.toString()
            var newPhone = phone.text.toString()
            val id = sharedPreferences.getString("id","Unknown").toString()
            val database = FirebaseDatabase.getInstance()
            val userRef: DatabaseReference = database.getReference("user").child(id)
            val updates = mapOf<String, Any>(
                "name" to newName,
                "phone" to newPhone,
                "email" to newEmail
            )
            userRef.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Fields updated successfully")
                    Toast.makeText(this@AccountDetailActivity, "Updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Firebase", "Error: ${task.exception?.message}")
                }
            }
            val pass = password.text.toString()
            if (pass != "") {
                firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.currentUser?.updatePassword(pass)
            }
        }
    }
}