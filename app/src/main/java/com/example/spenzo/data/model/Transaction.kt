package com.example.spenzo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val type: String = "",
    val date: String = "",
    val note: String = ""
) : Serializable