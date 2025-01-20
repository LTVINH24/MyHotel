package com.xinchaongaymoi.hotelbookingapp.components.admin.users

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ServerValue
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.adapter.AdminUserAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminUsersBinding
import com.xinchaongaymoi.hotelbookingapp.model.User

class AdminUsersFragment : Fragment() {
    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: AdminUserAdapter
    private var valueEventListener: ValueEventListener? = null
    private var usersRef = FirebaseDatabase.getInstance().getReference("user")
    
    private var currentPage = 1
    private val itemsPerPage = 10
    private var searchQuery = ""
    private var totalUsers = 0

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
        setupSearch()
        setupPagination()
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

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString()?.lowercase() ?: ""
                currentPage = 1
                loadUsers()
            }
        })
    }

    private fun setupPagination() {
        binding.prevButton.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                loadUsers()
            }
        }

        binding.nextButton.setOnClickListener {
            val totalPages = (totalUsers + itemsPerPage - 1) / itemsPerPage
            if (currentPage < totalPages) {
                currentPage++
                loadUsers()
            }
        }

        binding.pageEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newPage = binding.pageEditText.text.toString().toIntOrNull()
                val totalPages = (totalUsers + itemsPerPage - 1) / itemsPerPage
                
                if (newPage != null && newPage in 1..totalPages) {
                    currentPage = newPage
                    loadUsers()
                    true
                } else {
                    Toast.makeText(context, "Số trang không hợp lệ", Toast.LENGTH_SHORT).show()
                    binding.pageEditText.setText(currentPage.toString())
                    false
                }
            } else {
                false
            }
        }
    }

    private fun loadUsers() {
        // Hủy listener cũ nếu có
        valueEventListener?.let { 
            usersRef.removeEventListener(it)
        }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Kiểm tra binding có tồn tại không
                if (_binding == null) return

                val allUsers = mutableListOf<User>()
                
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.toUser()
                    
                    if (searchQuery.isNotEmpty()) {
                        val nameMatch = user.name.lowercase().contains(searchQuery)
                        val emailMatch = user.email.lowercase().contains(searchQuery)
                        if (!nameMatch && !emailMatch) continue
                    }
                    
                    allUsers.add(user)
                }

                allUsers.sortWith(compareBy { it.name.lowercase() })
                
                totalUsers = allUsers.size
                val startIndex = (currentPage - 1) * itemsPerPage
                val endIndex = minOf(startIndex + itemsPerPage, totalUsers)
                
                updatePaginationUI()
                
                val pagedUsers = allUsers.subList(startIndex, endIndex)
                userAdapter.updateUsers(pagedUsers)
            }

            override fun onCancelled(error: DatabaseError) {
                if (_binding != null) {
                    Toast.makeText(context, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Đăng ký listener mới
        usersRef.orderByChild("name").addValueEventListener(valueEventListener!!)
    }

    private fun updatePaginationUI() {
        val totalPages = (totalUsers + itemsPerPage - 1) / itemsPerPage
        binding.pageEditText.setText(currentPage.toString())
        binding.totalPagesText.text = totalPages.toString()
        binding.prevButton.isEnabled = currentPage > 1
        binding.nextButton.isEnabled = currentPage < totalPages
    }

    private fun DataSnapshot.toUser(): User {
        return User(
            uid = key ?: "",
            email = child("email").getValue(String::class.java) ?: "",
            name = child("name").getValue(String::class.java) ?: "",
            phone = child("phone").getValue(String::class.java) ?: "",
            role = child("role").getValue(String::class.java) ?: "",
            isBanned = child("isBanned").getValue(Boolean::class.java) ?: false
        )
    }

    private fun toggleUserBan(user: User) {
        // Không cho phép ban tài khoản admin
        if (user.role == "admin") {
            Toast.makeText(context, "Không thể ban tài khoản admin", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("user").child(user.uid)
        val newBanStatus = !user.isBanned
        
        // Lấy current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        
        val updates = hashMapOf<String, Any>(
            "isBanned" to newBanStatus,
            "banTimestamp" to ServerValue.TIMESTAMP,
            "banReason" to (if (newBanStatus) "Tài khoản bị vô hiệu hóa bởi admin" else ""),
            "accountStatus" to (if (newBanStatus) "disabled" else "active")
        )

        // Chỉ thêm forceLogout nếu không phải là tài khoản admin đang đăng nhập
        if (user.uid != currentUserId) {
            updates["forceLogout"] = true
        }

        userRef.updateChildren(updates)
            .addOnSuccessListener {
                if (_binding != null) {
                    user.isBanned = newBanStatus
                    
                    val message = if (newBanStatus) {
                        "Đã vô hiệu hóa tài khoản ${user.email}"
                    } else {
                        "Đã kích hoạt lại tài khoản ${user.email}"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    
                    // Cập nhật UI
                    userAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                if (_binding != null) {
                    Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        // Hủy đăng ký listener trước khi hủy binding
        valueEventListener?.let {
            usersRef.removeEventListener(it)
        }
        super.onDestroyView()
        _binding = null
    }
} 