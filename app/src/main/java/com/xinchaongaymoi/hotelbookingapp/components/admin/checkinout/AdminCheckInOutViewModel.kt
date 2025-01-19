package com.xinchaongaymoi.hotelbookingapp.components.admin.checkinout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xinchaongaymoi.hotelbookingapp.model.BookingWithDetails
import com.xinchaongaymoi.hotelbookingapp.service.BookingService
import android.util.Log
class AdminCheckInOutViewModel:ViewModel() {
    private var currentStatus =""
    private var currentSearchQuery = ""
    private val bookingService = BookingService()
    private val _bookings = MutableLiveData<List<BookingWithDetails>>()
    val bookings: LiveData<List<BookingWithDetails>> = _bookings
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    fun loadBookingByStatus(status:String){
        currentStatus = status
        _loading.value=true
        bookingService.getBookingsByStatus(status){
            bookings ->
            val filteredBookings = filterBookings(bookings)
            _bookings.postValue(filteredBookings)
            _loading.postValue(false)
        }
    }
    fun searchBookings(query:String){
        currentSearchQuery =query.lowercase()
        loadBookingByStatus(currentStatus)
    }
   private fun filterBookings(bookings:List<BookingWithDetails>):List<BookingWithDetails>
   {
      return if(currentSearchQuery.isEmpty()){
          bookings
      }
       else{
           bookings.filter { booking
               ->booking.user.name.lowercase().contains(currentSearchQuery)
           }
      }
   }
}