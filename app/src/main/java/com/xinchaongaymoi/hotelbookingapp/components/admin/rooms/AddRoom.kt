package com.xinchaongaymoi.hotelbookingapp.components.admin.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinchaongaymoi.hotelbookingapp.adapter.AdminRoomAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminRoomsBinding
import com.xinchaongaymoi.hotelbookingapp.service.RoomService

class AddRoom : Fragment() {
    private var _binding: FragmentAdminRoomsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminRoomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}