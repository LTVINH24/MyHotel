package com.xinchaongaymoi.hotelbookingapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.databinding.ItemExtraImageBinding

class ExtraImagesAdapter(
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ExtraImagesAdapter.ViewHolder>() {
    
    private val images = mutableListOf<Uri>()
    private val existingImageUrls = mutableListOf<String>()

    inner class ViewHolder(val binding: ItemExtraImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExtraImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = existingImageUrls.size + images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < existingImageUrls.size) {
            Glide.with(holder.binding.imageView.context)
                .load(existingImageUrls[position])
                .into(holder.binding.imageView)
        } else {
            val image = images[position - existingImageUrls.size]
            holder.binding.imageView.setImageURI(image)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(position)
        }
    }

    fun addImage(uri: Uri) {
        images.add(uri)
        notifyItemInserted(images.size + existingImageUrls.size - 1)
    }

    fun addImageUrl(url: String) {
        existingImageUrls.add(url)
        notifyItemInserted(existingImageUrls.size - 1)
    }

    fun removeImage(position: Int) {
        if (position < existingImageUrls.size) {
            existingImageUrls.removeAt(position)
        } else {
            images.removeAt(position - existingImageUrls.size)
        }
        notifyItemRemoved(position)
    }

    fun getAllImages(): List<Uri> = images

    fun getExistingImageUrls(): List<String> = existingImageUrls

    fun clearAll() {
        val size = images.size + existingImageUrls.size
        images.clear()
        existingImageUrls.clear()
        notifyItemRangeRemoved(0, size)
    }
}