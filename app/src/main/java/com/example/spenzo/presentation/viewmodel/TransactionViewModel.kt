package com.example.spenzo.presentation.viewmodel

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.spenzo.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TransactionViewModel(private val context: Context) : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStore = FirebaseFirestore.getInstance()

    private val itemsCollection =
        firebaseStore.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("transactions")

    fun getItems(
        query: String,
        category: String?,
        type: String?,
        onSuccess: (List<Transaction>) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        var queryRef: Query = itemsCollection

        // Применяем фильтры
        if (query.isNotEmpty()) {
            queryRef = queryRef.whereEqualTo("title", query)
        }
        if (!category.isNullOrEmpty()) {
            queryRef = queryRef.whereEqualTo("category", category)
        }
        if (!type.isNullOrEmpty()) {
            queryRef = queryRef.whereEqualTo("type", type)
        }

        queryRef.get()
            .addOnSuccessListener { querySnapshot ->
                val itemsList = mutableListOf<Transaction>()
                for (document in querySnapshot.documents) {
                    val item = document.toObject(Transaction::class.java)
                    item?.let { itemsList.add(it) }
                }
                saveItemsLocally(itemsList)
                onSuccess(itemsList)
            }
            .addOnFailureListener { exception ->
                val locallySavedItems = loadItemsLocally(query, category, type)
                if (locallySavedItems.isNotEmpty()) {
                    onSuccess(locallySavedItems)
                } else {
                    onError(exception)
                }
            }
    }

    private fun saveItemsLocally(items: List<Transaction>) {
        val gson = Gson()
        val itemsJson = gson.toJson(items)
        context.getSharedPreferences("TransactionData", Context.MODE_PRIVATE).edit {
            putString("itemsList", itemsJson)
        }
    }

    fun loadItemsLocally(query: String, category: String?, type: String?): List<Transaction> {
        val gson = Gson()
        val sharedPreferences = context.getSharedPreferences("TransactionData", Context.MODE_PRIVATE)
        val itemsJson = sharedPreferences.getString("itemsList", null)
        val typeToken: Type = object : TypeToken<List<Transaction>>() {}.type
        val itemsList: List<Transaction> = gson.fromJson(itemsJson, typeToken) ?: emptyList()

        return itemsList.filter { transaction ->
            val matchesQuery = query.isEmpty() || transaction.title.contains(query, ignoreCase = true)
            val matchesCategory = category.isNullOrEmpty() || transaction.category == category
            val matchesType = type.isNullOrEmpty() || transaction.type == type
            matchesQuery && matchesCategory && matchesType
        }
    }
}