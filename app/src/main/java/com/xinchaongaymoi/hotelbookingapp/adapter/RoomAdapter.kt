package com.xinchaongaymoi.hotelbookingapp.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.databinding.RoomItemSearchBinding

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {
    private var roomList = mutableListOf<Room>()
    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    inner class RoomViewHolder(private val binding: RoomItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnBookNow.setOnClickListener {

            }
        }

        fun bind(room: Room) {
            binding.apply {
                roomName.text = room.roomName
                roomLocation.text = room.location
                roomPrice.text = "Price: ${room.pricePerNight} $"
//                roomType.text = room.roomType
                roomArea.text = "Diện tích: ${room.area} m2"
                ratingBar.rating=room.rating.toFloat()
                Glide.with(roomImage.context)
                    .load(room.mainImage)
                    .into(roomImage)

                itemView.setOnClickListener {
                    onItemClickListener?.invoke(room.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = RoomItemSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room =roomList[position]
        holder.bind(room)
    }
    fun updateRooms(newRooms:List<Room>){
        roomList.clear()
        roomList.addAll(newRooms)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = roomList.size
}