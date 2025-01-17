package com.xinchaongaymoi.hotelbookingapp.components.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.database.*
import com.xinchaongaymoi.hotelbookingapp.adapter.ImageSliderAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentRoomDetailBinding
import com.xinchaongaymoi.hotelbookingapp.activity.BookingActivity

class RoomDetailFragment : Fragment() {
    private var _binding: FragmentRoomDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private val TAG = "RoomDetailFragment"
    private val BOOKING_REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        imageSliderAdapter = ImageSliderAdapter(emptyList())
        binding.viewPagerImages.adapter = imageSliderAdapter

        // Thêm click listener cho nút đặt phòng
        binding.btnBookNow.setOnClickListener {
            arguments?.getString("ROOM_ID")?.let { roomId ->
                navigateToBooking(roomId)
            }
        }

        arguments?.getString("ROOM_ID")?.let { roomId ->
            Log.d(TAG, "Fetching details for room: $roomId")
            fetchRoomDetails(roomId)
        } ?: run {
            Log.e(TAG, "No room ID provided")
            Toast.makeText(context, "Không tìm thấy ID phòng!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToBooking(roomId: String) {
        val intent = Intent(requireContext(), BookingActivity::class.java).apply {
            putExtra("ROOM_ID", roomId)
            // Lấy ngày check-in và check-out từ SearchViewModel
            val searchViewModel: SearchViewModel by activityViewModels()
            putExtra("CHECK_IN", searchViewModel.checkInDate.value)
            putExtra("CHECK_OUT", searchViewModel.checkOutDate.value)
        }
        startActivityForResult(intent, BOOKING_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BOOKING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Quay lại màn hình trước đó sau khi đặt phòng thành công
            parentFragmentManager.popBackStack()
        }
    }

    private fun fetchRoomDetails(roomId: String) {
        val database = FirebaseDatabase.getInstance()
        val roomRef = database.getReference("rooms").child(roomId)

        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Room data received: ${snapshot.value}")
                if (snapshot.exists()) {
                    try {
                        val imageList = snapshot.child("images").children.mapNotNull { it.value?.toString() }
                        Log.d(TAG, "Images found: ${imageList.size}")
                        imageSliderAdapter.updateImages(imageList)

                        binding.apply {
                            tvRoomName.text = snapshot.child("roomName").value?.toString() ?: "N/A"
                            tvArea.text = "Diện tích: ${snapshot.child("area").value?.toString() ?: "N/A"} m²"
                            tvBedType.text = "Loại giường: ${snapshot.child("bedType").value?.toString() ?: "N/A"}"
                            tvLocation.text = "Vị trí: ${snapshot.child("location").value?.toString() ?: "N/A"}"
                            tvMaxGuests.text = "Số khách tối đa: ${snapshot.child("maxGuests").value?.toString() ?: "N/A"}"
                            tvPricePerHour.text = "Giá theo giờ: ${snapshot.child("pricePerHour").value?.toString() ?: "N/A"}$"
                            tvPricePerNight.text = "Giá theo đêm: ${snapshot.child("pricePerNight").value?.toString() ?: "N/A"}$"
                            tvUtilities.text = "Tiện ích: ${snapshot.child("utilities").value?.toString() ?: "N/A"}"
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating UI: ${e.message}", e)
                        Toast.makeText(context, "Lỗi khi hiển thị thông tin phòng", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "Room not found")
                    binding.tvRoomName.text = "Không tìm thấy thông tin phòng!"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
                binding.tvRoomName.text = "Lỗi khi tải dữ liệu: ${error.message}"
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(roomId: String) = RoomDetailFragment().apply {
            arguments = Bundle().apply {
                putString("ROOM_ID", roomId)
            }
        }
    }
} 