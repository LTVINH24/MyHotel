package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemAdminUserBinding
import com.xinchaongaymoi.hotelbookingapp.model.User

class AdminUserAdapter(
    private val onUserDetail: (User) -> Unit,
    private val onToggleBan: (User) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var users = listOf<User>()
    private val HEADER_TYPE = 0
    private val ITEM_TYPE = 1

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER_TYPE else ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_admin_user_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val binding = ItemAdminUserBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                UserViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                holder.bind(users[position - 1])
            }
        }
    }

    override fun getItemCount() = users.size + 1

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class UserViewHolder(private val binding: ItemAdminUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email
            binding.tvUserRole.text = when(user.role.lowercase()) {
                "admin" -> "Quản trị viên"
                else -> "Người dùng"
            }

            binding.btnMore.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    menuInflater.inflate(R.menu.admin_user_menu, menu)
                    
                    // Ẩn tùy chọn ban/unban nếu là admin
                    if (user.role.lowercase() == "admin") {
                        menu.findItem(R.id.action_toggle_ban).isVisible = false
                    } else {
                        menu.findItem(R.id.action_toggle_ban).title = 
                            if (user.isBanned) "Unban tài khoản" else "Ban tài khoản"
                    }
                    
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_view_detail -> {
                                onUserDetail(user)
                                true
                            }
                            R.id.action_toggle_ban -> {
                                onToggleBan(user)
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }
        }
    }
} 