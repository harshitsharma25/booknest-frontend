package com.example.something.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.something.R
import com.example.something.data.local.BookDatabase
import com.example.something.data.local.entities.CartItem
import com.example.something.databinding.FragmentHomeBinding
import com.example.something.models.ListedBooks
import com.example.something.repository.CartRepository
import com.example.something.utils.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
//    private val bookViewmodel: BookViewmodel by activityViewModels()
    private lateinit var viewModel: BookViewmodel
    private lateinit var mAdapter: HomeAdapter
    private lateinit var navController : NavController
    private lateinit var listedBooks : List<ListedBooks>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        // 1. Get database instance
        val database = BookDatabase.getDatabase(requireContext())

        // 2. Get DAO
        val dao = database.BookDao()

        // 3. Create repository
        val cartRepository = CartRepository(dao)

        // 4. Create ViewModel using factory
        val factory = BookViewmodelFactory(cartRepository)
        viewModel = ViewModelProvider(this, factory)[BookViewmodel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.etTmpSearch.setOnClickListener{
            showSearchFragment()

            binding.fragmentContainer.visibility = View.VISIBLE
            binding.rcvBooks.visibility = View.GONE
            binding.myAppBarLayout.visibility = View.GONE

        }

        setupRecyclerView()
        observeList()
    }


    private fun showSearchFragment() {
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
    }

    private fun observeList() {
        println("Result is: In observeList")
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is BookUiState.Loading -> showLoading()
                        is BookUiState.BookData -> {
                            binding.pbBooks.visibility = View.GONE
                            binding.rcvBooks.visibility = View.VISIBLE
                            mAdapter.submitList(state.books)
                            listedBooks = state.books
                        }
                        BookUiState.Empty -> showEmptyState()
                        is BookUiState.Error -> showError(state.message)
                        is BookUiState.NoNetwork -> showNoNetwork()
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        mAdapter = HomeAdapter(requireContext(),
            onBookItemClicked = { bookCoverImage ->
                checkAndRequestNotificationPermission(bookCoverImage)
            },
            onItemAddToCart = {item ->
                addItemToCart(item)
            }
        )
        binding.rcvBooks.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(SpaceItemDecoration(26))
        }
    }

    fun addItemToCart(item : ListedBooks){
        val cartItem = CartItem(
            quantity = 1,
            title = item.title,
            author_names = item.author_names,
            bookCoverId = item.bookCoverId,
            first_publish_year = item.first_publish_year,
            rate = Random.nextInt(100,151)
        )
        viewModel.addItemToCart(cartItem)
    }

    private fun checkAndRequestNotificationPermission(bookCoverImage : String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED){
                //todo: apply the condition to check book is actually saved in cart or not , if yes than show notification
                showNotification(bookCoverImage)
            }
            else{
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
        else{
            //todo: apply the condition to check book is actually saved in cart or not , if yes than show notification
            showNotification(bookCoverImage)
        }
    }

    private fun showNotification(bookCoverImage: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Start coroutine from activity
        lifecycleScope.launch {
            // Load bitmap on IO thread
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(bookCoverImage)
                        .submit()
                        .get()
                } catch (e: Exception) {
                    null
                }
            }

            if (bitmap != null) {
                val bigPictureStyle = NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap as Bitmap?)
                    .bigLargeIcon(null as Bitmap?)

                val builder = NotificationCompat.Builder(requireContext(), "book_channel_id")
                    .setSmallIcon(R.drawable.ic_book)
                    .setContentTitle("Book Added to Cart")
                    .setContentText("You added a book to your cart.")
                    .setStyle(bigPictureStyle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                NotificationManagerCompat.from(requireContext()).notify(3, builder.build())
            }
        }
    }



    private fun showLoading() {
        binding.pbBooks.visibility = View.VISIBLE
        binding.rcvBooks.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.pbBooks.visibility = View.GONE
        binding.rcvBooks.visibility = View.GONE
    }

    private fun showError(message: String?) {
        binding.pbBooks.visibility = View.GONE
        binding.rcvBooks.visibility = View.GONE
        Toast.makeText(requireContext(), message ?: "An error occurred", Toast.LENGTH_SHORT).show()
    }

    private fun showNoNetwork() {
        binding.pbBooks.visibility = View.GONE
        binding.rcvBooks.visibility = View.GONE
        Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
    }
}