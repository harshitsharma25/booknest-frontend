package com.booknest.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booknest.app.data.local.entities.CartItem
import com.booknest.app.models.ListedBooks
import com.booknest.app.models.Logs
import com.booknest.app.data.remote.network.HTTPStatus
import com.booknest.app.data.remote.api.NoNetworkException
import com.booknest.app.data.remote.api.onApiError
import com.booknest.app.data.remote.api.onException
import com.booknest.app.data.remote.api.onSuccess
import com.booknest.app.data.remote.api.safeApiCall
import com.booknest.app.repository.BookRepository
import com.booknest.app.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Managing all the states
sealed class BookUiState{
    object Loading : BookUiState()
    object Empty :   BookUiState()
    data class BookData(val books : List<ListedBooks>) : BookUiState()
    data class Error(val code : Int, val message : String?) : BookUiState()
    data class NoNetwork(val prevState : BookUiState) : BookUiState()
}

class BookViewmodel(private val cartRepository: CartRepository) : ViewModel() {

    private var _uiState : MutableStateFlow<BookUiState> = MutableStateFlow(BookUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var _filteredBookState : MutableStateFlow<List<ListedBooks>> = MutableStateFlow(emptyList())
    val filteredBookState = _filteredBookState.asStateFlow()

    val repository = BookRepository()

    init {
        fetchBooks()
    }


    fun fetchBooks(){
        viewModelScope.launch {
            safeApiCall {
                repository.fetchBooks()
            }.onSuccess { body ->
                val books = body.reading_log_entries.map { it.toBookDetail() }
                if (books.isEmpty()) {
                    _uiState.value = BookUiState.Empty
                } else {
                    _uiState.value = BookUiState.BookData(books)
                    //_filteredBookState.value = books // set filtered to full list initially
                }
            }.onApiError{ code, message ->
                _uiState.value = BookUiState.Error(
                    code,
                    message ?: "Something went wrong. Please try again later."
                )
            } .onException { e ->
                when (e) {
                    is NoNetworkException -> {
                        _uiState.value = BookUiState.NoNetwork(
                            _uiState.value
                        )
                    }
                    else -> {
                        _uiState.value = BookUiState.Error(
                            HTTPStatus.UNEXPECTED_RESPONSE.code,
                            HTTPStatus.UNEXPECTED_RESPONSE.message
                        )
                    }
                }
            }
        }
    }


    fun filterProducts(query : String){
        val currentState = _uiState.value
        if(currentState is BookUiState.BookData){
            val filtered = currentState.books.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.author_names.any{ author ->
                            author.contains(query, ignoreCase = true)
                }
            }
            println("filtered list -----> $filtered")
            _filteredBookState.value = filtered
        }
    }

    fun clearFilterList(){
        _filteredBookState.value = emptyList()
    }

    fun addItemToCart(item: CartItem){
            viewModelScope.launch {
                try {
                    cartRepository.addItem(item)
                } catch (e :Exception){
                    println(e.message ?: "Item couldn't be Inserted in the Cart")
                }
            }
    }
}


fun Logs.toBookDetail() : ListedBooks {
    return ListedBooks(
        title = this.work.title,
        author_names = this.work.author_names,
        first_publish_year = this.work.first_publish_year,
        bookCoverId = this.work.cover_id
    )
}