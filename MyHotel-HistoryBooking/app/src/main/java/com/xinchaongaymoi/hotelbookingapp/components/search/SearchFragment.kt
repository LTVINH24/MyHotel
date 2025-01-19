package com.xinchaongaymoi.hotelbookingapp.components.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentSearchBinding
import java.util.Calendar
import com.xinchaongaymoi.hotelbookingapp.R
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinchaongaymoi.hotelbookingapp.adapter.RoomAdapter
import com.xinchaongaymoi.hotelbookingapp.adapter.SearchRoomAdapter

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var binding: FragmentSearchBinding
    private var guestCount = 1
    // Khởi tạo adapters
    private val luxuryRoomAdapter = SearchRoomAdapter(SearchRoomAdapter.TYPE_LUXURY)
    private val royalRoomAdapter = SearchRoomAdapter(SearchRoomAdapter.TYPE_ROYAL)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePickers()
        setupPriceSeekBar()
        setUpSearchBtn()
        setupRecyclerViews()
        setupGuestCounter()
        viewModel.loadRoomsByType()
        observeRoomData()
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val date = String.format("%02d/%02d/%d", day, month + 1, year)
                editText.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupDatePickers() {
        binding.checkInDate.setOnClickListener { showDatePicker(binding.checkInDate) }
        binding.checkOutDate.setOnClickListener { showDatePicker(binding.checkOutDate) }
    }

    private fun setupPriceSeekBar() {
        binding.priceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.priceRangeText.text = "0$ - ${progress}$"
            }


            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }

    private fun setUpSearchBtn() {
        binding.btnSearch.setOnClickListener {

            val checkIn = binding.checkInDate.text.toString().trim()
            val checkOut = binding.checkOutDate.text.toString().trim()
            val maxPrice = binding.priceSeekBar.progress.toDouble().let { 
                if (it > 0) it else null 
            }
            if ((checkIn.isBlank() && checkOut.isBlank()) ) {
                return@setOnClickListener
            }
            viewModel.setDates(checkIn, checkOut)
            viewModel.searchRooms(
                guestCount,
                checkIn.ifBlank { null },
                checkOut.ifBlank { null },
                maxPrice
            )
            findNavController().navigate(R.id.action_searchFragment_to_searchResultFragment)
        }
    }
    private fun setupGuestCounter() {
        binding.guestCountText.text = guestCount.toString()

        binding.decreaseGuests.setOnClickListener {
            if (guestCount > 1) {
                guestCount--
                binding.guestCountText.text = guestCount.toString()
            }
        }
        binding.increaseGuests.setOnClickListener {
            if (guestCount < 10) {
                guestCount++
                binding.guestCountText.text = guestCount.toString()
            }
        }
    }
    private fun setupRecyclerViews() {
        binding.luxuryRoomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = luxuryRoomAdapter
        }

        binding.royalRoomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = royalRoomAdapter
        }

        // Thêm click listeners
        luxuryRoomAdapter.setOnItemClickListener { room ->
            navigateToRoomDetail(room.id)
        }

        royalRoomAdapter.setOnItemClickListener { room ->
            navigateToRoomDetail(room.id)
        }
    }

    private fun navigateToRoomDetail(roomId: String) {
        // Sử dụng Navigation Component để chuyển fragment
        findNavController().navigate(
            R.id.action_searchFragment_to_roomDetailFragment,
            Bundle().apply {
                putString("ROOM_ID", roomId)
            }
        )
    }

    private fun observeRoomData() {
        viewModel.luxuryRooms.observe(viewLifecycleOwner) { rooms ->
            luxuryRoomAdapter.updateRooms(rooms)
        }
        viewModel.royalRooms.observe(viewLifecycleOwner) { rooms ->
            royalRoomAdapter.updateRooms(rooms)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}