package com.xinchaongaymoi.hotelbookingapp.data.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.data.model.Room
import com.xinchaongaymoi.hotelbookingapp.R

class HomeRoomAdapter(private val rooms:MutableList<Room>):RecyclerView.Adapter<HomeRoomAdapter.ViewHolder>() {

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val saleTV :TextView =itemView.findViewById(R.id.saleTV)
        val priceTV:TextView =itemView.findViewById(R.id.priceTV)
        val nameRoomTV:TextView=itemView.findViewById(R.id.nameRoomTV)
        val ratingTV:TextView =itemView.findViewById(R.id.ratingTV)
        val imageRoom:ImageView =itemView.findViewById(R.id.imageRoom)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.room_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room =rooms[position]
        holder.ratingTV.text= "Rating: ${room.rating}"
        holder.nameRoomTV.text=room.room_name
        holder.priceTV.text = "${room.price_per_night}$/night"
        holder.saleTV.text= "${room.sale}% OFF"
        Glide.with(holder.itemView.context)
            .load(room.image)
            .into(holder.imageRoom)
    }
}