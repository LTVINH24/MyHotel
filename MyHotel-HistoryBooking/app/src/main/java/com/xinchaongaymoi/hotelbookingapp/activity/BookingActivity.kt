package com.xinchaongaymoi.hotelbookingapp.activity
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityBookingBinding
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.service.BookingService
import com.xinchaongaymoi.hotelbookingapp.service.RoomService
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log
import com.bumptech.glide.Glide
import android.content.Intent
import android.os.Build
import android.view.View

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding
    private lateinit var bookingService: BookingService
    private lateinit var roomService: RoomService
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bookingService  = BookingService()
        roomService = RoomService()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val roomId = intent.getStringExtra("Room_ID") ?: return

        val checkIn = intent.getStringExtra("CHECK_IN") ?: return
        val checkOut = intent.getStringExtra("CHECK_OUT") ?: return

        roomService.getRoomById(roomId){
            room->
            if(room!=null){
                setRoomInfo(room)
                setBookingDetails(room,checkIn,checkOut)
            }
        }
        binding.btnConfirmBooking.setOnClickListener {
            handleBooking(roomId, checkIn, checkOut)
        }
    }
    private fun setRoomInfo(room: Room){
        binding.apply {
            tvRoomName.text = room.roomName
            tvRoomType.text = "Room type: ${room.roomType}"
            tvLocation.text = room.location
            ratingBar.rating = room.rating.toFloat()
            tvRating.text = String.format("%.1f", room.rating)

            tvGuestCount.text = "${room.maxGuests} guests"
            tvBedCount.text = "${room.totalBed} beds"

            Glide.with(this@BookingActivity)
                .load(room.mainImage)
                .into(binding.ivRoomImage)
        }
    }
    private fun handleBooking(roomId: String, checkIn: String, checkOut: String) {
        val userId = sharedPreferences.getString("id", null)
        if (userId == null) {
            Toast.makeText(this, "Please login before booking", Toast.LENGTH_SHORT).show()
            return
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm booking")
            .setMessage("Are you sure you want to book this room?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Confirm") { _, _ ->
                // Hiển thị loading
                binding.progressBar.visibility = View.VISIBLE
                binding.btnConfirmBooking.isEnabled = false
                
                // Lấy tổng tiền từ TextView
                val totalPriceText = binding.tvTotalPrice.text.toString()
                val totalPrice = totalPriceText.replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: 0.0
                
                bookingService.createBooking(
                    roomId = roomId,
                    userId = userId,
                    checkInDate = checkIn,
                    checkOutDate = checkOut,
                    totalPrice = totalPrice
                ) { success, bookingId ->
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.btnConfirmBooking.isEnabled = true
                        
                        if (success) {
                            Toast.makeText(this, "Booking successfully!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error: ${bookingId ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .show()
    }
    private fun setBookingDetails(room: Room,checkIn:String,checkOut:String){
        binding.apply {
            tvCheckIn.text = "Check In: ${checkIn}"
            tvCheckOut.text = "Check Out: ${checkOut}"
            val nights = calculateNights(checkIn, checkOut)
            tvNights.text = "Nights: $nights "
            val pricePerNight = if (room.sale > 0) {
              room.sale
            } else {
                room.pricePerNight
            }

            tvPricePerNight.text = "${String.format("%.2f", pricePerNight)}$"
            tvNumberOfNights.text = "$nights night"
            val totalPrice = pricePerNight * nights
            tvTotalPrice.text = "${String.format("%.2f", totalPrice)}$"
        }
    }
    private fun calculateNights(checkIn: String, checkOut: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val checkInDate = dateFormat.parse(checkIn)
        val checkOutDate = dateFormat.parse(checkOut)
        return ((checkOutDate?.time ?: 0) - (checkInDate?.time ?: 0)) / (1000 * 60 * 60 * 24).toInt()
    }
}