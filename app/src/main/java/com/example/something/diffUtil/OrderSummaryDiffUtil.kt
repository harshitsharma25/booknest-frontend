package com.example.something.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.something.data.local.entities.CartItem

class OrderSummaryDiffUtil : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.bookCoverId == newItem.bookCoverId
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}