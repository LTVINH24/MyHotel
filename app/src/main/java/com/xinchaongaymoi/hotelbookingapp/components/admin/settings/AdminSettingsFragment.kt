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
import com.google.firebase.auth.EmailAuthProvider
import androidx.appcompat.app.AlertDialog
import com.xinchaongaymoi.hotelbookingapp.databinding.DialogChangePasswordBinding

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
        setupChangePassword()
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
                    val phone = dialogBinding.edtPhone.text.toString().trim()

                    if (name.isNotEmpty() && phone.isNotEmpty()) {
                        updateProfile(name, auth.currentUser?.email ?: "", phone)
                    } else {
                        Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .create()

            auth.currentUser?.let { user ->
                dialogBinding.edtEmail.setText(user.email)
                dialogBinding.edtEmail.isEnabled = false
                
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
            // Kiểm tra role trước khi update
            database.getReference("user").child(currentUser).child("role")
                .get()
                .addOnSuccessListener { snapshot ->
                    val role = snapshot.getValue(String::class.java)
                    if (role == "admin") {
                        val userUpdates = hashMapOf(
                            "name" to name,
                            "phone" to phone,
                            "forceLogout" to false  // Đảm bảo không force logout
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

    private fun setupChangePassword() {
        binding.btnChangePassword.setOnClickListener {
            val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đổi mật khẩu")
                .setView(dialogBinding.root)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val currentPassword = dialogBinding.edtCurrentPassword.text.toString()
                    val newPassword = dialogBinding.edtNewPassword.text.toString()
                    val confirmPassword = dialogBinding.edtConfirmPassword.text.toString()

                    when {
                        currentPassword.isEmpty() -> {
                            dialogBinding.edtCurrentPassword.error = "Vui lòng nhập mật khẩu hiện tại"
                        }
                        newPassword.isEmpty() -> {
                            dialogBinding.edtNewPassword.error = "Vui lòng nhập mật khẩu mới"
                        }
                        newPassword.length < 8 -> {
                            dialogBinding.edtNewPassword.error = "Mật khẩu phải có ít nhất 8 ký tự"
                        }
                        !newPassword.contains(Regex("[A-Z]")) -> {
                            dialogBinding.edtNewPassword.error = "Mật khẩu phải chứa ít nhất 1 chữ viết hoa"
                        }
                        !newPassword.contains(Regex("[0-9]")) -> {
                            dialogBinding.edtNewPassword.error = "Mật khẩu phải chứa ít nhất 1 số"
                        }
                        !newPassword.contains(Regex("[!@#\$%^&*(),.?\":{}|<>]")) -> {
                            dialogBinding.edtNewPassword.error = "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt"
                        }
                        confirmPassword.isEmpty() -> {
                            dialogBinding.edtConfirmPassword.error = "Vui lòng xác nhận mật khẩu"
                        }
                        newPassword != confirmPassword -> {
                            dialogBinding.edtConfirmPassword.error = "Mật khẩu xác nhận không khớp"
                        }
                        else -> {
                            changePassword(currentPassword, newPassword, dialog)
                        }
                    }
                }
            }
            
            // Thêm helper text để hiển thị các yêu cầu về mật khẩu
            dialogBinding.edtNewPassword.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    dialogBinding.tilNewPassword.helperText = """
                        Mật khẩu phải có:
                        • Ít nhất 8 ký tự
                        • Ít nhất 1 chữ viết hoa
                        • Ít nhất 1 số
                        • Ít nhất 1 ký tự đặc biệt
                    """.trimIndent()
                } else {
                    dialogBinding.tilNewPassword.helperText = null
                }
            }
            
            dialog.show()
        }
    }

    private fun changePassword(currentPassword: String, newPassword: String, dialog: AlertDialog) {
        val user = auth.currentUser
        val email = user?.email
        val userId = user?.uid

        if (user != null && email != null && userId != null) {
            // Kiểm tra xem user có phải là admin không
            database.getReference("user").child(userId).child("role")
                .get()
                .addOnSuccessListener { snapshot ->
                    val role = snapshot.getValue(String::class.java)
                    if (role == "admin") {
                        val credential = EmailAuthProvider.getCredential(email, currentPassword)
                        
                        user.reauthenticate(credential).addOnCompleteListener { reauth ->
                            if (reauth.isSuccessful) {
                                user.updatePassword(newPassword).addOnCompleteListener { update ->
                                    if (update.isSuccessful) {
                                        // Cập nhật mật khẩu thành công, đảm bảo không force logout
                                        database.getReference("user").child(userId)
                                            .child("forceLogout")
                                            .setValue(false)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                                dialog.dismiss()
                                            }
                                    } else {
                                        Toast.makeText(context, "Lỗi: ${update.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 