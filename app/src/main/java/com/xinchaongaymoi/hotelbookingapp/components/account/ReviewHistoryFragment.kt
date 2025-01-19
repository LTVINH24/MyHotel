package com.xinchaongaymoi.hotelbookingapp.components.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinchaongaymoi.hotelbookingapp.adapter.ReviewHistoryAdapter
import com.xinchaongaymoi.hotelbookingapp.components.search.RoomDetailFragment
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentReviewHistoryBinding

class ReviewHistoryFragment : Fragment() {
    private var _binding : FragmentReviewHistoryBinding?=null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewHistoryAdapter
    private val viewModel: ReviewsHistoryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewHistoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        loadReviewHistory()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewHistoryAdapter()
        binding.rvReviewHistory.apply {
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
    private fun loadReviewHistory()
    {
        val userId = requireActivity().getSharedPreferences("UserPrefs",Context.MODE_PRIVATE)
            .getString("id",null)
        if(userId==null)
        {
            binding.tvNoReviews.visibility=View.VISIBLE
            binding.tvNoReviews.text="Please login to see reviews"
            return
        }
        viewModel.loadReviewsHistory(userId)
    }

    companion object {
        fun newInstance(roomId: String) = RoomDetailFragment().apply {
            arguments = Bundle().apply {
                putString("ROOM_ID", roomId)
            }
        }
    }
}