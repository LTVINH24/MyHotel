package com.xinchaongaymoi.hotelbookingapp.components.admin.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinchaongaymoi.hotelbookingapp.adapter.AdminRoomAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminRoomsBinding
import com.xinchaongaymoi.hotelbookingapp.service.RoomService
import com.xinchaongaymoi.hotelbookingapp.R
class AdminRoomsFragment : Fragment() {
    private var _binding: FragmentAdminRoomsBinding? = null
    private val binding get() = _binding!!

    private lateinit var roomAdapter: AdminRoomAdapter
    private val roomService = RoomService()
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
        setupRecyclerView()
        loadRooms()
        binding.fabAddRoom.setOnClickListener{
            findNavController().navigate(R.id.action_adminRoomsFragment_to_addRoomFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupRecyclerView(){
        roomAdapter = AdminRoomAdapter(
            mutableListOf(),
            onEditClick = {
                room->
            },
            onDeleteClick = {
                room->
            }
        )
        binding.roomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = roomAdapter
        }
    }
    private fun loadRooms(){
        binding.loadingProgressBar.visibility =View.VISIBLE
        roomService.getAllRooms { rooms->
            binding.loadingProgressBar.visibility =View.GONE
           roomAdapter.updateRooms(rooms)
        }
    }
} 