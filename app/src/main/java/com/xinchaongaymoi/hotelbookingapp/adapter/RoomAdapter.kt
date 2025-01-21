package com.xinchaongaymoi.hotelbookingapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.activity.BookingActivity
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.databinding.RoomItemSearchBinding
import android.util.Log

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {
    private var rooms = listOf<Room>()
    private var onItemClickListener: ((Room) -> Unit)? = null
    private var onBookClickListener: ((Room) -> Unit)? = null
    private var checkInDate: String? = null
    private var checkOutDate: String? = null

    inner class RoomViewHolder(private val binding: RoomItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.apply {
                // Load hình ảnh bằng Glide
                Glide.with(itemView.context)
                    .load(room.mainImage)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(roomImage)

                roomName.text = room.roomName
                tvBedCount.text = "Bed: ${room.totalBed}"
                roomArea.text = "${room.area} m²"
                roomPrice.text = "${room.pricePerNight}$"
                ratingBar.rating = room.rating.toFloat()
                ratingValue.text ="${String.format("%.1f", room.rating)}"

                // Xử lý click cho toàn bộ card
                cardViewRoom.setOnClickListener {
                    onItemClickListener?.invoke(room)
                }

                // Xử lý click cho nút đặt ngay
                btnBookNow.setOnClickListener {
                    onBookClickListener?.invoke(room)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = RoomItemSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount(): Int = rooms.size

    fun setOnItemClickListener(listener: (Room) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnBookClickListener(listener: (Room) -> Unit) {
        onBookClickListener = listener
    }

    fun updateRooms(newRooms: List<Room>) {
        rooms = newRooms
        notifyDataSetChanged()
    }

    fun setDates(checkIn: String, checkOut: String) {
        checkInDate = checkIn
        checkOutDate = checkOut
        notifyDataSetChanged()
    }
}