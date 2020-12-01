package com.develop.room530.lis.akursnotify.network

import com.develop.room530.lis.akursnotify.getDateDotFormat
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.*

data class AlfaDtoModel(
    val title: String,
    val currenciesData: List<CurrencyRates>
)

data class CurrencyRates(
    @SerializedName("text") val time: String,
    val date: Date,
    @SerializedName("value") val rates: Rates,
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
    @SerializedName("value") val price: String,
    val change: String
)

interface AlfaApiService {
    @FormUrlEncoded
    @POST("exchange/digital/")
    suspend fun getAkursRatesOnDate(
        @Field("selectedDate") date: String
    ): List<AlfaDtoModel>
}

object AlfaApi {
    private val gson =  GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.alfabank.by/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val service: AlfaApiService = retrofit.create(AlfaApiService::class.java)

    suspend fun getAkursRatesOnDateImpl(date: String = getDateDotFormat(Date())): List<AlfaAkursRate> {
        val data = withContext(Dispatchers.IO) {
            service.getAkursRatesOnDate(date)
        }
        val akursData = data.filter { it.title.contains("A-Курс") }  // A - EN character !!!!!

        val rates = akursData.map {
            it.currenciesData.map { cur ->
                AlfaAkursRate(
                    cur.date,
                    cur.time,
                    cur.rates.exchangeRate[0].purchase.price,
                    cur.rates.exchangeRate[0].purchase.change
                )
            }
        }
        return rates.flatten()
    }
}

data class AlfaAkursRate(
    val date: Date,
    val time: String,
    val price: String,
    val change: String
)