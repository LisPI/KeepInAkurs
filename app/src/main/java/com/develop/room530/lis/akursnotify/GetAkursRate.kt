package com.develop.room530.lis.akursnotify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup

// TODO must return Result structure
suspend fun getAkursRate() : String{
    val doc = withContext(Dispatchers.IO) {
        try {
            Jsoup.connect("https://www.alfabank.by/exchange/digital/").get()
        } catch (e: Exception) {
            null
        }
    }
    doc?.let {
        val jsonString =
            doc.select(".container-main section div div:nth-child(2)").attr("data-initial")

        val title = JSONObject(jsonString).getJSONArray("initialItems").getJSONObject(1)
            .getString("title")
        val time = JSONObject(jsonString).getJSONArray("initialItems").getJSONObject(1)
            .getJSONArray("currenciesData").getJSONObject(0).getString("text")

        return JSONObject(jsonString).getJSONArray("initialItems").getJSONObject(1)
            .getJSONArray("currenciesData").getJSONObject(0)
            .getJSONObject("value").getJSONArray("exchangeRate").getJSONObject(0)
            .getJSONObject("purchase").getString("value")
    }
    return "-1"
}