package com.xinchaongaymoi.hotelbookingapp.components.admin.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.activity.LoginActivity
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminSettingsBinding
import com.xinchaongaymoi.hotelbookingapp.databinding.DialogEditProfileBinding

class AdminSettingsFragment : Fragment() {
    private var _binding: FragmentAdminSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        
        setupEditProfile()
        setupLogout()
        return binding.root
    }

    private fun setupEditProfile() {
        binding.btnEditProfile.setOnClickListener {
            val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
            
            // Lấy thông tin hiện tại của admin
            val currentUser = auth.currentUser?.uid
            if (currentUser != null) {
                db.collection("users").document(currentUser)
                    .get()
                    .addOnSuccessListener { document ->
                        dialogBinding.edtName.setText(document.getString("name"))
                        dialogBinding.edtEmail.setText(document.getString("email"))
                        dialogBinding.edtPhone.setText(document.getString("phone"))
                    }
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thay đổi thông tin")
                .setView(dialogBinding.root)
                .setPositiveButton("Lưu") { dialog, _ ->
                    val name = dialogBinding.edtName.text.toString()
                    val email = dialogBinding.edtEmail.text.toString()
                    val phone = dialogBinding.edtPhone.text.toString()

                    if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
                        updateProfile(name, email, phone)
                    } else {
                        Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
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

            db.collection("users").document(currentUser)
                .update(userUpdates as Map<String, Any>)
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