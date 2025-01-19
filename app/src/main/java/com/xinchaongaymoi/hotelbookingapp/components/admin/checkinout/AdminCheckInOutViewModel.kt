package com.xinchaongaymoi.hotelbookingapp.components.admin.checkinout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xinchaongaymoi.hotelbookingapp.model.BookingWithDetails
import com.xinchaongaymoi.hotelbookingapp.service.BookingService
import android.util.Log
class AdminCheckInOutViewModel:ViewModel() {
    private val bookingService = BookingService()
    private val _bookings = MutableLiveData<List<BookingWithDetails>>()
    val bookings: LiveData<List<BookingWithDetails>> = _bookings
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    fun loadBookingByStatus(status:String){
        _loading.value=true
        bookingService.getBookingsByStatus(status){
            bookings ->
            _bookings.postValue(bookings)
            _loading.postValue(false)
        }
    }
    fun updateBookingStatus(bookingId:String,newStatus:String,callback:(Boolean)->Unit){
        bookingService.updateBookingStatus(bookingId,newStatus,callback)
    }
    fun updateBookingCheckOutStatus(bookingId:String,newCheckOutStatus:String,callback:(Boolean)->Unit){
        bookingService.updateBookingCheckoutStatus(bookingId,newCheckOutStatus,callback)
    }
}