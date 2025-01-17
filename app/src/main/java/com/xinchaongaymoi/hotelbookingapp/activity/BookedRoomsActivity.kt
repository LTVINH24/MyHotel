package com.xinchaongaymoi.hotelbookingapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.adapter.BookedRoomsAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.ActivityBookedRoomsBinding
import com.xinchaongaymoi.hotelbookingapp.model.Booking
import com.xinchaongaymoi.hotelbookingapp.model.Room
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookedRoomsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookedRoomsBinding
    private lateinit var database: DatabaseReference
    private lateinit var roomDatabase: DatabaseReference
    private lateinit var adapter: BookedRoomsAdapter
    private val bookedRoomsList = mutableListOf<Booking>()
    private val roomList = mutableListOf<Room>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookedRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("Booking")
        roomDatabase = FirebaseDatabase.getInstance().getReference("rooms")


        // Setup RecyclerView
        adapter = BookedRoomsAdapter(bookedRoomsList,roomList) { booking ->
            if (booking.status == "completed") {
                cancelBooking(booking)
            } else {
                bookAgain(booking)
            }
        }
        binding.recyclerViewBookedRooms.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewBookedRooms.adapter = adapter

        // Load bookings from Firebase
        loadBookings()
        loadRooms()
    }

    private fun loadRooms() {
        roomDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomList.clear()
                for (data in snapshot.children) {
                    val room = data.getValue(Room::class.java)
                    room?.let { roomList.add(it) }
                }
                // You can now use roomList to check availability when booking again or during booking process
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BookedRoomsActivity", "Failed to load rooms: ${error.message}")
                Toast.makeText(this@BookedRoomsActivity, "Failed to load rooms", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadBookings() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookedRoomsList.clear()
                for (data in snapshot.children) {
                    val booking = data.getValue(Booking::class.java)
                    booking?.let { bookedRoomsList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BookedRoomsActivity", "Failed to load bookings: ${error.message}")
                Toast.makeText(this@BookedRoomsActivity, "Failed to load bookings", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cancelBooking(booking: Booking) {
        // Kiểm tra ngày hủy có phải là trước ngày đặt phòng 1 ngày hay không
        val currentDate = Calendar.getInstance().time
        val bookingDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(booking.checkInDate)

        val calendar = Calendar.getInstance()
        calendar.time = bookingDate
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Cộng thêm 1 ngày vào ngày đặt phòng

        if (currentDate.before(calendar.time)) {
            // Nếu ngày hiện tại nhỏ hơn ngày đặt phòng 1 ngày
            Toast.makeText(this, "Cannot cancel this booking yet.", Toast.LENGTH_SHORT).show()
        } else {
            // Cập nhật trạng thái của booking
            val updatedBooking = booking.copy(status = "uncompleted")
            database.child(booking.id).setValue(updatedBooking)
                .addOnSuccessListener {
                    Toast.makeText(this, "Booking canceled successfully", Toast.LENGTH_SHORT).show()

                    // Cập nhật nút thành "Book Again"
                    binding.recyclerViewBookedRooms.findViewHolderForAdapterPosition(bookedRoomsList.indexOf(booking))
                        ?.let { viewHolder ->
                            val btn = viewHolder.itemView.findViewById<Button>(R.id.btnCancelBooking)
                            btn.text = "Book Again"
                            btn.setOnClickListener {
                                // Logic để đặt lại phòng
                                bookAgain(updatedBooking)
                            }
                            // Đổi màu nút thành màu xanh
                            btn.setBackgroundColor(ContextCompat.getColor(this@BookedRoomsActivity, R.color.green))
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to cancel booking: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun bookAgain(booking: Booking) {
        // Logic để đặt lại phòng (tạo booking mới)
        // Đây là nơi bạn có thể thêm logic tạo lại booking nếu cần
        Toast.makeText(this, "Booking again logic goes here", Toast.LENGTH_SHORT).show()
    }
}