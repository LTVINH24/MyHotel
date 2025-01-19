package com.xinchaongaymoi.hotelbookingapp.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.model.Booking

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

class RoomService {
    private val database = FirebaseDatabase.getInstance().reference
    private val roomsRef = database.child("rooms")
    private val bookingsRef = database.child("Booking")
    fun searchRooms(
        guestCount: Int,
        checkIn: String?,
        checkOut: String?,
        maxPrice: Double?,
        callback: (List<Room>) -> Unit
    )
    {
        roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allRooms = mutableListOf<Room>()
                for (data in snapshot.children) {
                    val room = data.getValue(Room::class.java)
                    room?.let {
                        val guestCountMatch = room.maxGuests == guestCount
                        val priceMatch = maxPrice == null || room.pricePerNight <= maxPrice

                        if (guestCountMatch && priceMatch) {
                            allRooms.add(it)
                        }
                    }
                }
                
                if (!checkIn.isNullOrBlank() && !checkOut.isNullOrBlank()) {
                    checkAvailableRooms(allRooms, checkIn, checkOut, callback)
                } else {
                    callback(allRooms)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                callback(emptyList())
            }
        })
    }
    private fun checkAvailableRooms(
        rooms: List<Room>,
        checkIn: String,
        checkOut: String,
        callback: (List<Room>) -> Unit
    ) {
        bookingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val checkInDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(checkIn)
                    val checkOutDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(checkOut)
                    val bookedRoomIds = mutableSetOf<String>()

                    for (bookingSnapshot in snapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let {
                            if(it.status=="pending"||it.status=="confirmed")
                            {
                                val bookingCheckIn = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .parse(it.checkInDate)
                                val bookingCheckOut = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .parse(it.checkOutDate)

                                if (!(checkOutDate?.before(bookingCheckIn) == true ||
                                            checkInDate?.after(bookingCheckOut) == true)) {
                                    bookedRoomIds.add(it.roomId)
                                }
                            }
                        }
                    }

                    val availableRooms = rooms.filter { room ->
                        !bookedRoomIds.contains(room.id)
                    }
                    callback(availableRooms)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("RoomService", "Error checking bookings", error.toException())
                    callback(emptyList())
                }
            })
    }
    fun getRoomsByType(type: String, callback: (List<Room>) -> Unit) {
        roomsRef
            .orderByChild("roomType")
            .equalTo(type)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rooms = mutableListOf<Room>()
                    for (roomSnapshot in snapshot.children) {
                        val room = roomSnapshot.getValue(Room::class.java)
                        room?.let {
                            rooms.add(it)
                        }
                    }
                    callback(rooms)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("RoomService", "Error found room", error.toException())
                    callback(emptyList())
                }
            })
    }
    fun getRoomById(roomId:String,callback: (Room?) -> Unit){
        roomsRef.child(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val room = snapshot.getValue(Room::class.java)?.copy(id = snapshot.key ?: "")
                        callback(room)
                    } catch (e: Exception) {
                        callback(null)
                    }
                } else {

                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }

        })
    }
}