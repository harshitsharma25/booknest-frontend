package com.example.something.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.something.data.local.entities.CartItem
import com.example.something.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


sealed class CartUiState {
    data class Data(val items: List<CartItem>) : CartUiState()
    data class Error(val message: String) : CartUiState()
    object Empty : CartUiState()
}

class CartViewmodel(private val repository: CartRepository): ViewModel() {
    private val _cartUiState = MutableStateFlow<CartUiState>(CartUiState.Empty)
    val cartUiState = _cartUiState.asStateFlow()

    init {
        getCartItems()
    }

    private fun getCartItems(){
        viewModelScope.launch {
            repository.cartItems
                .catch { e ->
                    _cartUiState.value = CartUiState.Error(e.message ?: "Unknown Error")
                }
                .collect{ items ->
                    _cartUiState.value = if(items.isNotEmpty()){
                        CartUiState.Data(items)
                    } else{
                        CartUiState.Empty
                    }
                }
        }
    }

    fun addItem(item: CartItem){
        viewModelScope.launch {
            try {
                repository.addItem(item)
            } catch (e: Exception){
                _cartUiState.value = CartUiState.Error(e.message ?: "Failed to Add Item")
            }
        }
    }

    fun increaseQuantity(id : Int){
        viewModelScope.launch {
            try {
                repository.increaseQuantity(id)
            } catch (e: Exception){
                _cartUiState.value = CartUiState.Error(e.message ?: "Failed to Increase Quantity")
            }
        }
    }

    fun decreaseQuantity(id : Int){
        viewModelScope.launch {
            try {
                repository.decreaseQuantity(id)
            } catch (e: Exception){
                _cartUiState.value = CartUiState.Error(e.message ?: "Failed to Increase Quantity")
            }
        }
    }

    fun deleteItem(item: CartItem){
        viewModelScope.launch {
            try {
                repository.deleteItem(item)
            } catch (e: Exception){
                _cartUiState.value = CartUiState.Error(e.message ?: "Failed to Delete Item")
            }
        }
    }
}