package com.xinchaongaymoi.hotelbookingapp.components.admin.checkinout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminCheckinoutBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.xinchaongaymoi.hotelbookingapp.adapter.BookingManagementAdapter
import com.xinchaongaymoi.hotelbookingapp.R

class AdminCheckInOutFragment : Fragment() {
    private var _binding: FragmentAdminCheckinoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminCheckInOutViewModel by viewModels()
    private lateinit var adapter:BookingManagementAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminCheckinoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabLayout()
        setupSearchView()
        setupObservers()
        viewModel.loadBookingByStatus("pending")
    }
    private fun setupRecyclerView()
    {
        adapter =BookingManagementAdapter{
            booking->
            val bundle =Bundle().apply {
                putString("bookingIdDetail",booking.booking.id)
            }
            findNavController().navigate(R.id.action_adminCheckInOutFragment_to_adminCheckInOutDetail,bundle)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@AdminCheckInOutFragment.adapter
        }
    }
    private fun setupTabLayout()
    {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0->viewModel.loadBookingByStatus("pending")
                    1->viewModel.loadBookingByStatus("confirmed")
                    2->viewModel.loadBookingByStatus("checkin")
                }

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun setupSearchView(){
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchBookings(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.searchBookings(it)
                }
                return true
            }
        })
    }
    private fun setupObservers(){
        viewModel.bookings.observe(viewLifecycleOwner){
            bookings->adapter.updateBookings(bookings)
        }
        viewModel.loading.observe(viewLifecycleOwner){
            isLoading->binding.loadingProgressBar.isVisible= isLoading
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 