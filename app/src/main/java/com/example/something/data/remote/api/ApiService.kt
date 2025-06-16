package com.example.something.data.remote.api

import com.example.something.models.Book
import retrofit2.http.GET

interface ApiService {

    @GET("people/mekBot/books/want-to-read.json")
    suspend fun getBooks() : Book
}