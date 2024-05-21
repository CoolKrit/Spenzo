package com.example.spenzo.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spenzo.data.db.TransactionDatabase
import com.example.spenzo.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDao = TransactionDatabase.getDatabase(application).transactionDao()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStore = FirebaseFirestore.getInstance()

    private val itemsCollection = firebaseStore.collection("users")
        .document(firebaseAuth.currentUser!!.uid)
        .collection("transactions")

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
            saveToFirestore(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            Log.e("TAG", "updateTransaction: HERE!")
            updateInFirestore(transaction)
            transactionDao.updateTransaction(transaction)
        }
    }

    private fun updateInFirestore(transaction: Transaction) {
        Log.e("TAG", "updateInFirestore: HERE!")
        itemsCollection.document(transaction.id).set(transaction)
    }

    private fun saveToFirestore(transaction: Transaction) {
        itemsCollection.document(transaction.id).set(transaction)
    }

    fun loadItems(query: String, category: String?, type: String?, isOnline: Boolean) {
        if (isOnline) {
            fetchItemsFromServer(query, category, type)
        } else {
            loadItemsLocally(query, category, type)
        }
    }

    private fun fetchItemsFromServer(query: String, category: String?, type: String?) {
        var queryRef: Query = itemsCollection

        if (query.isNotEmpty()) queryRef = queryRef.whereEqualTo("title", query)
        if (!category.isNullOrEmpty()) queryRef = queryRef.whereEqualTo("category", category)
        if (!type.isNullOrEmpty()) queryRef = queryRef.whereEqualTo("type", type)

        queryRef.orderBy("id", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { querySnapshot ->
                val itemsList =
                    querySnapshot.documents.mapNotNull { it.toObject(Transaction::class.java) }
                saveItemsLocally(itemsList)
                _transactions.postValue(itemsList)
            }
            .addOnFailureListener {
                loadItemsLocally(query, category, type)
            }
    }

    private fun saveItemsLocally(items: List<Transaction>) {
        viewModelScope.launch {
            items.forEach { transactionDao.insertTransaction(it) }
        }
    }

    private fun loadItemsLocally(query: String, category: String?, type: String?) {
        transactionDao.getAllTransactions().observeForever { itemsList ->
            val filteredItems = itemsList.filter { transaction ->
                val matchesQuery =
                    query.isEmpty() || transaction.title.contains(query, ignoreCase = true)
                val matchesCategory = category.isNullOrEmpty() || transaction.category == category
                val matchesType = type.isNullOrEmpty() || transaction.type == type
                matchesQuery && matchesCategory && matchesType
            }
            _transactions.postValue(filteredItems)
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            transactionDao.deleteAllTransactions()
        }
    }
}