package com.develop.room530.lis.akursnotify.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

data class AlfaModel(
    val title: String,
    val currenciesData: List<CurrencyRates>
)

data class CurrencyRates(
    val text: String,
    val date: String,
    val value: Rates,
)

data class Rates(
    val exchangeRate: List<Rate>,
    val conversionRate: List<Rate>
)

data class Rate(
    val title: String,
    val purchase: RateValue,
    val sell: RateValue
)

data class RateValue(
    val value: String,
    val change: String
)

interface AlfaApiService{
    @FormUrlEncoded
    @POST("exchange/digital/")
    suspend fun getAkursUsdCurrency(
        @Field("selectedDate") date : String = "30.11.2020"
    ): List<AlfaModel>
}

object AlfaApi{
    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl("https://www.alfabank.by/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: AlfaApiService = retrofit.create(AlfaApiService::class.java)
}