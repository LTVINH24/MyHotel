package com.xinchaongaymoi.hotelbookingapp.components

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.xinchaongaymoi.hotelbookingapp.databinding.DialogReviewBinding
import com.xinchaongaymoi.hotelbookingapp.model.Review
import com.xinchaongaymoi.hotelbookingapp.service.ReviewService

class ReviewDialog(
    private val context: Context,
    private val roomId: String,
    private val userId: String,
    private val onReviewSubmitted: () -> Unit
)  {
    private val dialog : Dialog = Dialog(context)
    private val reviewService = ReviewService()
    private lateinit var binding : DialogReviewBinding
    fun show()
    {
        binding = DialogReviewBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setupListeners()
        dialog.show()
    }
    private fun setupListeners()
    {
        binding.btnCancel.setOnClickListener{
            dialog.dismiss()
        }
        binding.btnSubmit.setOnClickListener{
            submitReview()
        }
    }
    private fun submitReview()
    {
        val rating = binding.ratingBar.rating
        val comment = binding.etComment.text.toString()
        if (rating == 0f) {
            Toast.makeText(context, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show()
            return
        }

        if (comment.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show()
            return
        }
        val review = Review(
            roomId = roomId,
            userId = userId,
            comment = comment,
            rating = rating
        )
        binding.btnSubmit.isEnabled = false
        reviewService.addReview(review) { success ->
            (context as? Activity)?.runOnUiThread {
                binding.btnSubmit.isEnabled = true
                if (success) {
                    Toast.makeText(context, "Đánh giá thành công", Toast.LENGTH_SHORT).show()
                    onReviewSubmitted()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}