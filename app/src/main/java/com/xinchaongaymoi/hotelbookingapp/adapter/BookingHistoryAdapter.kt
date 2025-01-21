package com.xinchaongaymoi.hotelbookingapp.adapter

import android.graphics.Canvas
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
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar

class BookingHistoryAdapter(private val bookings: MutableList<BookingHistory> = mutableListOf(),
                            private val onCancelClick: (String) -> Unit,
                            private val onReviewClick: (String) -> Unit,
                            private val onDeleteClick: (BookingHistory) -> Unit,
                            private val onShowDatePicker: (BookingHistory) -> Unit)
    : RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>() {
        class  ViewHolder(private val binding: ItemBookingHistoryBinding)
            :RecyclerView.ViewHolder(binding.root){
                fun bind(bookingHistory: BookingHistory, onCancelClick: (String) -> Unit, onReviewClick: (String) -> Unit , onDeleteClick: (BookingHistory) -> Unit , onShowDatePicker: (BookingHistory) -> Unit){
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

                        // Nếu trạng thái là cancelled hoặc uncompleted, gọi showDatePickerFragment
                        binding.root.setOnClickListener {
                            if (bookingHistory.booking.status == "cancelled" || bookingHistory.booking.status == "uncompleted") {
                                onShowDatePicker(bookingHistory)  // Gọi callback này khi điều kiện thỏa mãn
                            }
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
                    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    val checkInDateTime = formatter.parse(checkInDate)
                    
                    val today = java.util.Calendar.getInstance().apply {
                        time = java.util.Date()
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }.time
                    
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
        holder.bind(bookings[position], onCancelClick, onReviewClick, onDeleteClick, onShowDatePicker)

    }
    fun updateBookings(newBookings: List<BookingHistory>) {
        bookings.clear()
        bookings.addAll(newBookings)
        notifyDataSetChanged()
    }
    private fun setStatusBackground(status:String,tvStatus:TextView){

    }

    fun getItemTouchHelper(): ItemTouchHelper.Callback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val booking = bookings[position]

                if (booking.booking.status == "cancelled" || booking.booking.status == "uncompleted") {
                    onDeleteClick(booking)
                    bookings.removeAt(position)
                    notifyItemRemoved(position)
                    Snackbar.make(viewHolder.itemView, "Booking deleted", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(viewHolder.itemView, "Cannot delete this booking", Snackbar.LENGTH_SHORT).show()
                    notifyItemChanged(position)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                if (dX < 0) {
                    val itemView = viewHolder.itemView
                    val deleteIcon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete)
                    deleteIcon?.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    deleteIcon?.draw(c)
                }
            }
        }
    }

}