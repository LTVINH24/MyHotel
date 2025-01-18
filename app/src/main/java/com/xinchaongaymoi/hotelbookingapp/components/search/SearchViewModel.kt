package com.xinchaongaymoi.hotelbookingapp.components.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.Query
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.service.RoomService
import android.util.Log
class SearchViewModel : ViewModel() {
    private val roomService = RoomService()
    private val _checkInDate = MutableLiveData<String>()
    val checkInDate: LiveData<String> = _checkInDate

    private val _checkOutDate = MutableLiveData<String>()
    val checkOutDate: LiveData<String> = _checkOutDate
    
    private val _searchResults = MutableLiveData<List<Room>>()
    val searchResults: LiveData<List<Room>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _luxuryRooms = MutableLiveData<List<Room>>()
    val luxuryRooms: LiveData<List<Room>> = _luxuryRooms

    private val _royalRooms = MutableLiveData<List<Room>>()
    val royalRooms: LiveData<List<Room>> = _royalRooms
    enum class SortOrder
    {
        NONE,
        PRICE_LOW_TO_HIGH,
        PRICE_HIGH_TO_LOW,
        RATING_HIGH_TO_LOW
    }
    fun setDates(checkIn: String, checkOut: String) {
        _checkInDate.value = checkIn
        _checkOutDate.value = checkOut
    }
    fun searchRooms(guestCount: Int, checkIn: String?, checkOut: String?, maxPrice: Double?) {
        _isLoading.value = true
        roomService.searchRooms(guestCount, checkIn, checkOut, maxPrice) { rooms ->
            _searchResults.postValue(rooms)
            _isLoading.value = false
        }
    }

    fun loadRoomsByType() {
        _isLoading.value = true
        var loadedTypes = 0

        roomService.getRoomsByType("Luxury") { rooms ->
            _luxuryRooms.postValue(rooms)
            loadedTypes++
            if (loadedTypes == 2) {
                _isLoading.postValue(false)
            }
        }
        roomService.getRoomsByType("Royal") { rooms ->
            _royalRooms.postValue(rooms)
            loadedTypes++
            if (loadedTypes == 2) {
                _isLoading.postValue(false)
            }
        }
    }
    fun sortRooms(sortOrder: SortOrder){
        val currentList = _searchResults.value?:return
        val sortedList = when(sortOrder){
            SortOrder.NONE->currentList
            SortOrder.PRICE_LOW_TO_HIGH->currentList.sortedBy { it.pricePerNight }
            SortOrder.PRICE_HIGH_TO_LOW->currentList.sortedByDescending { it.pricePerNight }
            SortOrder.RATING_HIGH_TO_LOW->currentList.sortedByDescending{it.rating}
        }
        _searchResults.postValue(sortedList)
    }
}