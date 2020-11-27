package com.develop.room530.lis.akursnotify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup

suspend fun getAkursRate() : String{
    val doc = withContext(Dispatchers.IO) {
        try {
            Result.Success(Jsoup.connect("https://www.alfabank.by/exchange/digital/").get())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    if(doc is Result.Success){
        val jsonString =
            doc.data.select(".container-main section div div:nth-child(2)").attr("data-initial")

        val title = JSONObject(jsonString).getJSONArray("initialItems").getJSONObject(1)
            .getString("title")
        val time = JSONObject(jsonString).getJSONArray("initialItems").getJSONObject(1)
            .getJSONArray("currenciesData").getJSONObject(0).getString("text")

        return JSONObject(jsonString).getJSONArray("initialItems").getJSONObject(1)
            .getJSONArray("currenciesData").getJSONObject(0)
            .getJSONObject("value").getJSONArray("exchangeRate").getJSONObject(0)
            .getJSONObject("purchase").getString("value")
    }
    return doc.toString()
}