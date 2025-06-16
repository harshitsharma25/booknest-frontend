package com.example.something.ui.home

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
import com.example.something.diffUtil.BookDiffCallback
import com.example.something.models.ListedBooks

class HomeAdapter(
    private val context : Context,
    private val onBookItemClicked: (String) -> Unit,
    private val onItemAddToCart : (ListedBooks) -> Unit
//    private var bookList : List<ListedBooks>
    ) : ListAdapter<ListedBooks, HomeAdapter.bookViewHolder>(BookDiffCallback()){

        class bookViewHolder(view: View) : RecyclerView.ViewHolder(view)

//    fun updateData(newBookList : List<ListedBooks>){
//        bookList = newBookList
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bookViewHolder {
        return bookViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book,parent,false))
    }

//    override fun getItemCount(): Int {
//        return bookList.size()
//    }


    override fun onBindViewHolder(holder: bookViewHolder, position: Int) {
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
            onBookItemClicked(imageUrl)
            onItemAddToCart(item)
        }

    }
}