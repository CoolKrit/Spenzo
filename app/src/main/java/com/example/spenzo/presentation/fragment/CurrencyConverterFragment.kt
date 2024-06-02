package com.example.spenzo.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.spenzo.databinding.FragmentCurrencyConverterBinding
import com.example.spenzo.presentation.viewmodel.CurrencyViewModel
import com.example.spenzo.presentation.viewmodel.TransactionViewModel

class CurrencyConverterFragment : Fragment() {

    private var _binding: FragmentCurrencyConverterBinding? = null
    private val binding get() = _binding!!

    private lateinit var currencyViewModel: CurrencyViewModel
    private var currencyRates: Map<String, Double> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencyViewModel = ViewModelProvider(requireActivity())[CurrencyViewModel::class.java]
        currencyViewModel.currencyRates.observe(viewLifecycleOwner) { rates ->
            currencyRates = rates
        }
        setupCurrencyConverter()
    }

    private fun setupCurrencyConverter() {
        val currencies = arrayOf("USD", "RUB", "EUR")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.currencySpinner1.adapter = adapter
        binding.currencySpinner2.adapter = adapter

        binding.convertButton.setOnClickListener {
            val amount = binding.amountEditText1.text.toString().toDoubleOrNull()
            if (amount != null) {
                val fromCurrency = binding.currencySpinner1.selectedItem as String
                val toCurrency = binding.currencySpinner2.selectedItem as String
                convertCurrency(amount, fromCurrency, toCurrency)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }

        binding.currencySpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCurrency = currencies[position]
                currencyViewModel.fetchCurrencyRates(selectedCurrency)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String) {
        val fromRate = currencyRates[fromCurrency] ?: 1.0
        val toRate = currencyRates[toCurrency] ?: 1.0
        val convertedAmount = amount * (toRate / fromRate)
        binding.amountEditText2.setText(String.format("%.2f", convertedAmount))
    }
}