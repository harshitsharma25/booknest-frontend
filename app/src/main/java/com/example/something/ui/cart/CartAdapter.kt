package com.example.something.ui.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.something.R
import com.example.something.data.local.entities.CartItem
import com.example.something.diffUtil.CartDiffCallback
import org.w3c.dom.Text
import kotlin.time.times

class CartAdapter(
    private val context : Context,
    private val increaseQuantityOfProduct : (Int) -> Unit,
    private val decreaseQuantityOfProduct : (Int) -> Unit,
    private val deleteProduct : (CartItem) -> Unit) :
    ListAdapter<CartItem,CartAdapter.CartViewHolder>(CartDiffCallback()) {

    class CartViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false))
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = getItem(position)
        val imageView = holder.itemView.findViewById<ImageView>(R.id.ivCartBook)

        val imageUrl = "https://covers.openlibrary.org/b/id/${item.bookCoverId}-L.jpg"

        Glide.with(context)
            .load(imageUrl)
            .into(imageView)

        holder.itemView.findViewById<TextView>(R.id.tvCartBookTitle).text = item.title
        holder.itemView.findViewById<TextView>(R.id.tvCartBookAuthorName).text = item.author_names.first()
        holder.itemView.findViewById<TextView>(R.id.tvCartPublishYear).text = item.first_publish_year.toString()

        val cartQuantityTextView = holder.itemView.findViewById<TextView>(R.id.tvCartQuantity)
        cartQuantityTextView.text = item.quantity.toString()


        val ibSubtractOrDelete = holder.itemView.findViewById<ImageButton>(R.id.ibSubtractOrDelete)
        val ibAddProduct = holder.itemView.findViewById<ImageButton>(R.id.ibAdd)

        val cartQuantity = item.quantity

        if(cartQuantity == 1){
            ibSubtractOrDelete.apply {
                setImageResource(R.drawable.ic_delete)
                setOnClickListener {
                    deleteProduct(item)
                }
            }

        } else if(cartQuantity > 1){

            ibSubtractOrDelete.apply {
                setImageResource(R.drawable.ic_subtract)
                setOnClickListener {
                    decreaseQuantityOfProduct(item.bookCoverId)
                }
            }
        }

        val bookRate = item.rate
        val booksPrice = cartQuantity * bookRate

        holder.itemView.findViewById<TextView>(R.id.tvBookAmount).text = "₹$booksPrice"

        holder.itemView.findViewById<ImageButton>(R.id.btnDeleteItem).setOnClickListener {
            deleteProduct(item)
        }

        ibAddProduct.setOnClickListener {
            increaseQuantityOfProduct(item.bookCoverId)
        }
    }

}