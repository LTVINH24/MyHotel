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
                        if(it.dele!="true"){
                            val guestCountMatch = room.maxGuests == guestCount
                            val priceMatch = maxPrice == null || room.pricePerNight <= maxPrice

                            if (guestCountMatch && priceMatch) {
                                allRooms.add(it)
                            }
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
                Log.e("RoomService", "Error fetching rooms", error.toException())
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
                            if(it.dele!="true"){
                                rooms.add(it)

                            }
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
    fun getAllRooms(callback: (MutableList<Room>) -> Unit){
        roomsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               val rooms = mutableListOf<Room>()
                for(data in snapshot.children)
                {

                    val isDeletedValue = data.child("isDeleted").getValue(String::class.java)
                    Log.d("RoomService", "isDeleted from snapshot: $isDeletedValue")
                    val room = data.getValue(Room::class.java)
                    Log.i("Rommmmmmmm",room.toString())
                    room?.let {
                        if(it.dele!="true"){
                            rooms.add(it)
                        }
                    }
                }
                callback(rooms)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList<Room>().toMutableList())
            }

        })
    }
    fun addRoom(room:Room,callback:(Boolean)->Unit){
        val roomId = roomsRef.push().key?:return
        val roomWithId = room.copy(id = roomId)
        roomsRef.child(roomId).setValue(roomWithId)
            .addOnSuccessListener {
                Log.i("thanhhhhhcongggg","Thanhcongggggggggggg")
                callback(true)
            }
            .addOnFailureListener{
                callback(false)
            }
    }
    fun getRoomById(roomId:String,callback: (Room?) -> Unit){
        roomsRef.child(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val room = snapshot.getValue(Room::class.java)
                        if (room != null && room.dele!="true") {
                            callback(room.copy(id = snapshot.key ?: ""))
                        } else {
                            callback(null)
                        }
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
    fun updateRoom(room:Room,callback:(Boolean)->Unit){
        room.id?.let {
            roomId->
            roomsRef.child(roomId).setValue(room)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener{
                    callback(false)
                }
        }?:callback(false)
    }
    fun deleteRoom(roomId:String,callback: (Boolean) -> Unit)
    {
        roomsRef.child(roomId).updateChildren(mapOf("dele" to "true"))
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener{
                callback(false)
            }
    }
}