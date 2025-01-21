package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xinchaongaymoi.hotelbookingapp.model.ReviewDetail
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemReviewBinding
class ReviewAdapter(private var reviews : List<ReviewDetail> = emptyList())
    : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>()
{

    class ReviewViewHolder(private val binding:ItemReviewBinding ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reviewWithUser: ReviewDetail) {
            binding.apply {
                tvUserName.text = reviewWithUser.user.name
                ratingBar.rating = reviewWithUser.review.rating
                tvComment.text = reviewWithUser.review.comment
                tvReviewTime.text = reviewWithUser.getFormattedDate()
            }
        }
    }




    override fun onBindViewHolder(holder: ReviewAdapter.ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReviewViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
    fun updateReviews(newReviews: List<ReviewDetail>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}