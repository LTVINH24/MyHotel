package com.xinchaongaymoi.hotelbookingapp.adapter

import androidx.compose.ui.text.LinkAnnotation

class ExtraImagesAdapter(
    private val images:MutableList<LinkAnnotation.Url> = mutableListOf(),
    private val onDeleteClick: (Int)->Unit
) {
}