package com.xinchaongaymoi.hotelbookingapp.components.admin.checkinout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xinchaongaymoi.hotelbookingapp.model.BookingWithDetails
import com.xinchaongaymoi.hotelbookingapp.service.BookingService

class AdminCheckInOutDetailViewModel:ViewModel() {
    private val bookingService = BookingService()

    private val _bookingDetails = MutableLiveData<BookingWithDetails>()
    val bookingDetails: LiveData<BookingWithDetails> = _bookingDetails

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> = _updateResult

    private val _updateCheckoutStatusResult = MutableLiveData<Boolean>()
    val updateCheckoutStatusResult: LiveData<Boolean> = _updateCheckoutStatusResult

    fun  loadBookingDetails(bookingId:String)
    {
        _loading.value=true
        bookingService.getBookingsDetailById(bookingId){
            bookingDetails->
            _loading.value=false
           bookingDetails?.let {
               _bookingDetails.postValue(it)
           }
        }

    }

    fun getNextStatus(currentStatus: String): String {
        return when (currentStatus) {
            "pending" -> "confirmed"
            "confirmed" -> "checkin"
            "checkin" -> "completed"
            else -> currentStatus
        }
    }
    fun getUpdateStatusButtonText(currentStatus: String): String {
        return when (currentStatus) {
            "pending" -> "Confirm booking"
            "confirmed" -> "Check In"
            "checkin" -> "Check Out"
            else -> ""
        }
    }
    fun shouldShowCheckoutStatus(status: String): Boolean {
        return status == "checkin"
    }
    fun updateBookingStatus(bookingId: String, newStatus: String) {
        bookingService.updateBookingStatus(bookingId, newStatus) { success ->
            _updateResult.postValue(success)
        }
    }
    fun updateCheckoutStatus(bookingId: String, checkoutStatus: String) {
        bookingService.updateBookingCheckoutStatus(bookingId, checkoutStatus) { success ->
            _updateResult.postValue(success)
        }
    }

}