package com.example.something.repository

import com.example.something.models.Book
import com.example.something.data.remote.network.ApiClient

class BookRepository {

    suspend fun fetchBooks() : Book {
        println("Result is: in Repository")
        val result =  ApiClient.apiService.getBooks()
        println("Result is: $result")
        return result
    }


}