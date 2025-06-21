package com.booknest.app.data.remote.api

import com.booknest.app.models.Book
import retrofit2.http.GET

interface ApiService {

    @GET("people/mekBot/books/want-to-read.json")
    suspend fun getBooks() : Book
}