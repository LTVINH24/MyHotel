package com.xinchaongaymoi.hotelbookingapp.adapter

import android.content.Intent
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.xinchaongaymoi.hotelbookingapp.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.xinchaongaymoi.hotelbookingapp.activity.BookingActivity
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemBookingHistoryBinding
import com.xinchaongaymoi.hotelbookingapp.model.BookingHistory

class BookingHistoryAdapter(private val bookings: MutableList<BookingHistory> = mutableListOf(),
                            private val onCancelClick: (String) -> Unit,
                            private val onShowDatePicker: (BookingHistory) -> Unit,
                            private val onDeleteClick: (BookingHistory) -> Unit)
    : RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>() {
        class  ViewHolder(private val binding: ItemBookingHistoryBinding)
            :RecyclerView.ViewHolder(binding.root){
                fun bind(bookingHistory: BookingHistory, onCancelClick: (String) -> Unit, onShowDatePicker: (BookingHistory) -> Unit, onDeleteClick: (BookingHistory) -> Unit){
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
                        btnCancelBooking.visibility = if(bookingHistory.booking.status=="pending"){
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
            holder.bind(bookings[position], onCancelClick, onShowDatePicker, onDeleteClick )

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