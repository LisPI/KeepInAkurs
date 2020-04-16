package com.develop.room530.lis.akursnotify

import retrofit2.http.GET

data class NbrbModel(
    var Date: String? = null,
    var Cur_OfficialRate: String? = null
)

interface NbrbApi{
    @GET("api/exrates/rates/145")
    suspend fun getUsdCurrency(): NbrbModel
}