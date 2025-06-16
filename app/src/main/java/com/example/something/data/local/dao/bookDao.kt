package com.example.something.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.something.data.local.entities.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * from cart_items")
    fun getAllCartItems() : Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItem(item: CartItem)

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE bookCoverId = :itemId")
    suspend fun increaseQuantity(itemId : Int)

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE bookCoverId = :itemId")
    suspend fun decreaseQuantity(itemId: Int)

    @Delete
    suspend fun deleteItem(item: CartItem)
}