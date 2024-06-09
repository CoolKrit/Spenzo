package com.example.spenzo.presentation.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spenzo.R
import com.example.spenzo.data.model.Transaction
import com.example.spenzo.databinding.ItemTransactionBinding
import com.example.spenzo.presentation.activity.MainActivity
import com.example.spenzo.presentation.activity.MainActivity.Companion.CURRENCY
import com.example.spenzo.presentation.activity.MainActivity.Companion.SETTINGS
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(private val onClick: (Transaction) -> Unit) :
    ListAdapter<Transaction, TransactionAdapter.ItemViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    inner class ItemViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            binding.transactionTitle.text = item.title
            binding.transactionCategory.text = item.category

            val context = binding.transactionAmount.context
            val sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
            val currency = when (sharedPreferences.getString(CURRENCY, "USD")) {
                "USD" -> "$"
                "EUR" -> "€"
                "RUB" -> "₽"
                else -> "$"
            }

            val colorRes = when (item.type) {
                "Income" -> R.color.income
                "Expense" -> R.color.expense
                else -> com.google.android.material.R.attr.colorOnSurface
            }

            when (item.category) {
                "Housing" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_food)
                }

                "Transportation" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_transport)
                }

                "Food" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_food)
                }

                "Utilities" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_utilities)
                }

                "Insurance" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_insurance)
                }

                "Healthcare" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_medical)
                }

                "Saving & Debts" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_savings)
                }

                "Personal Spending" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_personal_spending)
                }

                "Entertainment" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_entertainment)
                }

                "Miscellaneous" -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_other)
                }

                else -> {
                    binding.transactionIconView.setImageResource(R.drawable.ic_other)
                }
            }

            binding.transactionAmount.setTextColor(
                ContextCompat.getColor(
                    binding.transactionAmount.context,
                    colorRes
                )
            )
            val formattedAmount = formatNumber(item.amount)
            val amountText =
                if (item.type == "Income") "+$currency${formattedAmount}" else "-$currency${formattedAmount}"
            binding.transactionAmount.text = amountText
        }

        private fun formatNumber(number: Double): String {
            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
            return numberFormat.format(number)
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}