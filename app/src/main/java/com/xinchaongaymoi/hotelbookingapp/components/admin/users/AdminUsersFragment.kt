package com.xinchaongaymoi.hotelbookingapp.components.admin.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                // TODO: Navigate to user detail screen
                Toast.makeText(context, "Xem chi tiết: ${user.name}", Toast.LENGTH_SHORT).show()
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
                    userSnapshot.getValue(User::class.java)?.let { user ->
                        users.add(user.copy(uid = userSnapshot.key ?: ""))
                    }
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
        userRef.child("isBanned").setValue(!user.isBanned)
            .addOnSuccessListener {
                val message = if (!user.isBanned) "Đã ban tài khoản" else "Đã unban tài khoản"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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