package com.example.something.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.something.R
import com.example.something.diffUtil.UpdatedBookDiffCallback
import com.example.something.models.ListedBooks

class FilteredBookAdapter(
    private val context : Context,
    private val onAddToCartClicked : () -> Unit
) : ListAdapter<ListedBooks, FilteredBookAdapter.UpdatedBookViewHolder>(UpdatedBookDiffCallback()) {

    class UpdatedBookViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatedBookViewHolder {
        return UpdatedBookViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book,parent,false))
    }

    override fun onBindViewHolder(holder: UpdatedBookViewHolder, position: Int) {
        if(holder is UpdatedBookViewHolder){
            val item = getItem(position)

            val imageView = holder.itemView.findViewById<ImageView>(R.id.ivBook)
            val imageUrl = "https://covers.openlibrary.org/b/id/${item.bookCoverId}-L.jpg"

            Glide.with(context)
                .load(imageUrl)
                .into(imageView)

            holder.itemView.findViewById<TextView>(R.id.tvBookTitle).text = "Title: ${item.title}"
            holder.itemView.findViewById<TextView>(R.id.tvBookAuthorName).text = "Author Name: ${item.author_names.first()}"
            holder.itemView.findViewById<TextView>(R.id.tvPublishYear).text = "Publish Year: ${item.first_publish_year.toString()}"
            holder.itemView.findViewById<ImageButton>(R.id.btnAddToCart).setOnClickListener {
                onAddToCartClicked()
            }

        }
    }
}