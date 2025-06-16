package com.example.something.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.something.R
import com.example.something.data.local.BookDatabase
import com.example.something.data.local.entities.CartItem
import com.example.something.databinding.FragmentCartBinding
import com.example.something.repository.BookRepository
import com.example.something.repository.CartRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var viewmodel: CartViewmodel
    private lateinit var mAdapter : CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)

        val database = BookDatabase.getDatabase(requireContext())
        val dao = database.BookDao()
        val repository = CartRepository(dao)
        val factory = CartViewModelFactory(repository)
        viewmodel = ViewModelProvider(this,factory)[CartViewmodel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProceedToBuy.setOnClickListener {
            proceedToCheckout()
        }
        setupRecyclerView()
        observeCart()
    }

    fun observeCart(){
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewmodel.cartUiState.collect{ state ->
                    when(state){
                        is CartUiState.Data -> {
                            mAdapter.submitList(state.items)
                            binding.rcvCartItems.visibility = View.VISIBLE
                            binding.llCartContainer.visibility = View.GONE
                            binding.flCartItems.visibility = View.VISIBLE
                            binding.bottomCard.visibility = View.VISIBLE

                            // Calculate and display total amount
                            val totalAmount = state.items.sumOf { it.quantity * it.rate }
                            binding.tvTotalAmount.text = "₹$totalAmount"
                        }
                        CartUiState.Empty -> {
                            binding.rcvCartItems.visibility = View.GONE
                            binding.bottomCard.visibility = View.GONE
                            binding.llCartContainer.visibility = View.VISIBLE
                        }
                        is CartUiState.Error -> {
                            Toast.makeText(requireContext(), "Something went wrong: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun proceedToCheckout(){
        findNavController().navigate(R.id.action_cartFragment_to_orderSummaryFragment)
    }

    fun setupRecyclerView(){
        mAdapter = CartAdapter(
            requireContext(),
            increaseQuantityOfProduct = { bookId ->
                increaseQuantity(bookId)
            },
            decreaseQuantityOfProduct = { bookId ->
                decreaseQuantity(bookId)
            },
            deleteProduct = { item ->
                deleteProduct(item)
            }
        )
        binding.rcvCartItems.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun increaseQuantity(bookId : Int){
       viewmodel.increaseQuantity(bookId)
    }

    fun decreaseQuantity(bookId: Int){
        viewmodel.decreaseQuantity(bookId)
    }

    fun deleteProduct(item : CartItem){
        viewmodel.deleteItem(item)
    }

}