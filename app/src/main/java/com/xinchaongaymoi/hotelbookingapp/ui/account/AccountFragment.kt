package com.xinchaongaymoi.hotelbookingapp.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.data.model.AccountPageItem
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAccountBinding
import com.xinchaongaymoi.hotelbookingapp.ui.LanguageBottomSheet
import com.xinchaongaymoi.hotelbookingapp.ui.home.AccountViewModel

class AccountFragment : Fragment() {

private var _binding: FragmentAccountBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val accountViewModel =
        ViewModelProvider(this)[AccountViewModel::class.java]

    _binding = FragmentAccountBinding.inflate(inflater, container, false)
    val root: View = binding.root

//
//    val textView: TextView = binding.textSlideshow
//    accountViewModel.text.observe(viewLifecycleOwner) {
//      textView.text = it
//    }
      val accountRecyclerView: RecyclerView = binding.accountAndSecurityRecyclerView
      val settingsRecyclerView: RecyclerView = binding.settingsRecyclerView
      val accountItemList = listOf(
          AccountPageItem(R.drawable.ic_account, getString(R.string.string_account)){},
          AccountPageItem(R.drawable.ic_star, getString(R.string.my_reviews)){}
      )
      val accountAndSecurityAdapter = AccountPageItemAdapter(accountItemList)
        accountRecyclerView.adapter = accountAndSecurityAdapter
      accountRecyclerView.layoutManager = LinearLayoutManager(this.context)

        val settingsItemList = listOf(
            AccountPageItem(R.drawable.ic_language, getString(R.string.language)){
                // Show language bottom sheet
                val languageBottomSheet = LanguageBottomSheet()
                languageBottomSheet.show(parentFragmentManager, LanguageBottomSheet.TAG)
            }
        )
        val settingsAdapter = AccountPageItemAdapter(settingsItemList)
        settingsRecyclerView.adapter = settingsAdapter
        settingsRecyclerView.layoutManager = LinearLayoutManager(this.context)
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}