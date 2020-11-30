package com.develop.room530.lis.akursnotify.network

import com.develop.room530.lis.akursnotify.getDateMinusFormat
import com.develop.room530.lis.akursnotify.getDateWithOffset
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class NbrbModel(
    @SerializedName("Date") var date: String,
    @SerializedName("Cur_OfficialRate") var price: Float
)

interface NbrbApiService{
    @GET("api/exrates/rates/145")
    suspend fun getUsdRate(
        @Query(value = "ondate") to : String = getDateMinusFormat(getDateWithOffset(0))   // 2020-11-30
    ): NbrbModel

    @GET("api/exrates/rates/dynamics/145")
    suspend fun getUsdRatesHistory(
        @Query(value = "startdate") from : String = getDateMinusFormat(getDateWithOffset(-7)), // 2020-11-30
        @Query(value = "enddate") to : String = getDateMinusFormat(getDateWithOffset(+1)),
    ): List<NbrbModel>
}

object NbrbApi{
    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl("https://www.nbrb.by/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: NbrbApiService = retrofit.create(NbrbApiService::class.java)
}