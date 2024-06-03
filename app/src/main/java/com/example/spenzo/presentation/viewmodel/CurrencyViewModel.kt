package com.example.spenzo.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.spenzo.domain.repositories.CurrencyRepository
import com.example.spenzo.RetrofitInstance
import kotlinx.coroutines.launch

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    private val currencyRepository: CurrencyRepository

    private val _currencyRates = MutableLiveData<Map<String, Double>>()
    val currencyRates: LiveData<Map<String, Double>> get() = _currencyRates

    init {
        val api = RetrofitInstance.api
        currencyRepository = CurrencyRepository(api)
        fetchCurrencyRates("USD")
    }

    fun fetchCurrencyRates(base: String) {
        val apiKey = "32d909c213d5f6256ed5d2bd"
        viewModelScope.launch {
            val rates = currencyRepository.fetchCurrencyRates(base, apiKey)
            rates?.let { _currencyRates.postValue(it) }
        }
    }
}