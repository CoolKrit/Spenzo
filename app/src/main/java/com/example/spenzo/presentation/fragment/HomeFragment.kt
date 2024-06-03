package com.example.spenzo.presentation.fragment

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spenzo.R
import com.example.spenzo.data.model.Transaction
import com.example.spenzo.databinding.FragmentHomeBinding
import com.example.spenzo.presentation.activity.MainActivity.Companion.CURRENCY
import com.example.spenzo.presentation.activity.MainActivity.Companion.SETTINGS
import com.example.spenzo.presentation.adapter.TransactionAdapter
import com.example.spenzo.presentation.viewmodel.TransactionViewModel
import com.example.spenzo.presentation.viewmodel.TransactionViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
        sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == CURRENCY) {
                transactionAdapter.notifyDataSetChanged()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        setupRecyclerView()
        setupObservers()
        setupSpinners()
        setupSearchView()
        setupClearDatabaseButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        _binding = null
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToTransactionDetailsFragment(transaction)
            findNavController().navigate(action)
        }
        binding.transactionRecyclerView.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val transaction = transactionAdapter.currentList[position]
                    transactionViewModel.deleteTransaction(transaction)

                    Snackbar.make(binding.root, "Transaction deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            transactionViewModel.addTransaction(transaction)
                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.transactionRecyclerView)
    }

    private fun setupObservers() {
        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(requireActivity().application)
        )[TransactionViewModel::class.java]

        transactionViewModel.transactions.observe(viewLifecycleOwner) { items ->
            updateRecyclerView(items)
        }

        transactionViewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.userNameTextView.text = "Hi, $name!"
        }

        transactionViewModel.loadUserName()
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
        setupSpinner(binding.categorySpinner, R.array.spinner_transactionCategories)
        setupSpinner(binding.typeSpinner, R.array.spinner_transactionTypes)
    }

    private fun setupSpinner(spinner: Spinner, @ArrayRes itemsArrayRes: Int) {
        ArrayAdapter.createFromResource(
            requireContext(),
            itemsArrayRes,
            R.layout.item_spinner_layout
        ).also { adapter ->
            adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                loadItemsFromSpinnersAndSearchView()
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

        transactionViewModel.loadItems(query, category, type, isOnline(requireContext()))
    }

    private fun updateRecyclerView(items: List<Transaction>) {
        binding.emptyStateLayout.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        transactionAdapter.submitList(items)
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun setupClearDatabaseButton() {
        binding.deleteTransactions.setOnClickListener {
            transactionViewModel.deleteAllTransactions()
        }
    }
}