package com.xinchaongaymoi.hotelbookingapp.components.admin.checkinout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminCheckInOutDetailBinding
import com.xinchaongaymoi.hotelbookingapp.model.BookingWithDetails
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.util.Log
class AdminCheckInOutDetail : Fragment() {
    private var _binding: FragmentAdminCheckInOutDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminCheckInOutDetailViewModel by viewModels()
    private val bookingId : String by lazy {
        arguments?.getString("bookingIdDetail")?:""
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminCheckInOutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (bookingId.isEmpty()) {
          Toast.makeText(context,"Error find room",Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }
        viewModel.loadBookingDetails(bookingId)
        setupObservers()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers(){
        viewModel.loading.observe(viewLifecycleOwner){
            isLoading->
            binding.loadingProgressBar.isVisible = isLoading
        }
        viewModel.bookingDetails.observe(viewLifecycleOwner){
            bookingDetails->
            setupUI(bookingDetails)
        }
        viewModel.updateResult.observe(viewLifecycleOwner){
           success->
            if(success){
                Toast.makeText(context,"update successfully",Toast.LENGTH_SHORT).show()
                viewModel.loadBookingDetails(bookingId)
            }
            else{
                Toast.makeText(context,"Update failed",Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.updateCheckoutStatusResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context,"update successfully",Toast.LENGTH_SHORT).show()
                viewModel.loadBookingDetails(bookingId)
            } else {
                Toast.makeText(context,"Update failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI(bookingDetails:BookingWithDetails){
        with(binding){
            tvCustomerName.text = "Customer: ${bookingDetails.user.name}"
            tvCustomerEmail.text= "Email: ${bookingDetails.user.email}"
            tvCustomerPhone.text = "Phone: ${bookingDetails.user.phone}"

            Glide.with(requireContext())
                .load(bookingDetails.room.mainImage)
                .into(ivRoomImage)
            tvRoomName.text = "${bookingDetails.room.roomName}"
            tvRoomType.text = "Type: ${bookingDetails.room.roomType}"
            tvRoomPrice.text = "Price: ${bookingDetails.room.pricePerNight}"

            tvBookingId.text = "ID: ${bookingDetails.booking.id}"
            tvCheckInDate.text = "CheckIn: ${bookingDetails.booking.checkInDate}"
            tvCheckOutDate.text= "CheckOut: ${bookingDetails.booking.checkOutDate}"
            tvStatus.text = "Status: ${bookingDetails.booking.status}"
            tvTotalPrice.text="Total: ${bookingDetails.booking.totalPrice}"
            setupControls(bookingDetails)
        }
    }
    private fun setupControls(bookingDetails:BookingWithDetails){
        with(binding){
            val currentStatus = bookingDetails.booking.status
            cardUpdateStatus.isVisible = currentStatus in listOf("pending", "confirmed", "checkin")
            btnUpdateStatus.apply {
                text = viewModel.getUpdateStatusButtonText(currentStatus)
                setOnClickListener{
                    showUpdateStatusConfirmDialog(
                        bookingDetails.booking.id,
                        viewModel.getNextStatus(currentStatus)
                    )
                }
            }
            cardCheckoutStatus.isVisible = viewModel.shouldShowCheckoutStatus(currentStatus)
            if(cardUpdateStatus.isVisible){
                when (bookingDetails.booking.checkoutStatus) {
                    "paid" -> rbPaid.isChecked = true
                    "unpaid" -> rbUnpaid.isChecked = true
                }
                btnUpdateCheckoutStatus.setOnClickListener{
                    val newCheckoutStatus = when {
                        rbPaid.isChecked -> "paid"
                        rbUnpaid.isChecked -> "unpaid"
                        else -> return@setOnClickListener
                    }
                    if (newCheckoutStatus == bookingDetails.booking.checkoutStatus) {
                        Toast.makeText(context,"Please choose another payment status",Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    showUpdateCheckoutStatusConfirmDialog(
                        bookingDetails.booking.id,
                        newCheckoutStatus
                    )
                }
            }

        }
    }
    private fun showUpdateStatusConfirmDialog(bookingId: String, newStatus: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Comfirm")
            .setMessage("Are you sure you want to update your booking status?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.updateBookingStatus(bookingId, newStatus)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun showUpdateCheckoutStatusConfirmDialog(bookingId: String, newCheckoutStatus: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Comfirm")
            .setMessage("Are you sure you want to update your booking checkout-status?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.updateCheckoutStatus(bookingId, newCheckoutStatus)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}