package com.xinchaongaymoi.hotelbookingapp.components.admin.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.adapter.AdminUserAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminUsersBinding
import com.xinchaongaymoi.hotelbookingapp.model.User

class AdminUsersFragment : Fragment() {
    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: AdminUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = AdminUserAdapter(
            onUserDetail = { user ->
                val bundle = Bundle().apply {
                    putString("user_name", user.name)
                    putString("user_email", user.email)
                    putString("user_phone", user.phone)
                    putString("user_role", user.role)
                    putBoolean("user_is_banned", user.isBanned)
                }
                findNavController().navigate(R.id.action_users_to_detail, bundle)
            },
            onToggleBan = { user ->
                toggleUserBan(user)
            }
        )

        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }
    }

    private fun loadUsers() {
        val usersRef = FirebaseDatabase.getInstance().getReference("user")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val uid = userSnapshot.key ?: ""
                    val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                    val name = userSnapshot.child("name").getValue(String::class.java) ?: ""
                    val phone = userSnapshot.child("phone").getValue(String::class.java) ?: ""
                    val role = userSnapshot.child("role").getValue(String::class.java) ?: ""
                    val isBanned = userSnapshot.child("isBanned").getValue(Boolean::class.java) ?: false
                    
                    users.add(User(uid, email, name, phone, role, isBanned))
                }
                userAdapter.updateUsers(users)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleUserBan(user: User) {
        val userRef = FirebaseDatabase.getInstance().getReference("user").child(user.uid)
        val newBanStatus = !user.isBanned
        
        userRef.child("isBanned").setValue(newBanStatus)
            .addOnSuccessListener {
                user.isBanned = newBanStatus
                
                val message = if (newBanStatus) {
                    "Đã ban tài khoản ${user.email}"
                } else {
                    "Đã unban tài khoản ${user.email}"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 