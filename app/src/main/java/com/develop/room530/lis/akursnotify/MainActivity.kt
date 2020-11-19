package com.develop.room530.lis.akursnotify

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*


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

        currency.observe(
            this,
            { newValue -> findViewById<TextView>(R.id.tv_currency).text = newValue })
        currencyNB.observe(
            this,
            { newValue -> findViewById<TextView>(R.id.tv_currencyNB).text = newValue })

        findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh).setOnRefreshListener {
            findViewById<TextView>(R.id.tv_currency).text = getString(R.string.updateMessage)
            findViewById<TextView>(R.id.tv_currencyNB).text = ""
            getCurrency()
            printChart()
            findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh).isRefreshing = false
        }

        if (savedInstanceState != null) {
            currency.value = savedInstanceState.getString(KEY_CURRENCY)
            currencyNB.value = savedInstanceState.getString(KEY_CURRENCYNB)
        } else getCurrency()

        printChart()
    }

    private fun printChart() {
        if (checkInternet() != true) return

        val chart = findViewById<LineChart>(R.id.chart)

        val start = getDateMinusFormat(getDateWithOffset(-7))
        val end = getDateMinusFormat(getDateWithOffset(+1))

        CoroutineScope(Dispatchers.Main).launch {
            val currencyHistory = withContext(Dispatchers.IO){
                RetrofitClass.service.getUsdCurrencyHistory(start, end)
            }

            chart.xAxis.valueFormatter =
                IndexAxisValueFormatter(currencyHistory.map { it.Date.substring(5, 10) })
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            val dataset = LineDataSet(currencyHistory.mapIndexed { index, nbrbModel ->
                Entry(
                    index.toFloat(),
                    nbrbModel.Cur_OfficialRate
                )
            }, "USD по НБ РБ")
            dataset.color = Color.RED
            dataset.valueTextSize = 12F
            val data = LineData(dataset)
            data.isHighlightEnabled = false
            chart.description.text = ""
            chart.data = data
            chart.axisRight.isEnabled = false
            chart.legend.textSize = 14F
            chart.setNoDataText("")
            chart.invalidate()
        }
    }

    private fun getCurrency() {

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
                RetrofitClass.service.getUsdCurrency()
            }
            currencyNB.value = getString(R.string.NB) + " : " + nbrb.Cur_OfficialRate
        }
    }

    private fun checkInternet(): Boolean? {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting
    }

    fun getDateWithOffset(offset: Int, date: Date = Date()): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DATE, offset)
        return cal.time
    }

    fun getDateMinusFormat(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("ru"))
        return sdf.format(date)
    }
}
