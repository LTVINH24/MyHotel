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
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityRegisterBinding
import com.google.firebase.database.*
import com.xinchaongaymoi.hotelbookingapp.model.UserInfo

class AuthenActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
                                user?.let { saveUserToDatabase(it, _name, _phone) }
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
    fun saveUserToDatabase(user: FirebaseUser, _name:String, _phone:String) {
        val database = FirebaseDatabase.getInstance()
        val userRef: DatabaseReference = database.getReference("user").child(user.uid)

        val userData = UserInfo(
            email = user.email ?: "No Email",
            name = _name,
            phone = _phone
        )
        sharedPreferences.edit().apply {
            putString("id",user.uid)
            putString("name", _name)
            putString("email", user.email)
            putString("phone", _phone)
            apply()
        }
        userRef.setValue(userData)
            .addOnSuccessListener {
                Log.d("SaveUser", "User data saved successfully!")
            }
            .addOnFailureListener { error ->
                Log.e("SaveUser", "Error saving user data: ${error.message}")
            }
    }

}