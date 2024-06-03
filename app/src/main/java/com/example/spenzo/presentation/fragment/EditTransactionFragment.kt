package com.example.spenzo.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.spenzo.Constants
import com.example.spenzo.R
import com.example.spenzo.data.model.Transaction
import com.example.spenzo.databinding.FragmentEditTransactionBinding
import com.example.spenzo.presentation.viewmodel.TransactionViewModel
import com.example.spenzo.presentation.viewmodel.TransactionViewModelFactory
import java.util.Calendar

class EditTransactionFragment : Fragment() {

    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var transaction: Transaction

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(requireActivity().application)
        )[TransactionViewModel::class.java]

        transaction = arguments?.let {
            EditTransactionFragmentArgs.fromBundle(it).transaction
        } ?: throw IllegalArgumentException("Transaction must be passed to EditTransactionFragment")

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
            transactionTitleLayout.transactionTitleLayout.hint = transaction.title
            transactionAmountLayout.transactionAmountLayout.hint = transaction.amount.toString()
            transactionCategoryLayout.transactionCategoryLayout.hint = transaction.category
            transactionTypeLayout.transactionTypeLayout.hint = transaction.type
            transactionDateLayout.transactionDateLayout.hint = transaction.date
            transactionNoteLayout.transactionNoteLayout.hint = transaction.note

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
                        val date = "$dayOfMonth/${monthOfYear + 1}/$year"
                        transactionDateLayout.dateInputEditText.setText(date)
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }

            saveTransactionButton.setOnClickListener {
                val updatedTransaction = getTransactionContent()

                when {
                    updatedTransaction.title.isEmpty() -> {
                        this.transactionTitleLayout.titleInputEditText.error =
                            "Title must not be empty"
                    }

                    updatedTransaction.amount.isNaN() -> {
                        this.transactionAmountLayout.amountInputEditText.error =
                            "Amount must not be empty"
                    }

                    updatedTransaction.type.isEmpty() -> {
                        this.transactionTypeLayout.typeInputEditText.error =
                            "Transaction type must not be empty"
                    }

                    updatedTransaction.category.isEmpty() -> {
                        this.transactionCategoryLayout.categoryInputEditText.error =
                            "Tag must not be empty"
                    }

                    updatedTransaction.date.isEmpty() -> {
                        this.transactionDateLayout.dateInputEditText.error =
                            "Date must not be empty"
                    }

                    updatedTransaction.note.isEmpty() -> {
                        this.transactionNoteLayout.noteInputEditText.error =
                            "Note must not be empty"
                    }

                    else -> {
                        transactionViewModel.updateTransaction(updatedTransaction)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun getTransactionContent(): Transaction = binding.let {
        val id = transaction.id
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