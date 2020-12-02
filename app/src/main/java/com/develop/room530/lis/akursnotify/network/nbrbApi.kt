package com.develop.room530.lis.akursnotify.network

import android.util.Log
import com.develop.room530.lis.akursnotify.getDateMinusFormat
import com.develop.room530.lis.akursnotify.getDateWithOffset
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

data class NbrbModel(
    @SerializedName("Date") var date: Date,
    @SerializedName("Cur_OfficialRate") var price: Float
)

interface NbrbApiService {
    @GET("api/exrates/rates/145")
    suspend fun getUsdRate(
        @Query(value = "ondate") date: String = getDateMinusFormat(getDateWithOffset(0))   // 2020-11-30
    ): NbrbModel

    @GET("api/exrates/rates/dynamics/145")
    suspend fun getUsdRatesHistory(
        @Query(value = "startdate") from: String = getDateMinusFormat(getDateWithOffset(-7)), // 2020-11-30
        @Query(value = "enddate") to: String = getDateMinusFormat(getDateWithOffset(+1)),
    ): List<NbrbModel>
}

object NbrbApi {
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.nbrb.by/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val service: NbrbApiService = retrofit.create(NbrbApiService::class.java)

    suspend fun getUsdRateImpl(date: String = getDateMinusFormat(getDateWithOffset(0))): NbrbModel? {
        return try {
            withContext(Dispatchers.IO) {
                service.getUsdRate(date)
            }
        } catch (e: Exception) {
            Log.d("error", e.toString())
            null
        }
    }

    suspend fun getUsdRateHistoryImpl(from: String = getDateMinusFormat(getDateWithOffset(-7)), to: String = getDateMinusFormat(getDateWithOffset(+1))): List<NbrbModel> {
        return try {
            withContext(Dispatchers.IO) {
                service.getUsdRatesHistory(from, to)
            }
        } catch (e: Exception) {
            Log.d("error", e.toString())
            listOf()
        }
    }
}