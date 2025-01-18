package com.xinchaongaymoi.hotelbookingapp.components.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.model.FoodItem

class FoodOrderFragment : Fragment() {
    private lateinit var filterTabs: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_food_order, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterTabs = view.findViewById(R.id.filterTabs)

        showBookingFragment() // hiển thị fragment đặt bàn khi khởi động

        filterTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showBookingFragment()
//                    1 -> showFastFoodFragment()
//                    2 -> showDrinkFragment()
//                    3 -> showBuffetFragment()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect if needed
            }
        })
    }


    private fun showBookingFragment() {
        replaceFragment(ReservationsFragment())
    }

//    private fun showFastFoodFragment() {
//        replaceFragment(FastFoodFragment())
//    }
//
//    private fun showDrinkFragment() {
//        replaceFragment(DrinkFragment())
//    }
//
//    private fun showBuffetFragment() {
//        replaceFragment(BuffetFragment())
//    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction() // dùng childFragmentManager cho fragment trong fragment
            .replace(R.id.fragment_container_food, fragment) // fragment_container là id layout container trong layout của MainFragment
            .commit()
    }

}