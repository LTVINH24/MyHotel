package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.DatePickerDialog
import android.widget.Button
import com.google.android.material.button.MaterialButton
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentReservationsBinding

class ReservationActivity : AppCompatActivity() {

    private lateinit var binding: FragmentReservationsBinding
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var hourSpinner: Spinner
    private lateinit var minuteSpinner: Spinner
    private lateinit var guestNumberSpinner: Spinner
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var btnCancel: MaterialButton

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentReservationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameEditText = binding.nameEditText
        phoneEditText = binding.phoneEditText
        emailEditText = binding.emailEditText
        dateEditText = binding.dateEditText
        hourSpinner = binding.hourSpinner
        minuteSpinner = binding.minuteSpinner
        guestNumberSpinner = binding.guestNumberSpinner
        messageEditText = binding.messageEditText
        sendButton = binding.sendButton
        btnCancel = binding.btnCancel

        dateEditText.setOnClickListener { showDatePickerDialog() }
        initTimeSpinner()
        initGuestNumberSpinner()

        sendButton.setOnClickListener {
            saveBookingToFirebase()
        }

        btnCancel.setOnClickListener {
            onCancelClick()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
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
        val hours = (6..22).map { String.format("%02d", it) }
        val minutes = (0..59 step 5).map { String.format("%02d", it) }

        val hourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hours)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hourSpinner.adapter = hourAdapter

        val minuteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, minutes)
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        minuteSpinner.adapter = minuteAdapter
    }

    private fun initGuestNumberSpinner() {
        val numbers = (1..30).map { String.format("%02d", it) }
        val guestAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, numbers)
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
            Toast.makeText(this, "Please fill all * fields", Toast.LENGTH_SHORT).show()
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

        Log.d("ReservationActivity", "Attempting to add booking: $bookingData")

        firestore.collection("reservations")
            .add(bookingData)
            .addOnSuccessListener {
                Log.d("ReservationActivity", "Booking added successfully")
                Toast.makeText(this, "Booking successful!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener { e ->
                Log.e("ReservationActivity", "Booking failed: ${e.message}")
                Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearForm() {
        nameEditText.text.clear()
        phoneEditText.text.clear()
        emailEditText.text.clear()
        dateEditText.text.clear()
        messageEditText.text.clear()
    }

    private fun onCancelClick() {
        // Quay lại HomeActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Đóng ReservationActivity
    }
}
