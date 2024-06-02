package com.example.spenzo

import com.example.spenzo.domain.interfaces.CurrencyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.exchangerate-api.com"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: CurrencyApi by lazy {
        retrofit.create(CurrencyApi::class.java)
    }
}