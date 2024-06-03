package com.example.spenzo.domain.repositories

import androidx.lifecycle.LiveData
import com.example.spenzo.data.db.TransactionDao
import com.example.spenzo.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TransactionRepository(private val transactionDao: TransactionDao, private val firebaseStore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth) {

    private val itemsCollection = firebaseStore.collection("users")
        .document(firebaseAuth.currentUser!!.uid)
        .collection("transactions")

    fun getTransactions(): LiveData<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
        itemsCollection.document(transaction.id).set(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
        itemsCollection.document(transaction.id).set(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
        itemsCollection.document(transaction.id).delete()
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }

    fun loadItemsFromServer(query: String, category: String?, type: String?, onSuccess: (List<Transaction>) -> Unit, onFailure: () -> Unit) {
        var queryRef: Query = itemsCollection

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
                var itemsList = querySnapshot.documents.mapNotNull { it.toObject(Transaction::class.java) }
                itemsList = itemsList.sortedByDescending { it.id }
                onSuccess(itemsList)
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun loadUserName(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        firebaseStore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onSuccess(document.getString("name") ?: "User")
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}