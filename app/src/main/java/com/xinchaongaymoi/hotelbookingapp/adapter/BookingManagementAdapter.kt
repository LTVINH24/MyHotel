package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemBookingManagementBinding
import com.xinchaongaymoi.hotelbookingapp.model.Booking
import com.xinchaongaymoi.hotelbookingapp.model.BookingWithDetails

class BookingManagementAdapter(
    private val onItemClick:(BookingWithDetails) ->Unit
):RecyclerView.Adapter<BookingManagementAdapter.BookingManagementAdapterViewHolder>()
{
    private var bookings = listOf<BookingWithDetails>()
    fun updateBookings(newBookings: List<BookingWithDetails>){
        bookings = newBookings
        notifyDataSetChanged()
    }
    inner class BookingManagementAdapterViewHolder(private val binding:ItemBookingManagementBinding)
        :RecyclerView.ViewHolder(binding.root)
    {
            fun bind(bookingDetail:BookingWithDetails){
                with(binding){
                    tvCustomerName.text="Customer: ${bookingDetail.user.name}"
                    tvRoomName.text ="Room: ${bookingDetail.room.roomName}"
                    tvCheckInDate.text ="CheckIn: ${bookingDetail.booking.checkInDate}"
                    tvCheckOutDate.text = "CheckOut: ${bookingDetail.booking.checkOutDate}"
                    tvTotalPrice.text = "Total: ${bookingDetail.booking.totalPrice}"
                    Glide.with(root.context)
                        .load(bookingDetail.room.mainImage)
                        .into(ivRoomImage)
                    root.setOnClickListener{
                        onItemClick(bookingDetail)
                    }
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookingManagementAdapterViewHolder {
     val binding = ItemBookingManagementBinding.inflate(
         LayoutInflater.from(parent.context),parent,false
     )
        return BookingManagementAdapterViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return bookings.size
    }

    override fun onBindViewHolder(holder: BookingManagementAdapterViewHolder, position: Int) {
        holder.bind(bookings[position])
    }
}