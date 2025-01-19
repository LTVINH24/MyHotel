package com.xinchaongaymoi.hotelbookingapp.components.review


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xinchaongaymoi.hotelbookingapp.model.ReviewDetail
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.service.ReviewService
import com.xinchaongaymoi.hotelbookingapp.service.UserService

class RoomReviewsViewModel:ViewModel() {
    private val reviewService = ReviewService()
    private val userService = UserService()
    private val _reviews = MutableLiveData<List<ReviewDetail>>()
    val reviews: LiveData<List<ReviewDetail>> = _reviews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadReviews(roomId:String)
    {
        _isLoading.value=true

        reviewService.getReviewsByRoomId(roomId){
            reviews->
            if(reviews.isEmpty())
            {
                _reviews.postValue(emptyList())
                _isLoading.value=false
                return@getReviewsByRoomId
            }
            val reviewsWithUser =mutableListOf<ReviewDetail>()
            var completedCount = 0
            reviews.forEach{
                review->
                userService.getUserById(review.userId)
                {
                    user->
                    if(user!=null)
                    {
                        reviewsWithUser.add(ReviewDetail(review,user, Room()))
                    }
                    completedCount++

                    if(completedCount==reviews.size)
                    {
                        _reviews.postValue(reviewsWithUser)
                        _isLoading.value=false
                    }
                }
            }
        }
    }
}