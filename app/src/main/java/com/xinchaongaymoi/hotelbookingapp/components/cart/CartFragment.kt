package com.xinchaongaymoi.hotelbookingapp.components.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showFoodOrderFragment()
    }


    private fun showFoodOrderFragment() {
        replaceFragment(FoodOrderFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction() // dùng childFragmentManager cho fragment trong fragment
            .replace(R.id.fragment_container_cart, fragment) // fragment_container là id layout container trong layout của MainFragment
            .commit()
    }
}