package com.example.spenzo.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.spenzo.databinding.FragmentTransactionDetailsBinding

class TransactionDetailsFragment : Fragment() {

    private var _binding: FragmentTransactionDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transaction = arguments?.let {
            TransactionDetailsFragmentArgs.fromBundle(it).transaction
        }

        transaction?.let {
            binding.transactionTitle.text = it.title
            binding.transactionAmount.text = it.amount.toString()
            binding.transactionCategory.text = it.category
            binding.transactionType.text = it.type
            binding.transactionDate.text = it.date
            binding.transactionNote.text = it.note


            binding.transactionEditButton.setOnClickListener { _ ->
                val action = TransactionDetailsFragmentDirections.actionTransactionDetailsFragmentToEditTransactionFragment(it)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}