package com.booknest.app.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.booknest.app.data.local.entities.CartItem

class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.bookCoverId == newItem.bookCoverId
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}