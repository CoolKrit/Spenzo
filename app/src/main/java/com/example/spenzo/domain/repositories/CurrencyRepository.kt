package com.example.spenzo.domain.repositories

import com.example.spenzo.domain.interfaces.CurrencyApi

class CurrencyRepository(private val api: CurrencyApi) {

    suspend fun fetchCurrencyRates(base: String, apiKey: String): Map<String, Double>? {
        return try {
            val response = api.getRates(base, apiKey)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.rates
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}