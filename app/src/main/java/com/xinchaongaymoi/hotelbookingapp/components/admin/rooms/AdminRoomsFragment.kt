package com.xinchaongaymoi.hotelbookingapp.components.admin.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinchaongaymoi.hotelbookingapp.adapter.AdminRoomAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminRoomsBinding
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.model.Room

class AdminRoomsFragment : Fragment() {
    private var _binding: FragmentAdminRoomsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminRoomsViewModel by viewModels()
    private lateinit var roomAdapter: AdminRoomAdapter
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
        setupObservers()
        viewModel.loadRooms()
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
                val bundle = Bundle().apply {
                   putString("roomId",room.id)
                }
                findNavController().navigate(R.id.action_adminRoomsFragment_to_editRoomFragment,bundle)

            },
            onDeleteClick = {
                room->showDeteleConfirmationDialog(room)
            }
        )
        binding.roomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = roomAdapter
        }
    }
    private fun showDeteleConfirmationDialog(room: Room){
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirm delete")
            .setMessage("Are you sure you want to delete the room ${room.roomName}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteRoom(room)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun setupObservers() {
        viewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            roomAdapter.updateRooms(rooms)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

} 