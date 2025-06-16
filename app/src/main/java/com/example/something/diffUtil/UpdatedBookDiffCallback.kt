package com.example.something.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.something.models.ListedBooks

class UpdatedBookDiffCallback : DiffUtil.ItemCallback<ListedBooks>(){
    override fun areItemsTheSame(oldItem: ListedBooks, newItem: ListedBooks): Boolean {
        return oldItem.bookCoverId == newItem.bookCoverId
    }

    override fun areContentsTheSame(oldItem: ListedBooks, newItem: ListedBooks): Boolean {
        return oldItem == newItem
    }
}