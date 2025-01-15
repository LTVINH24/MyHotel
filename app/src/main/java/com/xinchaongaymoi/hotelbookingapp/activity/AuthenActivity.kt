package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityAuthenBinding

class AuthenActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAuthenBinding
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
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
                                val userRef = FirebaseDatabase.getInstance().getReference("users")
                                    .child(user?.uid ?: "")
                                
                                val userData = hashMapOf(
                                    "email" to email,
                                    "role" to "user", // Mặc định role là user
                                    "name" to "", // Có thể thêm các trường khác
                                    "phone" to ""
                                )
                                
                                userRef.setValue(userData).addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                }
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
}