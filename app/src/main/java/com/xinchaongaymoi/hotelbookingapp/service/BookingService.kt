package com.xinchaongaymoi.hotelbookingapp.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xinchaongaymoi.hotelbookingapp.model.Booking
import com.xinchaongaymoi.hotelbookingapp.model.BookingHistory
import com.xinchaongaymoi.hotelbookingapp.model.BookingWithDetails
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class BookingService {
    private val database = FirebaseDatabase.getInstance().reference
    private val bookingsRef =database.child("Booking")

    fun createBooking(
        roomId:String
        ,userId:String,
        checkInDate:String,
        checkOutDate:String,
        totalPrice:Double,
        callback:(Boolean,String?)->Unit
    ){
        val bookingId = bookingsRef.push().key?:return
        val booking = Booking(
            id =bookingId,
            roomId = roomId,
            userId = userId,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            status = "pending",
            createdAt =  SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            totalPrice =totalPrice
        )
        bookingsRef.child(bookingId).setValue(booking)
            .addOnSuccessListener {
                callback(true,bookingId)
            }
            .addOnFailureListener{
                e->callback(false,e.message)
            }
    }
    fun getBookingHistoryByUserId(userId: String,callback: (List<BookingHistory>) -> Unit){
        bookingsRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   val bookingHistoryList = mutableListOf<BookingHistory>()
                    var completedCount = 0
                    val totalBooking = snapshot.childrenCount
                    if(totalBooking==0L){
                        callback(bookingHistoryList)
                        return
                    }
                    for (bookingSnapshot in snapshot.children){
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let{
                            currentBooking->
                            database.child("rooms").child(currentBooking.roomId)
                                .addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val room = snapshot.getValue(Room::class.java)
                                        room?.let {
                                            bookingHistoryList.add(BookingHistory(currentBooking,room))
                                        }
                                        completedCount++
                                        if(completedCount.toLong() ==totalBooking){
                                            val sortedList = bookingHistoryList.sortedByDescending { 
                                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                                    .parse(it.booking.createdAt)
                                            }
                                            callback(sortedList)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        completedCount++
                                        if(completedCount.toLong() ==totalBooking){
                                            callback(bookingHistoryList)
                                        }
                                    }

                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                   callback(emptyList())
                }
            })
    }
    fun cancelBooking(bookingId:String,callback: (Boolean) -> Unit){
        bookingsRef.child(bookingId)
            .child("status")
            .setValue("cancelled")
            .addOnCompleteListener{
                task->callback(task.isSuccessful)
            }
    }
    fun getBookingsByStatus(status:String,callback:(List<BookingWithDetails>)->Unit)
    {
        bookingsRef.orderByChild("status").equalTo(status)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookingsList = mutableListOf<BookingWithDetails>()
                    var completedCount = 0
                    val totalBookings = snapshot.childrenCount
                    if (totalBookings == 0L) {
                        callback(bookingsList)
                        return
                    }
                    for (bookingSnapshot in snapshot.children)
                    {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let {
                            currentBooking->
                            database.child("rooms").child(currentBooking.roomId)
                                .addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(roomSnapshot: DataSnapshot) {
                                        val room = roomSnapshot.getValue(Room::class.java)
                                        database.child("user").child(currentBooking.userId)
                                            .addListenerForSingleValueEvent(object :ValueEventListener{
                                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                                    val user = userSnapshot.getValue(User::class.java)
                                                    if(room!=null&&user!=null){
                                                        bookingsList.add(BookingWithDetails(currentBooking,room,user))
                                                    }
                                                    completedCount++
                                                    if(completedCount.toLong()==totalBookings){
                                                        callback(bookingsList)
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    completedCount++
                                                    if (completedCount.toLong() == totalBookings) {
                                                        callback(bookingsList)
                                                    }
                                                }

                                            })
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        completedCount++
                                        if (completedCount.toLong() == totalBookings) {
                                            callback(bookingsList)
                                        }
                                    }

                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }
    fun getBookingsDetailById(bookingId:String,callback:(BookingWithDetails?)->Unit)
    {
        bookingsRef.child(bookingId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(bookingSnapshot: DataSnapshot) {
                    val booking = bookingSnapshot.getValue(Booking::class.java)
                    booking?.let{
                        currentBooking->
                        database.child("rooms").child(currentBooking.roomId)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(roomSnapshot: DataSnapshot) {
                                    val room =  roomSnapshot.getValue(Room::class.java)
                                    database.child("user").child(currentBooking.userId)
                                        .addListenerForSingleValueEvent(object :ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val user = snapshot.getValue(User::class.java)
                                                if(room!=null&&user!=null){
                                                    callback(BookingWithDetails(currentBooking,room,user))
                                                }
                                                else{
                                                    callback(null)
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                callback(null)

                                            }

                                        })

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    callback(null)

                                }

                            })
                    }?: run {
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)

                }

            })
    }
    fun updateBookingStatus(bookingId:String,newStatus:String,callback: (Boolean) -> Unit){
        bookingsRef.child(bookingId)
            .child("status")
            .setValue(newStatus)
            .addOnCompleteListener { task->callback(task.isSuccessful) }
    }
    fun updateBookingCheckoutStatus(bookingId:String,newCheckoutStatus:String,callback: (Boolean) -> Unit){
        bookingsRef.child(bookingId)
            .child("checkoutStatus")
            .setValue(newCheckoutStatus)
            .addOnCompleteListener { task->callback(task.isSuccessful) }
    }

    // Thêm phương thức deleteBooking
    fun deleteBooking(bookingId: String, callback: (Boolean) -> Unit) {
        bookingsRef.child(bookingId)
            .removeValue()  // Xoá booking khỏi Firebase
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }
}