package com.xinchaongaymoi.hotelbookingapp.components.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applozic.mobicomkit.api.account.register.RegistrationResponse
import com.google.firebase.auth.FirebaseAuth
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.activity.AccountDetailActivity
import com.xinchaongaymoi.hotelbookingapp.activity.CustomerChatActivity
import com.xinchaongaymoi.hotelbookingapp.activity.LoginActivity
import com.xinchaongaymoi.hotelbookingapp.activity.ManageAccountsActivity
import com.xinchaongaymoi.hotelbookingapp.model.AccountPageItem
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAccountBinding
import com.xinchaongaymoi.hotelbookingapp.components.LanguageBottomSheet
import com.xinchaongaymoi.hotelbookingapp.components.home.AccountViewModel
import io.kommunicate.Kommunicate
import io.kommunicate.callbacks.KMLoginHandler
import io.kommunicate.callbacks.KMLogoutHandler
import io.kommunicate.callbacks.KmCallback
import io.kommunicate.users.KMUser

class AccountFragment : Fragment() {

private var _binding: FragmentAccountBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private lateinit var sharedPreferences: SharedPreferences
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
      val chatButton: View = binding.button2
        chatButton.setOnClickListener {
            sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val _email = sharedPreferences.getString("email", null)
            val _phone = sharedPreferences.getString("phone", null)
            val _name = sharedPreferences.getString("name", null)
//            var intent = Intent(requireActivity(), CustomerChatActivity::class.java)
//            startActivity(intent)
//
//            Kommunicate.openConversation(this.requireContext(), null, null);
            val user = KMUser().apply {
                if (_email != null) {
                    userId = _email
                    email = _email
                }
                if (_phone != null) {
                    contactNumber = _phone
                }
                if (_name != null) {
                    displayName = _name
                }
            }
            Kommunicate.login(requireActivity(), user, object : KMLoginHandler {
                override fun onSuccess(registrationResponse: RegistrationResponse, context: Context) {
                    Kommunicate.openConversation(requireActivity())
                // You can perform operations such as opening the conversation, creating a new conversation or update user details on success
                }

                override fun onFailure(
                    registrationResponse: RegistrationResponse,
                    exception: java.lang.Exception
                ) {
                    // You can perform actions such as repeating the login call or throw an error message on failure
                }
            })
        }
      val accountItemList = listOf(
          AccountPageItem(R.drawable.ic_account, getString(R.string.string_account)){
              val intent = Intent(requireActivity(),AccountDetailActivity::class.java)
              startActivity(intent)
          },
          AccountPageItem(R.drawable.ic_booking, getString(R.string.bookings_history)){
              findNavController().navigate(
                  R.id.action_accountFragment_to_bookingHistoryFragment
              )
          } ,

          AccountPageItem(R.drawable.ic_star, getString(R.string.my_reviews)){
              findNavController().navigate(R.id.action_accountFragment_to_roomReviewsHistoryFragment)
          } ,
          AccountPageItem(R.drawable.ic_logout, getString(R.string.log_out)){
              Kommunicate.logout(context, object : KMLogoutHandler {
                  override fun onSuccess(context: Context?) {
                      Log.i("Logout", "Success")
                  }

                  override fun onFailure(exception: Exception?) {
                      Log.i("Logout", "Failed")
                  }
              })
              val auth = FirebaseAuth.getInstance()
              auth.signOut()
              val intent = Intent(requireActivity(),LoginActivity::class.java)
              startActivity(intent)
          },
          AccountPageItem(R.drawable.ic_switch, getString(R.string.switch_account)){
              val intent = Intent(requireActivity(), ManageAccountsActivity::class.java)
              startActivity(intent)

          }

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