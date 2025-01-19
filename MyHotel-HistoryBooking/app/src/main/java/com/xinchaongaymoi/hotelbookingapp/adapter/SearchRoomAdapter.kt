package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.R
import android.view.View
import android.view.ViewGroup
class SearchRoomAdapter(private val roomType: String) : RecyclerView.Adapter<SearchRoomAdapter.ViewHolder>() {

    companion object {
        const val TYPE_LUXURY = "Luxury"
        const val TYPE_ROYAL = "Royal"
    }

    private var roomList = mutableListOf<Room>()
    private var onItemClickListener: ((Room) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgRoom: ImageView = itemView.findViewById(R.id.imgRoom)
        private val tvRoomName: TextView = itemView.findViewById(R.id.tvRoomName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvTag:TextView=itemView.findViewById(R.id.tvTag)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)

        fun bind(room: Room) {
            Glide.with(itemView.context)
                .load(room.mainImage)
                .centerCrop()
                .into(imgRoom)

            tvRoomName.text = room.roomName
            tvPrice.text = "${room.pricePerNight}$"
            ratingBar.rating = room.rating.toFloat()

            when (roomType) {
                TYPE_LUXURY -> {
                    tvTag.text="LUXURY"
                    tvTag.setBackgroundColor(Color.parseColor("#FFD700"))
                    tvPrice.setTextColor(Color.parseColor("#FFD700"))  // Màu vàng cho Luxury
                    ratingBar.progressTintList = ColorStateList.valueOf(
                        Color.parseColor("#FFD700")
                    )
                }
                TYPE_ROYAL -> {
                    tvTag.text="ROYAL"
                    tvTag.setBackgroundColor(Color.parseColor("#8B0000"))
                    tvPrice.setTextColor(Color.parseColor("#8B0000"))  // Màu đỏ đậm cho Royal
                    ratingBar.progressTintList = ColorStateList.valueOf(
                        Color.parseColor("#8B0000")
                    )
                }
            }

            itemView.setOnClickListener {
                onItemClickListener?.invoke(room)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutRes = when (roomType) {
            TYPE_LUXURY -> R.layout.room_item
            TYPE_ROYAL -> R.layout.room_item
            else -> R.layout.room_item // default layout
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(roomList[position])
    }

    override fun getItemCount(): Int = roomList.size

    fun updateRooms(rooms: List<Room>) {
        roomList.clear()
        roomList.addAll(rooms.filter { it.roomType == roomType })
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Room) -> Unit) {
        onItemClickListener = listener
    }
}