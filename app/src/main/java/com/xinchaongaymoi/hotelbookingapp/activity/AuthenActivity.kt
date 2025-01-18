package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityAuthenBinding
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xinchaongaymoi.hotelbookingapp.components.account.AccountManager
import com.xinchaongaymoi.hotelbookingapp.components.account.AccountManager.saveAccounts
import com.xinchaongaymoi.hotelbookingapp.model.UserAccount
import com.xinchaongaymoi.hotelbookingapp.model.UserInfo

class AuthenActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAuthenBinding
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        binding = ActivityAuthenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth=FirebaseAuth.getInstance()
        val signUpBtn=binding.signUpBtn
        signUpBtn.setOnClickListener{
            val email= binding.emailET.text.toString()
            val password =binding.passwordET.text.toString()
           val regex= Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")
            if(email.isNotEmpty()&&password.isNotEmpty()){
                if(!regex.matches(password)){
                    Toast.makeText(this,"Password: 8+ chars, uppercase, lowercase, number, special char",Toast.LENGTH_SHORT).show()
                }
                else{
                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener{
                            if(it.isSuccessful){
                                val user = firebaseAuth.currentUser
                                val _name = binding.nameET.text.toString()
                                val _phone = binding.phoneET.text.toString()
                                user?.let { saveUserToDatabase(it, _name, _phone, password) }
                                val intent = Intent(this, OTPConfirmActivity::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            else{
                Toast.makeText(this,"Fields cannot be empty",Toast.LENGTH_SHORT).show()

            }
        }
        val loginBtn=binding.linkLogin
        loginBtn.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    fun saveUserToDatabase(user: FirebaseUser, _name: String, _phone: String, password:String) {
        val database = FirebaseDatabase.getInstance()
        val userRef: DatabaseReference = database.getReference("user").child(user.uid)

        val userData = UserInfo(
            email = user.email ?: "No Email",
            name = _name,
            phone = _phone
        )

        // Save current user info in UserPrefs
        sharedPreferences.edit().apply {
            putString("id", user.uid)
            putString("name", _name)
            putString("email", user.email)
            putString("phone", _phone)
            apply()
        }

        // Retrieve the existing accounts
        val existingAccounts = AccountManager.getAccounts(this).toMutableList()

        // Check if the account already exists
        val userAccount = UserAccount(
            userId = user.uid,
            email = user.email ?: "",
            displayName = _name ?: "user",
            loginType = "email-password",
            password = password
        )
        if (existingAccounts.none { it.userId == user.uid }) {
            // Add new account to the list
            existingAccounts.add(userAccount)
        }

        // Save the updated accounts list
        saveAccounts(this, existingAccounts)

        // Save the current account as the last used account
        AccountManager.setLastUsedAccount(this, user.uid)

        // Save user data in Firebase
        userRef.setValue(userData)
            .addOnSuccessListener {
                Log.d("SaveUser", "User data saved successfully!")
            }
            .addOnFailureListener { error ->
                Log.e("SaveUser", "Error saving user data: ${error.message}")
            }
    }

}