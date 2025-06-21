package com.booknest.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.booknest.app.repository.CartRepository

class BookViewmodelFactory(
    private val cartRepository: CartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewmodel::class.java)) {
            return BookViewmodel(cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
