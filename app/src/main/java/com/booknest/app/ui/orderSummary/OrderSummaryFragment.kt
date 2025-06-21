package com.booknest.app.ui.orderSummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.booknest.app.R
import com.booknest.app.data.local.BookDatabase
import com.booknest.app.databinding.FragmentOrdersummaryBinding
import com.booknest.app.repository.CartRepository
import com.booknest.app.ui.cart.CartUiState
import com.booknest.app.ui.cart.CartViewModelFactory
import com.booknest.app.ui.cart.CartViewmodel
import kotlinx.coroutines.launch

class OrderSummaryFragment  : Fragment(){
    private lateinit var binding : FragmentOrdersummaryBinding
    private lateinit var viewmodel: CartViewmodel
    private lateinit var mAdapter : OrderSummaryAdapter
    private lateinit var totalAmount : String
    private var ShippingAmount = 40

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersummaryBinding.inflate(inflater,container,false)

        val database = BookDatabase.getDatabase(requireContext())
        val dao = database.BookDao()
        val repository = CartRepository(dao)
        val factory = CartViewModelFactory(repository)
        viewmodel = ViewModelProvider(this,factory)[CartViewmodel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setActionBar()
        setupRecyclerView()
        observeCartData()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setActionBar(){
        val toolbarComponent = (activity as AppCompatActivity)
        toolbarComponent.setSupportActionBar(binding.orderSummaryToolbar)
        toolbarComponent.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        binding.orderSummaryToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView(){
        mAdapter = OrderSummaryAdapter(requireContext())
        binding.rcvCartItemsOverview.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeCartData(){
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewmodel.cartUiState.collect{ state ->
                    when(state){
                        is CartUiState.Data -> {
                            mAdapter.submitList(state.items)
                            totalAmount = state.items.sumOf { it.rate * it.quantity }.toString()

                            calculatePaymentDetails()
                        }
                        CartUiState.Empty -> {
                            //Toast.makeText(requireContext(),"Cart is Empty",Toast.LENGTH_SHORT).show()
                        }
                        is CartUiState.Error -> {
                            Toast.makeText(requireContext(),"Something went Wrong",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun calculatePaymentDetails(){
        val tvMRP: TextView? = view?.findViewById(R.id.tvMRP)
        val tvShipping : TextView? = view?.findViewById(R.id.tvShippingCharge)
        val tvTotalPayable : TextView? = view?.findViewById(R.id.tvTotalPayableAmount)

        tvMRP?.text = getString(R.string.total_payable,totalAmount.toInt())

        tvShipping?.text = getString(R.string.total_payable,ShippingAmount)


        val totalAmountPayable = totalAmount.toInt() + ShippingAmount
        tvTotalPayable?.text = getString(R.string.total_payable, totalAmountPayable)

        binding.tvTotalAmount.text = getString(R.string.total_payable,totalAmountPayable)
    }


}