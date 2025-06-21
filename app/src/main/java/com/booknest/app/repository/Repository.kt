package com.booknest.app.repository

import com.booknest.app.models.Book
import com.booknest.app.data.remote.network.ApiClient

class BookRepository {

    suspend fun fetchBooks() : Book {
        println("Result is: in Repository")
        val result =  ApiClient.apiService.getBooks()
        println("Result is: $result")
        return result
    }


}