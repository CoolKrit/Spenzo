package com.example.spenzo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spenzo.R
import com.example.spenzo.data.model.Transaction
import com.example.spenzo.databinding.TransactionItemBinding

class TransactionRVAdapter(private val items: List<Transaction>) : RecyclerView.Adapter<TransactionRVAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(private val binding: TransactionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            binding.transactionTitle.text = item.title
            binding.transactionCategory.text = item.category

            when (item.type) {
                "Income" -> {
                    binding.transactionAmount.setTextColor(
                        ContextCompat.getColor(
                            binding.transactionAmount.context,
                            R.color.income
                        )
                    )

                    binding.transactionAmount.text =
                        "+".plus(item.amount)
                }

                "Expense" -> {
                    binding.transactionAmount.setTextColor(
                        ContextCompat.getColor(
                            binding.transactionAmount.context,
                            R.color.expense
                        )
                    )

                    binding.transactionAmount.text =
                        "-".plus(item.amount)
                }
            }
        }
    }
}