package com.xinchaongaymoi.hotelbookingapp.components.admin.settings

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.activity.LoginActivity
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminSettingsBinding
import com.xinchaongaymoi.hotelbookingapp.databinding.DialogEditProfileBinding
import android.util.Log

class AdminSettingsFragment : Fragment() {
    private var _binding: FragmentAdminSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        
        setupEditProfile()
        setupLogout()
        return binding.root
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        )
    }

    private fun setupEditProfile() {
        binding.btnEditProfile.setOnClickListener {
            val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thay đổi thông tin")
                .setView(dialogBinding.root)
                .setPositiveButton("Lưu") { _, _ ->
                    val name = dialogBinding.edtName.text.toString().trim()
                    val email = dialogBinding.edtEmail.text.toString().trim()
                    val phone = dialogBinding.edtPhone.text.toString().trim()

                    if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
                        updateProfile(name, email, phone)
                    } else {
                        Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .create()

            auth.currentUser?.let { user ->
                dialogBinding.edtEmail.setText(user.email)
                
                database.getReference("user").child(user.uid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            dialogBinding.edtName.setText(snapshot.child("name").getValue(String::class.java))
                            dialogBinding.edtPhone.setText(snapshot.child("phone").getValue(String::class.java))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("ProfileDebug", "Error getting data", error.toException())
                        }
                    })
            }
            
            dialog.show()
        }
    }

    private fun updateProfile(name: String, email: String, phone: String) {
        val currentUser = auth.currentUser?.uid
        if (currentUser != null) {
            val userUpdates = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone
            )

            database.getReference("user").child(currentUser)
                .updateChildren(userUpdates as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(context, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 