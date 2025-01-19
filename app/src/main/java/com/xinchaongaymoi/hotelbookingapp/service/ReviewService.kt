package com.xinchaongaymoi.hotelbookingapp.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xinchaongaymoi.hotelbookingapp.model.Review

class ReviewService {
    private val database = FirebaseDatabase.getInstance().reference
    private val reviewsRef =database.child("Reviews")
    private val roomsRef = database.child("rooms")
    fun addReview(review:Review,callback:(Boolean)->Unit){
        val reviewId= reviewsRef.push().key?:return
        val reviewWithId = review.copy(id = reviewId)
        reviewsRef.child(reviewId).setValue(reviewWithId)
            .addOnSuccessListener {
                updateRoomRating(review.roomId)
                callback(true)
            }
            .addOnFailureListener{
                callback(false)
            }
    }
    fun getReviewsByRoomId(roomId:String,callback: (List<Review>) -> Unit)
    {
        reviewsRef.orderByChild("roomId").equalTo(roomId)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reviews = snapshot.children.mapNotNull {
                        it.getValue(Review::class.java)
                    }
                    callback(reviews)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }

            })
    }
    private fun updateRoomRating(roomId:String)
    {
        getReviewsByRoomId(roomId)
        {
            reviews ->
            if(reviews.isNotEmpty())
            {
                val averageRating = reviews.map{it.rating}.average()
                roomsRef.child(roomId).child("rating").setValue(averageRating)
            }
        }
    }

}