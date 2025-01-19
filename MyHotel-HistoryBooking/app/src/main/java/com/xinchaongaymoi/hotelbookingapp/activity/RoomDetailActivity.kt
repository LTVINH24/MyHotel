package com.xinchaongaymoi.hotelbookingapp.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.xinchaongaymoi.hotelbookingapp.adapter.ImageSliderAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityRoomDetailBinding

class RoomDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận roomId từ Intent
        val roomId = intent.getStringExtra("ROOM_ID")
        if (!roomId.isNullOrEmpty()) {
            // Hiển thị trạng thái đang tải dữ liệu
            binding.tvRoomName.text = "Fetching details for Room ID: $roomId"
            fetchRoomDetails(roomId)
        } else {
            // Thông báo lỗi nếu roomId không tồn tại
            Toast.makeText(this, "Room ID not found!", Toast.LENGTH_SHORT).show()
            finish() // Quay lại activity trước đó
        }
    }

    private fun fetchRoomDetails(roomId: String) {
        val database = FirebaseDatabase.getInstance()
        val roomRef = database.getReference("room").child(roomId)

        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Lấy danh sách URL hình ảnh từ trường "images"
                    val imageList = snapshot.child("images").children.mapNotNull { it.value?.toString() }

                    // Gán adapter cho ViewPager2
                    val adapter = ImageSliderAdapter(imageList)
                    binding.viewPagerImages.adapter = adapter

                    // Lấy thông tin chi tiết phòng
                    val roomName = snapshot.child("room_name").value?.toString() ?: "N/A"
                    val area = snapshot.child("area").value?.toString() ?: "N/A"
                    val bedType = snapshot.child("bed_type").value?.toString() ?: "N/A"
                    val location = snapshot.child("location").value?.toString() ?: "N/A"
                    val maxGuests = snapshot.child("max-guests").value?.toString() ?: "N/A"
                    val pricePerHour = snapshot.child("price_per_hour").value?.toString() ?: "N/A"
                    val pricePerNight = snapshot.child("price_per_night").value?.toString() ?: "N/A"
                    val utilities = snapshot.child("utilities").value?.toString() ?: "N/A"

                    // Cập nhật thông tin phòng vào giao diện
                    binding.tvRoomName.text = roomName
                    binding.tvArea.text = "Area: $area m²"
                    binding.tvBedType.text = "Bed Type: $bedType"
                    binding.tvLocation.text = "Location: $location"
                    binding.tvMaxGuests.text = "Max Guests: $maxGuests"
                    binding.tvPricePerHour.text = "Price per Hour: $pricePerHour USD"
                    binding.tvPricePerNight.text = "Price per Night: $pricePerNight USD"
                    binding.tvUtilities.text = "Utilities: $utilities"
                } else {
                    binding.tvRoomName.text = "Room not found!"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.tvRoomName.text = "Failed to fetch room details: ${error.message}"
            }
        })
    }
}
