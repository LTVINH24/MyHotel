package com.xinchaongaymoi.hotelbookingapp.components.account

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentCheckdayBookDetailsBinding
import java.util.Calendar

class DatePickerFragment : DialogFragment() {
    private lateinit var _binding: FragmentCheckdayBookDetailsBinding

    public var onBookingConfirmed: ((String, String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckdayBookDetailsBinding.inflate(inflater, container, false)

        // Thiết lập sự kiện click cho check-in date
        _binding.checkInDate.setOnClickListener {
            showDatePicker { date ->
                _binding.checkInDate.setText(date)
            }
        }

        // Thiết lập sự kiện click cho check-out date
        _binding.checkOutDate.setOnClickListener {
            showDatePicker { date ->
                _binding.checkOutDate.setText(date)
            }
        }

        _binding.btnCancel.setOnClickListener {

            dismiss()
        }

        // Thiết lập sự kiện confirm button
        _binding.btnConfirm.setOnClickListener {
            val checkIn = _binding.checkInDate.text.toString()
            val checkOut = _binding.checkOutDate.text.toString()

            if (checkIn.isEmpty() || checkOut.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Gọi callback khi xác nhận booking
                onBookingConfirmed?.invoke(checkIn, checkOut)
                parentFragmentManager.popBackStack()  // Đóng fragment sau khi xác nhận
            }
        }

        return _binding.root
    }

    // Hàm để hiển thị DatePickerDialog
    private fun showDatePicker(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSet(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }


}