package com.xinchaongaymoi.hotelbookingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.model.UserAccount

class AccountAdapter(
    private val context: Context,
    private val accounts: List<UserAccount>,
    private val onAccountClick: (UserAccount) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDisplayName: TextView = view.findViewById(R.id.tvDisplayName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)

        fun bind(account: UserAccount) {
            tvDisplayName.text = account.displayName
            tvEmail.text = account.email
            itemView.setOnClickListener {
                onAccountClick(account)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_account_layout, parent, false) // Use custom layout
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(accounts[position])
    }

    override fun getItemCount(): Int = accounts.size
}

