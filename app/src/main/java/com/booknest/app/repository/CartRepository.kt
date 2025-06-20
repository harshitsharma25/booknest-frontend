package com.booknest.app.repository

import com.booknest.app.data.local.dao.BookDao
import com.booknest.app.data.local.entities.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(private val dao : BookDao){
    val cartItems : Flow<List<CartItem>> = dao.getAllCartItems()

    suspend fun addItem(item : CartItem) = dao.insertItem(item)
    suspend fun deleteItem(item: CartItem) = dao.deleteItem(item)
    suspend fun increaseQuantity(id : Int) = dao.increaseQuantity(id)
    suspend fun decreaseQuantity(id : Int) = dao.decreaseQuantity(id)
}