package com.xinchaongaymoi.hotelbookingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xinchaongaymoi.hotelbookingapp.App
import com.xinchaongaymoi.hotelbookingapp.R


class LanguageAdapter(
        private val languages: List<String>,
        private val onLanguageSelected: (String) -> Unit
    ) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {
        class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.language_name)
            val tickImage: View = itemView.findViewById(R.id.tick_image)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.language_item, parent, false)
            return LanguageViewHolder(view)
        }

        override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
            val language = languages[position]
            holder.textView.text = language
            if(App.instance.language == language)
            {
                holder.tickImage.visibility = View.VISIBLE
            }
            else
            {
                holder.tickImage.visibility = View.GONE
            }
            holder.itemView.setOnClickListener{
                onLanguageSelected(language)
                if(App.instance.language != language)
                {
                    notifyItemChanged(languages.indexOf(App.instance.language))
                    notifyItemChanged(position)
                }
            }
        }

        override fun getItemCount(): Int = languages.size
    }
