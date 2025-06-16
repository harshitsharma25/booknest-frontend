package com.example.something.models

data class ListedBooks(
    val title : String,
    val author_names : List<String>,
    val first_publish_year : Int,
    val bookCoverId : Int
)
