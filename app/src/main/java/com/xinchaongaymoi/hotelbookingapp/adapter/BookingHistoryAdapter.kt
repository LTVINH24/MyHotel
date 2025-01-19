package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.xinchaongaymoi.hotelbookingapp.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemBookingHistoryBinding
import com.xinchaongaymoi.hotelbookingapp.model.BookingHistory

class BookingHistoryAdapter(private val bookings: MutableList<BookingHistory> = mutableListOf(),
                            private val onCancelClick: (String) -> Unit,
                            private val onReviewClick: (String) -> Unit)
    : RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>() {
        class  ViewHolder(private val binding: ItemBookingHistoryBinding)
            :RecyclerView.ViewHolder(binding.root){
                fun bind(bookingHistory: BookingHistory, onCancelClick: (String) -> Unit, onReviewClick: (String) -> Unit){
                    binding.apply {
                        tvRoomName.text = bookingHistory.room.roomName
                        tvCheckInDate.text = "CheckIn Date: ${bookingHistory.booking.checkInDate}"
                        tvCheckOutDate.text = "CheckOut Date: ${bookingHistory.booking.checkOutDate}"
                        tvBookingDate.text = "Booking At: ${bookingHistory.booking.createdAt}"
                        tvStatus.text = "${bookingHistory.booking.status}"
                        tvTotalPrice.text = "Total: ${bookingHistory.booking.totalPrice} $"
                        setStatusBackground(bookingHistory.booking.status, tvStatus)
                        Glide.with(itemView.context)
                            .load(bookingHistory.room.mainImage)
                            .into(ivRoom)
                        btnCancelBooking.visibility = if(bookingHistory.booking.status=="pending"
                            &&isCheckInDateAfterToday(bookingHistory.booking.checkInDate)
                            ){
                            View.VISIBLE
                        }
                        else{
                            View.GONE
                        }
                        reviewButton.visibility = if(bookingHistory.booking.status.lowercase()=="completed"){
                            View.VISIBLE
                        }
                        else{
                            View.GONE
                        }

                        btnCancelBooking.setOnClickListener {
                            onCancelClick(bookingHistory.booking.id)
                        }
                        reviewButton.setOnClickListener {
                            onReviewClick(bookingHistory.room.id)
                        }
                    }
                }
            private fun isCheckInDateAfterToday(checkInDate: String): Boolean {
                return try {
                    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val checkInDateTime = formatter.parse(checkInDate)
                    val today = java.util.Calendar.getInstance().time
                    checkInDateTime?.after(today) ?: false
                } catch (e: Exception) {
                    false
                }
            }
            private fun setStatusBackground(status: String, tvStatus: TextView) {
                val textColor = when(status.lowercase()){
                    "pending"-> R.color.status_pending
                    "confirmed"->R.color.status_confirmed
                    "cancelled"->R.color.status_cancelled
                    "completed"->R.color.status_completed
                    else -> {R.color.status_pending}
                }
                tvStatus.apply {
                   setTextColor(ContextCompat.getColor(context,textColor))
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return  ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return bookings.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bookings[position], onCancelClick, onReviewClick)

    }
    fun updateBookings(newBookings: List<BookingHistory>) {
        bookings.clear()
        bookings.addAll(newBookings)
        notifyDataSetChanged()
    }
    private fun setStatusBackground(status:String,tvStatus:TextView){

    }

}