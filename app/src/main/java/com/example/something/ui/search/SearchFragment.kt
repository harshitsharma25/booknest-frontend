package com.example.something.ui.search

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.something.ui.home.BookViewmodel
import com.example.something.R
import com.example.something.data.local.BookDatabase
import com.example.something.databinding.FragmentSearchBinding
import com.example.something.notification.createNotificationChannel
import com.example.something.repository.CartRepository
import com.example.something.ui.home.BookViewmodelFactory
import kotlinx.coroutines.launch

class SearchFragment : Fragment(){

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter : FilteredBookAdapter
//    val viewModel: BookViewmodel by activityViewModels()
    private lateinit var viewModel: BookViewmodel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup listeners, adapters etc.
        setActionBar()
        // 1. Get database instance
        val database = BookDatabase.getDatabase(requireContext())

        // 2. Get DAO
        val dao = database.BookDao()

        // 3. Create repository
        val cartRepository = CartRepository(dao)

        // 4. Create ViewModel using factory
        val factory = BookViewmodelFactory(cartRepository)
        viewModel = ViewModelProvider(this, factory)[BookViewmodel::class.java]
        viewModel.clearFilterList()

        createNotificationChannel(requireContext())
        adapter = FilteredBookAdapter(requireContext()){
            checkAndRequestNotificationPermission()
        }
        binding.rcvBookResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvBookResults.adapter = adapter
        binding.etSearch.apply {
            requestFocus()
            addTextChangedListener { text ->
                val query = text?.toString().orEmpty()
                if (query.isBlank()) {
                    viewModel.clearFilterList() // Clear list when input is empty
                } else {
                    viewModel.filterProducts(query)
                }
            }
        }



        // To show the keyboard while auto-clicked on the search bar
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)

        observeFilteredList()
    }

    private fun observeFilteredList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredBookState.collect { filteredBooks ->
                    adapter.submitList(filteredBooks)
                    binding.rcvBookResults.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun setActionBar(){
        val toolbarComponent = (activity as AppCompatActivity)
        toolbarComponent.setSupportActionBar(binding.searchToolbar)
        toolbarComponent.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.searchToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder = NotificationCompat.Builder(requireContext(), "book_channel_id")
            .setSmallIcon(R.drawable.ic_book)
            .setContentTitle("Added Successfully")
            .setContentText("New book added to the Cart.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.notify(1, builder.build())
    }





    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showNotification()
            } else {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        } else {
            showNotification()
        }
    }

}