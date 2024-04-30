package com.example.spenzo.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spenzo.databinding.FragmentHomeBinding
import com.example.spenzo.presentation.adapter.TransactionRVAdapter
import com.example.spenzo.presentation.viewmodel.TransactionViewModel

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

        transactionRVAdapter = TransactionRVAdapter(emptyList())
        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transactionRecyclerView.adapter = transactionRVAdapter

        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        observeViewModel()

        setupSearchView()
        setupCategorySpinner()
        setupTypeSpinner()
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

    private fun observeViewModel() {
        loadItems("", null, null)
    }

    private fun loadItems(query: String, category: String?, type: String?) {
        transactionViewModel.getItems(query, category, type,
            onSuccess = { items ->
                transactionRVAdapter = TransactionRVAdapter(items)
                binding.transactionRecyclerView.adapter = transactionRVAdapter
            },
            onError = { exception ->
                // Обработка ошибки
            }
        )
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val category =
                    if (binding.categorySpinner.selectedItem as String? == "All") null else binding.categorySpinner.selectedItem as String?
                val type =
                    if (binding.typeSpinner.selectedItem as String? == "All") null else binding.typeSpinner.selectedItem as String?
                loadItems(newText.orEmpty(), category, type)
                return true
            }
        })
    }

    private fun setupCategorySpinner() {
        binding.categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    val category =
                        if (parent?.getItemAtPosition(position) as String? == "All") null else parent?.getItemAtPosition(
                            position
                        ) as String?
                    val type =
                        if (binding.typeSpinner.selectedItem as String? == "All") null else binding.typeSpinner.selectedItem as String?
                    loadItems(binding.searchView.query.toString(), category, type)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    val type =
                        if (binding.typeSpinner.selectedItem as String? == "All") null else binding.typeSpinner.selectedItem as String?
                    loadItems(binding.searchView.query.toString(), null, type)
                }
            }
    }

    private fun setupTypeSpinner() {
        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val category =
                    if (binding.categorySpinner.selectedItem as String? == "All") null else binding.categorySpinner.selectedItem as String?
                val type =
                    if (parent?.getItemAtPosition(position) as String? == "All") null else parent?.getItemAtPosition(
                        position
                    ) as String?
                loadItems(binding.searchView.query.toString(), category, type)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                val category =
                    if (binding.categorySpinner.selectedItem as String? == "All") null else binding.categorySpinner.selectedItem as String?
                loadItems(binding.searchView.query.toString(), category, null)
            }
        }
    }
}