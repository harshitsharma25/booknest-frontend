package com.example.something.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val bookCoverId : Int,
    val title : String,
    val author_names : List<String>,
    val first_publish_year : Int,
    var quantity : Int = 0,
    val rate : Int
)