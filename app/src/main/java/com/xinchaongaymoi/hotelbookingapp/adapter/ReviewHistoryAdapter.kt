package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemReviewHistoryBinding
import com.xinchaongaymoi.hotelbookingapp.model.ReviewDetail

class ReviewHistoryAdapter
    : RecyclerView.Adapter<ReviewHistoryAdapter.ReviewViewHolder>() {
    private var reviews = listOf<ReviewDetail>()
    inner class ReviewViewHolder(private val binding:ItemReviewHistoryBinding)
        :RecyclerView.ViewHolder(binding.root){

            fun bind(reviewDetail: ReviewDetail){
                binding.apply {
                    tvRoomName.text= "Room: ${reviewDetail.room.roomName}"
                    tvComment.text = reviewDetail.review.comment
                    ratingBar.rating= reviewDetail.review.rating
                    tvReviewDate.text = "CreatedAt: ${reviewDetail.getFormattedDate()}"
                    Glide.with(binding.root.context)
                        .load(reviewDetail.room.mainImage)
                        .into(imgRoom)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewHistoryBinding.inflate(
            LayoutInflater.from(parent.context)
            ,parent
            ,false
        )
        return ReviewViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return reviews.size
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }
    fun updateReviews(newReviews:List<ReviewDetail>){
        reviews =newReviews
        notifyDataSetChanged()
    }
}