package com.develop.room530.lis.akursnotify

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {

    private var currency = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currency.observe(this, Observer { newValue -> findViewById<TextView>(R.id.tv_currency).text = newValue })

        getCurrency()

        findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh).setOnRefreshListener {
            findViewById<TextView>(R.id.tv_currency).text = getString(R.string.updateMessage)
            getCurrency()
            findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh).isRefreshing = false
        }
    }

    private fun getCurrency(){
        CoroutineScope(Dispatchers.Main).launch {
            currency.value = withContext(Dispatchers.IO) {
                val doc = Jsoup.connect("https://www.alfabank.by/services/a-kurs/").get()
                doc.select(".curr-table tr:first-of-type td:first-of-type").text()
            }
        }
    }
}
