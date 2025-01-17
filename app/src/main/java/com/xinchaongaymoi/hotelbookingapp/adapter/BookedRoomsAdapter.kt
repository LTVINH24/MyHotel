package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.databinding.RoomBookedItemBinding
import com.xinchaongaymoi.hotelbookingapp.model.Booking
import com.xinchaongaymoi.hotelbookingapp.model.Room

class BookedRoomsAdapter(
    private val bookings: List<Booking>,
    private val rooms: List<Room>, // Đảm bảo bạn có dữ liệu này
    private val onCancelClick: (Booking) -> Unit
): RecyclerView.Adapter<BookedRoomsAdapter.BookedRoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedRoomViewHolder {
        val binding = RoomBookedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookedRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookedRoomViewHolder, position: Int) {
        val booking = bookings[position]
        holder.bind(booking)
    }

    override fun getItemCount(): Int = bookings.size

    inner class BookedRoomViewHolder(private val binding: RoomBookedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            // Tìm phòng trong danh sách rooms từ roomId
            val room = rooms.find { it.id == booking.roomId }
            val roomImage = room?.mainImage // Lấy ảnh chính của phòng từ room

            // Sử dụng Glide để tải ảnh vào ImageView
            if (roomImage != null) {
                Glide.with(binding.imgRoom.context)
                    .load(roomImage) // URL của ảnh
                    .into(binding.imgRoom)
            }

            binding.tvRoomName.text = room?.roomName ?: "Unknown Room"
            binding.tvTag.text = booking.status.uppercase()
            binding.tvCreatedAt.text = "Created At: ${booking.createdAt}"
            binding.tvPrice.text = "Total: $${booking.totalPrice}"

            // Hiển thị nút dựa trên trạng thái
            if (booking.status == "uncompleted") {
                binding.btnCancelBooking.text = "Book Again"
                binding.btnCancelBooking.setBackgroundColor(
                    ContextCompat.getColor(binding.btnCancelBooking.context, R.color.green)
                )
                binding.btnCancelBooking.setTextColor(
                    ContextCompat.getColor(binding.btnCancelBooking.context, R.color.white)
                )
                binding.btnCancelBooking.setOnClickListener {
                    onCancelClick(booking)
                }
            } else {
                binding.btnCancelBooking.text = "Cancel Booking"
                binding.btnCancelBooking.setBackgroundColor(
                    ContextCompat.getColor(binding.btnCancelBooking.context, R.color.red)
                )
                binding.btnCancelBooking.setTextColor(
                    ContextCompat.getColor(binding.btnCancelBooking.context, R.color.white)
                )
                binding.btnCancelBooking.setOnClickListener {
                    onCancelClick(booking)
                }
            }
        }
    }
}