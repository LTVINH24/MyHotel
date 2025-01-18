package com.xinchaongaymoi.hotelbookingapp.components.admin.rooms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.service.RoomService
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
            _isLoading.postValue(false)
            if (rooms != null) {
                _rooms.postValue(rooms)
            } else {
                _error.postValue("Không thể tải danh sách phòng")
            }
        }
    }
    fun deleteRoom(room:Room)
    {
        _isLoading.value=true
        roomService.deleteRoom(room.id){
            success->
            _isLoading.value=false
            if(success)
            {
                loadRooms()
            }
            else{
                _error.value = "Delete failed"
            }
        }
    }
}