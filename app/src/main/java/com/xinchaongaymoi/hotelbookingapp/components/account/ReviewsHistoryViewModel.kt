package com.xinchaongaymoi.hotelbookingapp.components.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xinchaongaymoi.hotelbookingapp.model.ReviewDetail
import com.xinchaongaymoi.hotelbookingapp.service.ReviewService

class ReviewsHistoryViewModel:ViewModel() {
    private val reviewService = ReviewService()
    private val _reviews = MutableLiveData<List<ReviewDetail>>()
    val reviews: LiveData<List<ReviewDetail>> = _reviews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadReviewsHistory(userId:String)
    {
        _isLoading.value=true
        reviewService.getReviewsByUserId(userId)
        {
            reviews->
            _isLoading.postValue(false)
            _reviews.postValue(reviews)
        }
    }
}