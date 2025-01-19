package com.xinchaongaymoi.hotelbookingapp.components.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.xinchaongaymoi.hotelbookingapp.adapter.RoomAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentSearchResultBinding
import com.xinchaongaymoi.hotelbookingapp.R
import androidx.navigation.fragment.findNavController

class SearchResultFragment : Fragment() {
    private var BOOKING_REQUEST_CODE = 100
    private val viewModel :SearchViewModel by activityViewModels()
    private lateinit var binding:FragmentSearchResultBinding
    private lateinit var roomAdapter:RoomAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =FragmentSearchResultBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setTabSort()
    }
    private fun setTabSort()
    {
        binding.filterTabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
               when(tab?.position){
                   0 -> viewModel.sortRooms(SearchViewModel.SortOrder.PRICE_LOW_TO_HIGH)
                   1 -> viewModel.sortRooms(SearchViewModel.SortOrder.PRICE_LOW_TO_HIGH)
                   2 -> viewModel.sortRooms(SearchViewModel.SortOrder.PRICE_HIGH_TO_LOW)
                   3 -> viewModel.sortRooms(SearchViewModel.SortOrder.RATING_HIGH_TO_LOW)
               }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun setupRecyclerView()
    {
        roomAdapter = RoomAdapter().apply {
            setOnItemClickListener { room ->
                navigateToRoomDetail(room.id)
            }
        }
        binding.recyclerViewRooms.apply {
            adapter = roomAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    private fun navigateToRoomDetail(roomId: String) {
        findNavController().navigate(
            R.id.action_searchResultFragment_to_roomDetailFragment,
            Bundle().apply {
                putString("ROOM_ID", roomId)
            }
        )
    }
    private fun observeViewModel(){
        viewModel.searchResults.observe(viewLifecycleOwner) { rooms ->
            roomAdapter.updateRooms(rooms)
            binding.tvNoRooms.visibility = if (rooms.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewRooms.visibility = if (rooms.isEmpty()) View.GONE else View.VISIBLE
        }
        viewModel.checkInDate.observe(viewLifecycleOwner) { checkIn ->
            viewModel.checkOutDate.value?.let { checkOut ->
                roomAdapter.setDates(checkIn, checkOut)
            }
        }

        viewModel.checkOutDate.observe(viewLifecycleOwner) { checkOut ->
            viewModel.checkInDate.value?.let { checkIn ->
                roomAdapter.setDates(checkIn, checkOut)
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BOOKING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.checkInDate.value?.let { checkIn ->
                viewModel.checkOutDate.value?.let { checkOut ->
                    viewModel.searchRooms(1, checkIn, checkOut, null)
                }
            }
        }
    }
}