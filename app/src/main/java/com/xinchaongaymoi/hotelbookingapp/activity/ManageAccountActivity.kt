package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.adapter.AccountAdapter
import com.xinchaongaymoi.hotelbookingapp.components.account.AccountManager
import com.xinchaongaymoi.hotelbookingapp.model.UserAccount

class ManageAccountsActivity : AppCompatActivity() {
    private lateinit var rvAccounts: RecyclerView
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_accounts)

        rvAccounts = findViewById(R.id.rvAccounts)
        rvAccounts.layoutManager = LinearLayoutManager(this)

        // Load saved accounts
        val accounts = AccountManager.getAccounts(this)
        rvAccounts.adapter = AccountAdapter(this, accounts) { account ->
            switchAccount(account)
        }
    }

    private fun switchAccount(account: UserAccount) {
        when (account.loginType) {
            "email-password" -> {
                // Use email and password for login
                auth.signInWithEmailAndPassword(account.email, "user-password") // Replace with stored password securely
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            AccountManager.setLastUsedAccount(this, account.userId)
                            finish() // Close the activity after switching
                        } else {
                            // Handle error
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
                            finish()
                        } else {
                            // Handle error
                        }
                    }
            }
        }
    }
}
