package com.booknest.app.ui.orderSummary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.booknest.app.R
import com.booknest.app.data.local.entities.CartItem
import com.booknest.app.diffUtil.OrderSummaryDiffUtil

class OrderSummaryAdapter(
    private val context : Context
): ListAdapter<CartItem,OrderSummaryAdapter.OrderSummaryViewholder>(
    OrderSummaryDiffUtil()
) {
    class OrderSummaryViewholder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewholder {
        return OrderSummaryViewholder(LayoutInflater.from(context).inflate(R.layout.item_cart_overview,parent,false))
    }

    override fun onBindViewHolder(holder: OrderSummaryViewholder, position: Int) {
        val item = getItem(position)

        val imageUrl = "https://covers.openlibrary.org/b/id/${item.bookCoverId}-L.jpg"
        val imageView = holder.itemView.findViewById<ImageView>(R.id.ivCartBookOverview)
        Glide.with(context)
            .load(imageUrl)
            .into(imageView)

        holder.itemView.findViewById<TextView>(R.id.tvCartBookTitle).text = item.title
        holder.itemView.findViewById<TextView>(R.id.tvCartBookAuthorName).text = item.author_names.first()
        holder.itemView.findViewById<TextView>(R.id.tvCartPublishYear).text = item.first_publish_year.toString()
        holder.itemView.findViewById<TextView>(R.id.tvBookQuantity).text = "Quantity: ${item.quantity}"
        holder.itemView.findViewById<TextView>(R.id.tvItemPrice).text = ((item.rate) * (item.quantity)).toString()
    }
}