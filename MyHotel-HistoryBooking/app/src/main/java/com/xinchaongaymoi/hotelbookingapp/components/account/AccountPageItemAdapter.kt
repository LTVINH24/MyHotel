package com.xinchaongaymoi.hotelbookingapp.components.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.model.AccountPageItem

class AccountPageItemAdapter(
    private val items: List<AccountPageItem>,
) : RecyclerView.Adapter<AccountPageItemAdapter.AccountPageViewHolder>() {

    // ViewHolder for item
    class AccountPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.itemIcon)
        val title: TextView = itemView.findViewById(R.id.itemTitle)
        val arrow: ImageView = itemView.findViewById(R.id.itemArrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountPageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_page_item, parent, false)
        return AccountPageViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountPageViewHolder, position: Int) {
        val item = items[position]

        holder.icon.setImageResource(item.iconResId)
        holder.title.text = item.title
        holder.arrow.setImageResource(R.drawable.ic_arrow_right)

        // Handle item click
        holder.itemView.setOnClickListener { item.onItemClick() }
    }

    override fun getItemCount(): Int = items.size
}