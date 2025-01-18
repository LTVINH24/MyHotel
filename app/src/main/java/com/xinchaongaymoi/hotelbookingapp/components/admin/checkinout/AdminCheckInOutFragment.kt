package com.xinchaongaymoi.hotelbookingapp.components.admin.checkinout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminCheckinoutBinding

class AdminCheckInOutFragment : Fragment() {
    private var _binding: FragmentAdminCheckinoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminCheckinoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 