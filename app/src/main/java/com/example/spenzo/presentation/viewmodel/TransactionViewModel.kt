package com.example.spenzo.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.spenzo.domain.repositories.TransactionRepository
import com.example.spenzo.data.db.TransactionDatabase
import com.example.spenzo.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _maxEarned = MutableLiveData<Double>()
    val maxEarned: LiveData<Double> get() = _maxEarned

    private val _maxSpent = MutableLiveData<Double>()
    val maxSpent: LiveData<Double> get() = _maxSpent

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> get() = _balance

    private val preferences = application.getSharedPreferences("transaction_prefs", Context.MODE_PRIVATE)
    private val currentIdKey = "current_id"

    init {
        val transactionDao = TransactionDatabase.getDatabase(application).transactionDao()
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseStore = FirebaseFirestore.getInstance()
        transactionRepository = TransactionRepository(transactionDao, firebaseStore, firebaseAuth)

        transactionRepository.getTransactions().observeForever { transactions ->
            _transactions.postValue(transactions)
            updateStats(transactions)
        }
    }

    fun loadUserName() {
        transactionRepository.loadUserName(
            onSuccess = { name -> _userName.postValue(name) },
            onFailure = { _userName.postValue("User") }
        )
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val newId = getNextId()
            val newTransaction = transaction.copy(id = if (transaction.id.isNullOrEmpty()) newId.toString() else transaction.id)
            transactionRepository.insertTransaction(newTransaction)
            _transactions.value = _transactions.value?.plus(newTransaction)
        }
    }

    private fun getNextId(): Int {
        val currentId = preferences.getInt(currentIdKey, 0)
        val nextId = currentId + 1
        preferences.edit().putInt(currentIdKey, nextId).apply()
        return nextId
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            _transactions.value = _transactions.value?.filter { it.id != transaction.id }
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
        }
    }

    fun loadItems(query: String, category: String?, type: String?, isOnline: Boolean) {
        if (isOnline) {
            transactionRepository.loadItemsFromServer(query, category, type,
                onSuccess = { items ->
                    _transactions.postValue(items)
                    updateStats(items)
                },
                onFailure = {
                    loadItemsLocally(query, category, type)
                }
            )
        } else {
            loadItemsLocally(query, category, type)
        }
    }

    private fun loadItemsLocally(query: String, category: String?, type: String?) {
        transactionRepository.getTransactions().observeForever { itemsList ->
            val filteredItems = itemsList.filter { transaction ->
                val matchesQuery = query.isEmpty() || transaction.title.contains(query, ignoreCase = true)
                val matchesCategory = category.isNullOrEmpty() || transaction.category == category
                val matchesType = type.isNullOrEmpty() || transaction.type == type
                matchesQuery && matchesCategory && matchesType
            }
            _transactions.postValue(filteredItems)
            updateStats(filteredItems)
        }
    }

    private fun updateStats(items: List<Transaction>) {
        val maxEarned = items.filter { it.type == "Income" }.sumOf { it.amount }
        val maxSpent = items.filter { it.type == "Expense" }.sumOf { it.amount }
        val balance = maxEarned - maxSpent

        _maxEarned.postValue(maxEarned)
        _maxSpent.postValue(maxSpent)
        _balance.postValue(balance)
    }
}