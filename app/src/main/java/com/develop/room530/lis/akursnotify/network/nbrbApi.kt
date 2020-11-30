package com.develop.room530.lis.akursnotify.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class NbrbModel(
    var Date: String,
    var Cur_OfficialRate: Float
)

interface NbrbApiService{
    @GET("api/exrates/rates/145")
    suspend fun getUsdCurrency(): NbrbModel

    @GET("api/exrates/rates/dynamics/145")
    suspend fun getUsdCurrencyHistory(
        @Query(value = "startdate") from : String, // 2020-11-10
        @Query(value = "enddate") to : String,
    ): List<NbrbModel>
}

object NbrbApi{
    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl("https://www.nbrb.by/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: NbrbApiService = retrofit.create(NbrbApiService::class.java)
}