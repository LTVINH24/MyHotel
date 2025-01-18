package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemAdminRoomBinding
import com.xinchaongaymoi.hotelbookingapp.model.Room

class AdminRoomAdapter(
    private var rooms:MutableList<Room>,
    private val onEditClick: (Room) -> Unit,
    private val onDeleteClick: (Room) -> Unit
) :RecyclerView.Adapter<AdminRoomAdapter.RoomViewHolder>()
{
    inner class RoomViewHolder(private val binding:ItemAdminRoomBinding)
        :RecyclerView.ViewHolder(binding.root)
    {
            fun bind(room:Room){
                binding.apply {
                    tvRoomName.text= room.roomName
                    tvRoomType.text="Type: ${room.roomType}"
                    tvTotalBed.text="Total bed: ${room.totalBed}"
                    tvMaxGuests.text="Guets: ${room.maxGuests}"
                    if(room.sale>0){
                        tvPrice.text = "Price: ${room.sale}$"
                    }
                    else{
                        tvPrice.text = "Price: ${room.pricePerNight}$"
                    }
                    Glide.with(imgRoom.context)
                        .load(room.mainImage)
                        .centerCrop()
                        .into(imgRoom)
                    btnEdit.setOnClickListener{onEditClick(room)}
                    btnDelete.setOnClickListener{onDeleteClick(room)}

                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemAdminRoomBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int {
      return rooms.size
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
     holder.bind(rooms[position])
    }
    fun updateRooms(newRooms:List<Room>){
        rooms.clear()
        rooms.addAll(newRooms)
        notifyDataSetChanged()
    }
}