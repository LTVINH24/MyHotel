package com.xinchaongaymoi.hotelbookingapp.components.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.activity.GalleryActivity
import com.xinchaongaymoi.hotelbookingapp.activity.GoogleMapActivity
import com.xinchaongaymoi.hotelbookingapp.activity.ReservationActivity

import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val accountViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root
    val imageView = root.findViewById<ImageView>(R.id.imageView8)
      imageView.setOnClickListener {
          val intent = Intent(requireContext(), GalleryActivity::class.java)
            startActivity(intent)
      }
      val locationText=root.findViewById<ConstraintLayout>(R.id.locationText)
        locationText.setOnClickListener {
            val intent = Intent(requireContext(), GoogleMapActivity::class.java)
            startActivity(intent)
        }

      // Xử lý sự kiện cho nút "Đặt bàn" trong HomeFragment
      val bookTableButton = root.findViewById<Button>(R.id.book_table_button)
      bookTableButton.setOnClickListener {
          // Chuyển sang ReservationActivity
          val intent = Intent(requireContext(), ReservationActivity::class.java)
          startActivity(intent)
      }
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

