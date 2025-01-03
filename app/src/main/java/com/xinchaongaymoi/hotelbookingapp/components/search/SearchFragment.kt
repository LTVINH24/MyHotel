package com.xinchaongaymoi.hotelbookingapp.components.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.xinchaongaymoi.hotelbookingapp.activity.SearchActivity
import com.xinchaongaymoi.hotelbookingapp.adapter.HomeRoomAdapter
import com.xinchaongaymoi.hotelbookingapp.service.RoomService

import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentSearchBinding
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinchaongaymoi.hotelbookingapp.activity.RoomDetailActivity

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var  luxuryRoomRecyclerView:RecyclerView
    private lateinit var  royalRoomRecyclerView:RecyclerView
    private lateinit var luxuryAdapter: HomeRoomAdapter
    private lateinit var royalAdapter: HomeRoomAdapter
    private val roomService = RoomService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val searchBtn :Button = binding.searchBtn
        val searchET :EditText = binding.searchET
        searchBtn.setOnClickListener{
            val intent =Intent(requireContext(), SearchActivity::class.java)
            intent.putExtra("keyWord",searchET.text.toString())
            startActivity(intent)
        }

        royalRoomRecyclerView= binding.recommendRV
        luxuryRoomRecyclerView =binding.bestRV
        royalRoomRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        luxuryRoomRecyclerView.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        getRooms()

        val root: View = binding.root
        homeViewModel.text.observe(viewLifecycleOwner) {}
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRooms(){
        Log.i("Hello","Hello")

        // Xử lý sự kiện click qua onItemClick
        val onItemClick: (String) -> Unit = { roomId ->
            val intent = Intent(requireContext(), RoomDetailActivity::class.java)
            intent.putExtra("ROOM_ID", roomId) // Gửi ROOM_ID qua Intent
            startActivity(intent)
        }

        roomService.getRoomByType("Luxury", callback = {
            roomList->luxuryAdapter= HomeRoomAdapter(roomList, onItemClick)
            Log.i("tesstesxgb",roomList.toString())
            luxuryRoomRecyclerView.adapter=luxuryAdapter
        })

        roomService.getRoomByType("Royal", callback = {
            roomList->royalAdapter= HomeRoomAdapter(roomList, onItemClick)
            royalRoomRecyclerView.adapter=royalAdapter
        })
    }
}