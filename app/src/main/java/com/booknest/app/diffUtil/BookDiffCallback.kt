package com.booknest.app.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.booknest.app.models.ListedBooks

class BookDiffCallback : DiffUtil.ItemCallback<ListedBooks>() {
    override fun areItemsTheSame(oldItem: ListedBooks, newItem: ListedBooks): Boolean {
        return oldItem.bookCoverId == newItem.bookCoverId  // Compare unique identifiers
    }

    override fun areContentsTheSame(oldItem: ListedBooks, newItem: ListedBooks): Boolean {
        return oldItem == newItem  // Compare content
    }
}
