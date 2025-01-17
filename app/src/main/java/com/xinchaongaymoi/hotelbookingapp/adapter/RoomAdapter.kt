package com.xinchaongaymoi.hotelbookingapp.adapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.activity.BookingActivity
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.databinding.RoomItemSearchBinding
import android.util.Log

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {
    private var roomList = mutableListOf<Room>()
    private var BOOKING_REQUEST_CODE = 100
    private var checkInDate: String? = null
    private var checkOutDate: String? = null
    var onItemClick: ((Room) -> Unit)? = null
    fun setOnItemClickListener(listener: (Room) -> Unit) {
        onItemClick = listener
    }
    inner class RoomViewHolder(private val binding: RoomItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.apply {
                Log.i("Tesssss",room.toString())
                roomName.text = room.roomName
                tvBedCount.text = "Total bed: ${room.totalBed}"
                roomPrice.text = "Price: ${room.pricePerNight} $"
                roomArea.text = "Area: ${room.area} m2"
                ratingBar.rating=room.rating.toFloat()
                Glide.with(roomImage.context)
                    .load(room.mainImage)
                    .into(roomImage)


            }
            binding.btnBookNow.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, BookingActivity::class.java).apply {
                    putExtra("Room_ID", room.id)
                    putExtra("CHECK_IN", checkInDate)
                    putExtra("CHECK_OUT", checkOutDate)
                }
                (context as androidx.fragment.app.FragmentActivity).startActivityForResult(intent, BOOKING_REQUEST_CODE)
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
    fun setDates(checkIn: String?, checkOut: String?) {
        checkInDate = checkIn
        checkOutDate = checkOut
    }
    override fun getItemCount(): Int = roomList.size
}