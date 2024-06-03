package com.example.spenzo.domain.interfaces

import com.example.spenzo.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApi {
    @GET("/v4/latest/{base}")
    suspend fun getRates(
        @Path("base") base: String,
        @Query("apikey") apiKey: String
    ): Response<CurrencyResponse>
}