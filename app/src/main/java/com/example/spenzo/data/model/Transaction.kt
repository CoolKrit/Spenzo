package com.example.spenzo.data.model

import java.io.Serializable

data class Transaction(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val type: String = "",
    val category: String = "",
    val description: String = ""
) : Serializable