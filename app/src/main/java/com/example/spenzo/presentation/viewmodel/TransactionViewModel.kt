package com.example.spenzo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spenzo.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TransactionViewModel : ViewModel() {
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
        onError: (Exception) -> Unit
    ) {
        var queryRef: Query = itemsCollection

        // Применяем фильтры
        if (!query.isNullOrEmpty()) {
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
                onSuccess(itemsList)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}