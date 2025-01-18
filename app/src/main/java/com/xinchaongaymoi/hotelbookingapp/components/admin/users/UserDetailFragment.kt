package com.xinchaongaymoi.hotelbookingapp.components.admin.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentUserDetailBinding

class UserDetailFragment : Fragment() {
    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Lấy dữ liệu từ arguments
        arguments?.let { args ->
            binding.apply {
                tvName.text = args.getString("user_name", "")
                tvEmail.text = args.getString("user_email", "")
                tvPhone.text = args.getString("user_phone", "")
                tvRole.text = if(args.getString("user_role", "").lowercase() == "admin") 
                    "Quản trị viên" else "Người dùng"
                tvStatus.text = if(args.getBoolean("user_is_banned", false)) 
                    "Đã bị khóa" else "Đang hoạt động"
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 