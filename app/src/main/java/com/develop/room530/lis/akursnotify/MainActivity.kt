package com.develop.room530.lis.akursnotify

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

const val KEY_CURRENCY = "key_currency"
const val KEY_CURRENCYNB = "key_currencyNB"

class MainActivity : AppCompatActivity() {

    private var currency = MutableLiveData<String>()
    private var currencyNB = MutableLiveData<String>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENCY, currency.value)
        outState.putString(KEY_CURRENCYNB, currencyNB.value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currency.observe(this, { newValue -> findViewById<TextView>(R.id.tv_currency).text = newValue })
        currencyNB.observe(this, { newValue -> findViewById<TextView>(R.id.tv_currencyNB).text = newValue })

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

        printChart()
    }

    private fun printChart() {
        val chart = findViewById<LineChart>(R.id.chart)
        chart.data = LineData(LineDataSet(listOf(Entry(1F, 2F), Entry(2F, 6F), Entry(4F, 3F)),"myLabel"))
        chart.invalidate()
    }

    private fun getCurrency(){

        if (checkInternet() != true) {
            currency.value = "Check your Internet connection"
            currencyNB.value = ""
            return
        }

        // TODO: get update 1 time per day for example
        // TODO: make red/green triangle as in Lesson - selectors
        CoroutineScope(Dispatchers.Main).launch {

            val doc = withContext(Dispatchers.IO) {
                Jsoup.connect("https://www.alfabank.by/services/a-kurs/").get()
            }
            currency.value = doc.select(".curr-table tr:first-of-type td:first-of-type").text()

            val nbrb = withContext(Dispatchers.IO) {
                RetrofitClass.service?.getUsdCurrency()
            }
            currencyNB.value = getString(R.string.NB) + " : " + nbrb?.Cur_OfficialRate
        }
    }

    private fun checkInternet(): Boolean? {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting
    }
}
