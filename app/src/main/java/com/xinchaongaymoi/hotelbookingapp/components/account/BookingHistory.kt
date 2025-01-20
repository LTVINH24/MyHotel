package com.xinchaongaymoi.hotelbookingapp.components.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.activity.ReviewActivity
import com.xinchaongaymoi.hotelbookingapp.adapter.BookingHistoryAdapter
import com.xinchaongaymoi.hotelbookingapp.components.ReviewDialog
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentBookingHistoryBinding
import com.xinchaongaymoi.hotelbookingapp.service.BookingService


class BookingHistory : Fragment() {
private lateinit var _binding :FragmentBookingHistoryBinding
private lateinit var bookingService: BookingService
private lateinit var adapter: BookingHistoryAdapter
private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentBookingHistoryBinding.inflate(inflater,container,false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingService = BookingService()
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs",Context.MODE_PRIVATE)
        setRecyclerView()
        loadBookingHistory()
    }
    private fun setRecyclerView(){
        var roomId: String? = null
        adapter = BookingHistoryAdapter(
            onCancelClick = { bookingId ->
                showCancelConfirmDialog(bookingId)
            },
            onReviewClick = { roomId ->
                val userId = sharedPreferences.getString("id",null)?: return@BookingHistoryAdapter
                showReviewDialog(roomId,userId)
            }
        )
        _binding.rvBookingHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@BookingHistory.adapter
        }
    }
    private fun showReviewDialog(roomId: String, userId: String)
    {
        val reviewDialog =ReviewDialog(
            requireContext(),
            roomId,
            userId
        )
        {
            Toast.makeText(context, getString(R.string.thank_you_for_review),Toast.LENGTH_SHORT).show()
        }
        reviewDialog.show()
    }
    private fun showCancelConfirmDialog(bookingId:String){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_cancellation_of_booking))
            .setMessage(getString(R.string.are_you_sure_to_cancel_your_booking))
            .setNegativeButton(getString(R.string.no)){ dialog, _->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)){ _, _->
                cancelBooking(bookingId)
            }
            .show()
    }
    private fun cancelBooking(bookingId: String){
        _binding.progressBar.visibility = View.VISIBLE
        bookingService.cancelBooking(bookingId){
            success->activity?.runOnUiThread{
                _binding.progressBar.visibility= View.GONE
            if (success) {
                Toast.makeText(context,
                    getString(R.string.reservation_successfully_canceled), Toast.LENGTH_SHORT).show()
                loadBookingHistory()
            } else {
                Toast.makeText(context,
                    getString(R.string.reservations_cannot_be_canceled), Toast.LENGTH_SHORT).show()
            }
        }
        }
    }
    private fun loadBookingHistory(){
        val userId = sharedPreferences.getString("id", null)
        if(userId==null)
        {
            _binding.tvNoBookings.visibility = View.VISIBLE
            return
        }
        _binding.progressBar.visibility =View.VISIBLE
        bookingService.getBookingHistoryByUserId(userId){
            bookings->
            activity?.runOnUiThread{
                _binding.progressBar.visibility=View.GONE
            }
            if(bookings.isEmpty()){
                _binding.tvNoBookings.visibility=View.VISIBLE
            }
            else{
                _binding.tvNoBookings.visibility =View.GONE
                adapter.updateBookings(bookings)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}