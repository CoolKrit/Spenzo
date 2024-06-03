package com.example.spenzo.presentation.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.spenzo.Constants
import com.example.spenzo.R
import com.example.spenzo.data.model.Transaction
import com.example.spenzo.presentation.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ExpensesAnalysisFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses_analysis, container, false)
        pieChart = view.findViewById(R.id.pieChart)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionViewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        transactionViewModel.loadItems("", null, null, isOnline(requireContext()))
        transactionViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            val incomeTransactions = transactions.filter { it.type == "Expense" }
            setupPieChart(incomeTransactions)
        }
    }

    private fun setupPieChart(transactions: List<Transaction>) {
        val groupedTransactions = transactions.groupBy { it.category }
        val entries = groupedTransactions.map { (category, transactions) ->
            val totalAmount = transactions.sumOf { it.amount }.toFloat()
            PieEntry(totalAmount, category)
        }

        val dataSet = PieDataSet(entries, "Expenses")
        val colors = transactions.map { Constants.getColor(it.category) }
        dataSet.colors = colors

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}