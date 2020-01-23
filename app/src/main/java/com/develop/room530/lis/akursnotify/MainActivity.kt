package com.develop.room530.lis.akursnotify

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

const val KEY_CURRENCY = "key_currency"
const val KEY_CURRENCYNB = "key_currencyNB"

class MainActivity : AppCompatActivity() {

    private var currency = MutableLiveData<String>()
    private var currencyNB = MutableLiveData<String>()

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(KEY_CURRENCY, currency.value)
        outState?.putString(KEY_CURRENCYNB, currencyNB.value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currency.observe(this, Observer { newValue -> findViewById<TextView>(R.id.tv_currency).text = newValue })
        currencyNB.observe(this, Observer { newValue -> findViewById<TextView>(R.id.tv_currencyNB).text = newValue })

        findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh).setOnRefreshListener {
            findViewById<TextView>(R.id.tv_currency).text = getString(R.string.updateMessage)
            findViewById<TextView>(R.id.tv_currencyNB).text = ""
            getCurrency()
            findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh).isRefreshing = false
        }


        if (savedInstanceState != null) {
            currency.value = savedInstanceState.getString(KEY_CURRENCY)
            currencyNB.value = savedInstanceState.getString(KEY_CURRENCYNB)
        }
        else getCurrency()
    }

    private fun getCurrency(){
        CoroutineScope(Dispatchers.Main).launch {
            val doc = withContext(Dispatchers.IO) {
                Jsoup.connect("https://www.alfabank.by/services/a-kurs/").get()
            }
            currency.value = doc.select(".curr-table tr:first-of-type td:first-of-type").text()
            currencyNB.value = getString(R.string.NB) + " : " + doc.select(".curr-table tr:last-of-type td:first-of-type").text()
        }
    }
}
