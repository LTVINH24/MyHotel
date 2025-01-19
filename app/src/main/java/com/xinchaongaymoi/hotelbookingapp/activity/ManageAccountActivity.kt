package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.adapter.AccountAdapter
import com.xinchaongaymoi.hotelbookingapp.components.account.AccountManager
import com.xinchaongaymoi.hotelbookingapp.components.account.AccountManager.saveAccounts
import com.xinchaongaymoi.hotelbookingapp.model.UserAccount
import com.google.firebase.database.*

class ManageAccountsActivity : AppCompatActivity() {
    private lateinit var rvAccounts: RecyclerView
    private val auth = FirebaseAuth.getInstance()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_accounts)

        rvAccounts = findViewById(R.id.rvAccounts)
        rvAccounts.layoutManager = LinearLayoutManager(this)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().reference
        // Load saved accounts
        val accounts = AccountManager.getAccounts(this)
        Log.e("alluser",accounts.toString())
        rvAccounts.adapter = AccountAdapter(this, accounts) { account ->
            Toast.makeText(this,"Switching...",Toast.LENGTH_SHORT).show()
            switchAccount(account)
        }

    }

    private fun switchAccount(account: UserAccount) {
        when (account.loginType) {
            "email-password" -> {
                // Use email and password for login
                auth.signInWithEmailAndPassword(account.email, account.password) // Replace with stored password securely
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            AccountManager.setLastUsedAccount(this, account.userId)
                            val userId = auth.currentUser?.uid
                            val user = auth.currentUser
                            fetchUserInfo(userId.toString(), account.password)
                            Toast.makeText(this,"Switching...",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.e("Switch", "Error when re-login")
                        }
                    }
            }

            "google" -> {
                // Use Google credentials to login
                val credential = GoogleAuthProvider.getCredential(null, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            AccountManager.setLastUsedAccount(this, account.userId)
                        } else {
                            // Handle error
                        }
                    }
            }
        }
    }
    private fun fetchUserInfo(userId: String, password:String) {
        database.child("user").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val email = snapshot.child("email").getValue(String::class.java) ?: "Unknown"
                    val phone = snapshot.child("phone").getValue(String::class.java) ?: "Unknown"
                    // Save user info in SharedPreferences
                    sharedPreferences.edit().apply {
                        putString("id",userId)
                        putString("name", name)
                        putString("email", email)
                        putString("phone", phone)
                        apply()
                    }
                    val account = UserAccount(
                        userId = userId,
                        email = email ?: "",
                        displayName = name ?: "User",
                        loginType = "email-password",
                        password = password
                    )
                    Log.e("phone",phone)
                    saveAccounts(this@ManageAccountsActivity, listOf(account))
                    // Navigate to HomeActivity
                    val intent = Intent(this@ManageAccountsActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ManageAccountsActivity, "User info not found in database!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageAccountsActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
