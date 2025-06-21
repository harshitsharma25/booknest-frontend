package com.booknest.app.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.booknest.app.repository.CartRepository

class CartViewModelFactory(private val repository: CartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewmodel::class.java)) {
            return CartViewmodel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}