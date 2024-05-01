package com.example.spenzo.presentation.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spenzo.R
import com.example.spenzo.data.model.Transaction
import com.example.spenzo.databinding.FragmentHomeBinding
import com.example.spenzo.presentation.adapter.TransactionRVAdapter
import com.example.spenzo.presentation.viewmodel.TransactionViewModel
import com.example.spenzo.presentation.viewmodel.TransactionViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var transactionRVAdapter: TransactionRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
        setupSpinners()
        setupSearchView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        transactionRVAdapter = TransactionRVAdapter(emptyList())
        binding.transactionRecyclerView.adapter = transactionRVAdapter
        binding.transactionRecyclerView.setHasFixedSize(true)
    }

    private fun setupViewModel() {
        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(requireContext())
        )[TransactionViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        loadItems("", null, null)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadItemsFromSpinnersAndSearchView()
                return true
            }
        })
    }

    private fun setupSpinners() {
        setupSpinner(
            binding.categorySpinner,
            R.array.spinner_transactionCategories
        ) { loadItemsFromSpinnersAndSearchView() }
        setupSpinner(
            binding.typeSpinner,
            R.array.spinner_transactionTypes
        ) { loadItemsFromSpinnersAndSearchView() }
    }

    private fun setupSpinner(
        spinner: Spinner,
        @ArrayRes itemsArrayRes: Int,
        onItemSelectedListener: (String?) -> Unit,
    ) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            itemsArrayRes,
            R.layout.spinner_item_layout
        )
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                onItemSelectedListener(parent?.getItemAtPosition(position) as? String)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadItemsFromSpinnersAndSearchView() {
        val category =
            if (binding.categorySpinner.selectedItem as? String == "All") null else binding.categorySpinner.selectedItem as? String
        val type =
            if (binding.typeSpinner.selectedItem as? String == "All") null else binding.typeSpinner.selectedItem as? String
        val query = binding.searchView.query.toString()

        if (isOnline(requireContext())) {
            loadItems(query, category, type)
        } else {
            val filteredItems = transactionViewModel.loadItemsLocally(query, category, type)
            updateRecyclerView(filteredItems)
        }
    }

    private fun loadItems(query: String, category: String?, type: String?) {
        transactionViewModel.getItems(query, category, type,
            onSuccess = { items ->
                updateRecyclerView(items)
            },
            onError = { exception ->
                // Обработка ошибки
            }
        )
    }

    private fun updateRecyclerView(items: List<Transaction>) {
        if (items.isEmpty()) binding.emptyStateLayout.visibility =
            View.VISIBLE else binding.emptyStateLayout.visibility = View.GONE
        transactionRVAdapter = TransactionRVAdapter(items)
        binding.transactionRecyclerView.adapter = transactionRVAdapter
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}