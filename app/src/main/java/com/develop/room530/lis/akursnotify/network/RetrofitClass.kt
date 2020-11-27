package com.develop.room530.lis.akursnotify.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClass{
    private fun retrofit() : Retrofit = Retrofit.Builder()
        .baseUrl("https://www.nbrb.by/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: NbrbApi = retrofit().create(NbrbApi::class.java)
}