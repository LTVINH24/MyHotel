package com.xinchaongaymoi.hotelbookingapp.components.search

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.xinchaongaymoi.hotelbookingapp.adapter.ImageSliderAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentRoomDetailBinding
import com.xinchaongaymoi.hotelbookingapp.activity.BookingActivity
import com.xinchaongaymoi.hotelbookingapp.R
import androidx.core.content.ContextCompat

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
        
        // Kiểm tra nguồn điều hướng
        val isFromSearch = arguments?.getBoolean("FROM_SEARCH", false) ?: false
        
        // Ẩn nút đặt phòng nếu đến từ SearchFragment
        if (isFromSearch) {
            binding.btnBookNow.visibility = View.GONE
        } else {
            binding.btnBookNow.visibility = View.VISIBLE
        }
        
        // Thêm nút back
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Setup ViewPager2 với indicators và navigation arrows
        setupImageSlider()

        // Thêm click listener cho nút đặt phòng
        binding.btnBookNow.setOnClickListener {
            arguments?.getString("ROOM_ID")?.let { roomId ->
                navigateToBooking(roomId)
            }
        }
        binding.btnViewReviews.setOnClickListener{
            arguments?.getString("ROOM_ID")?.let {
                roomId->
                val bundle = Bundle().apply {
                    putString("roomId",roomId)
                }
                findNavController().navigate(
                    R.id.action_roomDetailFragment_to_roomReviewsFragment,bundle
                )

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

        // Thêm vào sau các code khởi tạo
        setupIcons()
    }

    private fun setupIcons() {
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.primary)
        
        binding.apply {
            // Đặt màu cho các icon
            tvRoomType.compoundDrawables.firstOrNull()?.setTint(colorPrimary)
            tvArea.compoundDrawables.firstOrNull()?.setTint(colorPrimary)
            tvBedType.compoundDrawables.firstOrNull()?.setTint(colorPrimary)
            tvTotalBed.compoundDrawables.firstOrNull()?.setTint(colorPrimary)
            tvMaxGuests.compoundDrawables.firstOrNull()?.setTint(colorPrimary)
            tvPricePerNight.compoundDrawables.firstOrNull()?.setTint(colorPrimary)
            tvUtilities.compoundDrawables.firstOrNull()?.setTint(colorPrimary)
            
            // Set màu vàng cho RatingBar
            ratingBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FFD700"))
            ratingBar.secondaryProgressTintList = ColorStateList.valueOf(Color.parseColor("#FFD700"))
        }
    }

    private fun setupImageSlider() {
        imageSliderAdapter = ImageSliderAdapter(emptyList())
        binding.viewPagerImages.adapter = imageSliderAdapter
        
        // Setup indicators
        binding.dotsIndicator.attachTo(binding.viewPagerImages)
        
        // Setup navigation arrows
        binding.apply {
            // Ẩn arrows ban đầu
            prevButton.alpha = 0f
            nextButton.alpha = 0f
            
            // Hiện arrows khi chạm vào ViewPager
            viewPagerImages.setOnTouchListener { _, _ ->
                prevButton.animate().alpha(0.7f).setDuration(200).start()
                nextButton.animate().alpha(0.7f).setDuration(200).start()
                false
            }
            
            // Click listeners cho arrows
            prevButton.setOnClickListener {
                val currentItem = viewPagerImages.currentItem
                if (currentItem > 0) {
                    viewPagerImages.currentItem = currentItem - 1
                }
            }
            
            nextButton.setOnClickListener {
                val currentItem = viewPagerImages.currentItem
                if (currentItem < (imageSliderAdapter.itemCount - 1)) {
                    viewPagerImages.currentItem = currentItem + 1
                }
            }
            
            // Auto hide arrows sau 2 giây không chạm
            viewPagerImages.setOnClickListener {
                prevButton.animate().alpha(0.7f).setDuration(200).start()
                nextButton.animate().alpha(0.7f).setDuration(200).start()
                
                view?.postDelayed({
                    prevButton.animate().alpha(0f).setDuration(200).start()
                    nextButton.animate().alpha(0f).setDuration(200).start()
                }, 2000)
            }
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
                if (snapshot.exists()) {
                    try {
                        // Load ảnh vào ViewPager
                        val imageList = snapshot.child("images").children.mapNotNull { it.value?.toString() }
                        imageSliderAdapter.updateImages(imageList)

                        binding.apply {
                            // Tên phòng
                            tvRoomName.text = snapshot.child("roomName").value?.toString() ?: "N/A"
                            
                            // Loại phòng
                            tvRoomType.text = "Loại phòng: ${snapshot.child("roomType").value?.toString() ?: "N/A"}"
                            
                            // Diện tích
                            tvArea.text = "Diện tích: ${snapshot.child("area").value?.toString() ?: "N/A"} m²"
                            
                            // Thông tin giường
                            tvBedType.text = "Loại giường: ${snapshot.child("bedType").value?.toString() ?: "N/A"}"
                            tvTotalBed.text = "Số giường: ${snapshot.child("totalBed").value?.toString() ?: "N/A"}"
                            
                            // Số khách tối đa
                            tvMaxGuests.text = "Số khách tối đa: ${snapshot.child("maxGuests").value?.toString() ?: "N/A"} người"
                            
                            // Giá phòng
                            val pricePerNight = snapshot.child("pricePerNight").value?.toString()?.toDoubleOrNull() ?: 0.0
                            tvPricePerNight.text = "Giá theo đêm: ${String.format("%,.0f", pricePerNight)}$"
                            
                            // Tiện ích
                            tvUtilities.text = "Tiện ích: ${snapshot.child("utilities").value?.toString() ?: "N/A"}"
                            
                            // Đánh giá với RatingBar và số
                            val rating = snapshot.child("rating").value?.toString()?.toDoubleOrNull() ?: 0.0
                            ratingBar.rating = rating.toFloat()
                            tvRating.text = "(${String.format("%.1f", rating)})"
                            tvRatingLabel.text = "Đánh giá:"
                            
                            // Ảnh chính
                            val mainImage = snapshot.child("mainImage").value?.toString()
                            if (!mainImage.isNullOrEmpty()) {
                                imageList.toMutableList().apply {
                                    if (!contains(mainImage)) add(0, mainImage)
                                }.let { imageSliderAdapter.updateImages(it) }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating UI: ${e.message}", e)
                        Toast.makeText(context, "Lỗi khi hiển thị thông tin phòng", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.tvRoomName.text = "Không tìm thấy thông tin phòng!"
                }
            }

            override fun onCancelled(error: DatabaseError) {
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