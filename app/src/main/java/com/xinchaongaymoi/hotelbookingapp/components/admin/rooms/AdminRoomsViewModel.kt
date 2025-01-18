package com.xinchaongaymoi.hotelbookingapp.components.admin.rooms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.service.RoomService
import android.util.Log
class AdminRoomsViewModel:ViewModel() {
    private val roomService =RoomService()
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> = _rooms
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    fun loadRooms() {
        _isLoading.value = true
        roomService.getAllRooms { rooms ->
//            Log.i("Rommmmmmmmmmmmmmmmm",rooms.toString())
            _isLoading.postValue(false)
            if (rooms != null) {
                _rooms.postValue(rooms)
            } else {
                _error.postValue("Không thể tải danh sách phòng")
            }
        }
    }
}