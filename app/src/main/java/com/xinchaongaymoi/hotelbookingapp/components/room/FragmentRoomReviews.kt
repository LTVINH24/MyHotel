package com.xinchaongaymoi.hotelbookingapp.components.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xinchaongaymoi.hotelbookingapp.adapter.ReviewAdapter
import com.xinchaongaymoi.hotelbookingapp.components.review.RoomReviewsViewModel
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentRoomReviewsBinding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager

class FragmentRoomReviews : Fragment() {
    private var _binding : FragmentRoomReviewsBinding?=null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewAdapter
    private val viewModel: RoomReviewsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomReviewsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        arguments?.getString("ROOM_REVIEW_ID")?.let { roomId ->
            viewModel.loadReviews(roomId)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.rvReviews.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    private fun setupObservers() {
        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            reviewAdapter.updateReviews(reviews)
            binding.tvNoReviews.visibility = if (reviews.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    companion object {
        fun newInstance(roomId: String) = RoomDetailFragment().apply {
            arguments = Bundle().apply {
                putString("ROOM_ID", roomId)
            }
        }
    }
}