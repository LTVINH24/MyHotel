package com.xinchaongaymoi.hotelbookingapp.components.cart

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.xinchaongaymoi.hotelbookingapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservationsFragment : Fragment() {
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var hourSpinner: Spinner
    private lateinit var minuteSpinner: Spinner
    private lateinit var guestNumberSpinner: Spinner
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button

    private val firestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reservations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.nameEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        dateEditText = view.findViewById(R.id.dateEditText)
        hourSpinner = view.findViewById(R.id.hourSpinner)
        minuteSpinner = view.findViewById(R.id.minuteSpinner)
        guestNumberSpinner = view.findViewById(R.id.guestNumberSpinner)
        messageEditText = view.findViewById(R.id.messageEditText)
        sendButton = view.findViewById(R.id.sendButton)

        dateEditText.setOnClickListener { showDatePickerDialog() }
        initTimeSpinner()
        initGuestNumberSpinner()

        sendButton.setOnClickListener {
            saveBookingToFirebase()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateEditText.setText(dateFormat.format(selectedDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    private fun initTimeSpinner() {
        val hours = (0..23).map { String.format("%02d", it) }
        val minutes = (0..59 step 5).map { String.format("%02d", it) }


        val hourAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hours)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hourSpinner.adapter = hourAdapter

        val minuteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, minutes)
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        minuteSpinner.adapter = minuteAdapter
    }

    private fun initGuestNumberSpinner(){
        val numbers = (1..30).map { String.format("%02d", it) }
        val guestAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, numbers)
        guestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        guestNumberSpinner.adapter = guestAdapter
    }


    private fun saveBookingToFirebase() {
        val name = nameEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val email = emailEditText.text.toString()
        val date = dateEditText.text.toString()
        val hour = hourSpinner.selectedItem.toString()
        val minute = minuteSpinner.selectedItem.toString()
        val guestNumber = guestNumberSpinner.selectedItem.toString()
        val message = messageEditText.text.toString()


        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all * fields", Toast.LENGTH_SHORT).show()
            return
        }

        val bookingData = hashMapOf(
            "name" to name,
            "phone_number" to phone,
            "email" to email,
            "date" to date,
            "time" to "$hour:$minute",
            "number_of_people" to guestNumber,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("reservations")
            .add(bookingData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Booking successful!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Booking failed!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearForm(){
        nameEditText.text.clear()
        phoneEditText.text.clear()
        emailEditText.text.clear()
        dateEditText.text.clear()
        messageEditText.text.clear()
    }
}