package com.example.spenzo.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.spenzo.Constants
import com.example.spenzo.R
import com.example.spenzo.data.model.Transaction
import com.example.spenzo.databinding.FragmentAddTransactionBinding
import com.example.spenzo.presentation.viewmodel.TransactionViewModel
import com.example.spenzo.presentation.viewmodel.TransactionViewModelFactory
import java.util.Calendar
import java.util.Date
import java.util.UUID

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(requireActivity().application)
        )[TransactionViewModel::class.java]

        initViews()
    }

    private fun initViews() {
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_autocomplete_layout,
            Constants.transactionTags
        )
        val typeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_autocomplete_layout,
            Constants.transactionType
        )

        with(binding) {
            transactionCategoryLayout.categoryInputEditText.setAdapter(categoryAdapter)
            transactionTypeLayout.typeInputEditText.setAdapter(typeAdapter)

            transactionDateLayout.dateInputEditText.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                        binding.transactionDateLayout.dateInputEditText.setText(dat)
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }

            saveTransactionButton.setOnClickListener {
                val transaction = getTransactionContent()

                when {
                    transaction.title.isEmpty() -> {
                        this.transactionTitleLayout.titleInputEditText.error =
                            "Title must not be empty"
                    }

                    transaction.amount.isNaN() -> {
                        this.transactionAmountLayout.amountInputEditText.error =
                            "Amount must not be empty"
                    }

                    transaction.type.isEmpty() -> {
                        this.transactionTypeLayout.typeInputEditText.error =
                            "Transaction type must not be empty"
                    }

                    transaction.category.isEmpty() -> {
                        this.transactionCategoryLayout.categoryInputEditText.error =
                            "Tag must not be empty"
                    }

                    transaction.date.isEmpty() -> {
                        this.transactionDateLayout.dateInputEditText.error =
                            "Date must not be empty"
                    }

                    transaction.note.isEmpty() -> {
                        this.transactionNoteLayout.noteInputEditText.error =
                            "Note must not be empty"
                    }

                    else -> {
                        transactionViewModel.addTransaction(transaction)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun getTransactionContent(): Transaction = binding.let {
        val id = ""
        val title = it.transactionTitleLayout.titleInputEditText.text.toString()
        val amount = if (it.transactionAmountLayout.amountInputEditText.text.toString()
                .isEmpty()
        ) Double.NaN else it.transactionAmountLayout.amountInputEditText.text.toString().toDouble()
        val category = it.transactionCategoryLayout.categoryInputEditText.text.toString()
        val type = it.transactionTypeLayout.typeInputEditText.text.toString()
        val date = it.transactionDateLayout.dateInputEditText.text.toString()
        val note = it.transactionNoteLayout.noteInputEditText.text.toString()

        return Transaction(id, title, amount, category, type, date, note)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}