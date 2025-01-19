package com.xinchaongaymoi.hotelbookingapp.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityLoginBinding
import com.xinchaongaymoi.hotelbookingapp.model.UserInfo


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var accountsPreferences: SharedPreferences
    private lateinit var database:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().reference
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        enableEdgeToEdge()
        setContentView(binding.root)
        Log.e("id",getString(R.string.default_web_client_id) )
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.loginGoogleBtn.setOnClickListener {
            signInWithGoogle()
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailLoginET.text.toString()
            val password = binding.passwordLoginET.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val userId = firebaseAuth.currentUser?.uid
                        fetchUserInfo(userId.toString(), password)
                        user?.let { firebaseUser ->
                            navigateBasedOnRole(firebaseUser.uid)
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Đăng nhập thất bại: ${it.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        val gson = Gson()
        val accountsJson = sharedPreferences.getString("accounts", "[]")
        val accountListType = object : TypeToken<MutableList<UserInfo>>() {}.type
        val accounts: MutableList<UserInfo> = gson.fromJson(accountsJson, accountListType)


        binding.linkSignUp.setOnClickListener{
            startActivity(Intent(this, AuthenActivity::class.java))
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        } else {
            Log.i("GoogleSignIn", "Failed with resultCode: ${result.resultCode}, data: ${result.data}")
            result.data?.extras?.let {
                for (key in it.keySet()) {
                    Log.i("GoogleSignInExtras", "Key: $key, Value: ${it[key]}")
                }
            }
        }
    }

    private fun handleResult(task:Task<GoogleSignInAccount>)
    {
        if(task.isSuccessful){
            Log.i("thanhcong","thanhcong")
            val account :GoogleSignInAccount?=task.result
            if(account!=null){
                sharedPreferences.edit().apply {
                    putString("displayName", account.displayName)
                    putString("email", account.email)
                    putString("photoUrl", account.photoUrl?.toString())
                    putString("id", account.id)
                    apply()
                }
                Log.i("SharedPreferences", "User info saved successfully")
                updateUI(account)
            }
        }
        else{
            Log.i("thatbai","thatbai")

            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()

        }
    }
    private fun updateUI(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful)
            {
                val intent =Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()

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

                    // Navigate to HomeActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "User info not found in database!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateBasedOnRole(userId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("user").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val isBanned = snapshot.child("isBanned").getValue(Boolean::class.java) ?: false
            if (isBanned) {
                val usersRef = FirebaseDatabase.getInstance().getReference("user")
                usersRef.orderByChild("role").equalTo("admin").limitToFirst(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var adminEmail = ""
                            for (adminSnapshot in snapshot.children) {
                                adminEmail = adminSnapshot.child("email").getValue(String::class.java) ?: ""
                                break
                            }
                            Toast.makeText(
                                this@LoginActivity,
                                "Tài khoản đã bị khóa. Vui lòng liên hệ email: $adminEmail để được hỗ trợ",
                                Toast.LENGTH_LONG
                            ).show()
                            firebaseAuth.signOut()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Tài khoản đã bị khóa. Vui lòng liên hệ admin để được hỗ trợ",
                                Toast.LENGTH_LONG
                            ).show()
                            firebaseAuth.signOut()
                        }
                    })
                return@addOnSuccessListener
            }

            val role = snapshot.child("role").value?.toString()
            when (role) {
                "admin" -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                }
                else -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("LoginDebug", "Error getting data", exception)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

