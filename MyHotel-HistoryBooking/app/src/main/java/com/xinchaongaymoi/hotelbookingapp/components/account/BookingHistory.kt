package com.xinchaongaymoi.hotelbookingapp.components.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xinchaongaymoi.hotelbookingapp.activity.BookingActivity
import com.xinchaongaymoi.hotelbookingapp.adapter.BookingHistoryAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentBookingHistoryBinding
import com.xinchaongaymoi.hotelbookingapp.model.BookingHistory
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

        _binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()  // Quay lại fragment trước đó
        }

    }
    private fun setRecyclerView() {
        // Sửa lại: Truyền vào callback đúng kiểu
        adapter = BookingHistoryAdapter(
            bookings = mutableListOf(),
            onCancelClick = { bookingId ->
                showCancelConfirmDialog(bookingId)
            },
            onShowDatePicker = {bookingHistory -> showDatePickerFragment(bookingHistory) } , // Gọi hàm showDatePickerFragment()
            onDeleteClick = { bookingHistory ->
                // Xử lý việc xoá booking từ server
                deleteBookingFromServer(bookingHistory)
            }
        )
        _binding.rvBookingHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@BookingHistory.adapter
            // Sử dụng ép kiểu an toàn
            (adapter as? BookingHistoryAdapter)?.let {
                ItemTouchHelper(it.getItemTouchHelper()).attachToRecyclerView(this)
            }
        }




    }
    private fun showCancelConfirmDialog(bookingId:String){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm cancellation of booking")
            .setMessage("Are you sure to cancel your booking?")
            .setNegativeButton("No"){dialog,_->
                dialog.dismiss()
            }
            .setPositiveButton("Yes"){_,_->
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
                Toast.makeText(context, "Reservation successfully canceled", Toast.LENGTH_SHORT).show()
                loadBookingHistory()
            } else {
                Toast.makeText(context, "Reservations cannot be canceled", Toast.LENGTH_SHORT).show()
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
    private fun showDatePickerFragment(bookingHistory: BookingHistory) {
        val datePickerFragment = DatePickerFragment().apply {
            onBookingConfirmed = { checkIn, checkOut ->
                // Truyền dữ liệu qua Intent đến màn hình BookingActivity
                val intent = Intent(context, BookingActivity::class.java).apply {
                    // Thêm các giá trị vào Intent
                    putExtra("Room_ID", bookingHistory.room.id)
                    putExtra("CHECK_IN", checkIn) // Ngày check-in được chọn
                    putExtra("CHECK_OUT", checkOut) // Ngày check-out được chọn
                }
                // Chuyển sang BookingActivity
                startActivity(intent)
            }
        }
        datePickerFragment.show(parentFragmentManager, "datePickerFragment")
    }

    private fun deleteBookingFromServer(bookingHistory: BookingHistory) {
        // Xử lý việc xoá booking từ server, ví dụ gọi API xoá booking
        bookingService.deleteBooking(bookingHistory.booking.id) { success ->
            if (!success) {
                Toast.makeText(context, "Failed to delete booking", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()

    }
}