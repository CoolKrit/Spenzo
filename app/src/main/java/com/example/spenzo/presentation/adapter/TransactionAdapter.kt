package com.example.spenzo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spenzo.R
import com.example.spenzo.data.model.Transaction
import com.example.spenzo.databinding.ItemTransactionBinding

class TransactionAdapter(private val onClick: (Transaction) -> Unit) : ListAdapter<Transaction, TransactionAdapter.ItemViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    class ItemViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            binding.transactionTitle.text = item.title
            binding.transactionCategory.text = item.category

            val colorRes = when (item.type) {
                "Income" -> R.color.income
                "Expense" -> R.color.expense
                else -> com.google.android.material.R.attr.colorOnSurface
            }

            binding.transactionAmount.setTextColor(ContextCompat.getColor(binding.transactionAmount.context, colorRes))
            val amountText = if (item.type == "Income") "+${item.amount}" else "-${item.amount}"
            binding.transactionAmount.text = amountText
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