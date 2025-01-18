package com.xinchaongaymoi.hotelbookingapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemExtraImageBinding

class ExtraImagesAdapter(
    private val images:MutableList<Uri> = mutableListOf(),
    private val onDeleteClick: (Int)->Unit
):RecyclerView.Adapter<ExtraImagesAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemExtraImageBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExtraImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val image = images[position]
        holder.binding.imageView.setImageURI(image)
        holder.binding.btnDelete.setOnClickListener{
            onDeleteClick(position)
        }
    }
    fun addImage(uri:Uri){
        images.add(uri)
        notifyItemInserted(images.size-1)
    }
    fun removeImage(position: Int)
    {
        images.removeAt(position)
        notifyItemRemoved(position)
    }
    fun getAllImages() =images.toList()
}