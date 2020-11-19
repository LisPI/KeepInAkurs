package com.develop.room530.lis.akursnotify

import retrofit2.http.GET
import retrofit2.http.Query

data class NbrbModel(
    var Date: String,
    var Cur_OfficialRate: Float
)

interface NbrbApi{
    @GET("api/exrates/rates/145")
    suspend fun getUsdCurrency(): NbrbModel

    @GET("api/exrates/rates/dynamics/145")
    suspend fun getUsdCurrencyHistory(
        @Query(value = "startdate") from : String, // 2020-11-10
        @Query(value = "enddate") to : String,
    ): List<NbrbModel>
}