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
import com.develop.room530.lis.akursnotify.database.getDatabase
import com.develop.room530.lis.akursnotify.network.AlfaApi
import com.develop.room530.lis.akursnotify.network.NbrbApi
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        CoroutineScope(Dispatchers.Main).launch {
            val currencyHistory = withContext(Dispatchers.IO) {
                getDatabase(applicationContext).nbrbDatabaseDao.getNbrbkurs()
            }

            val rates = withContext(Dispatchers.IO) {
                getDatabase(applicationContext).akursDatabaseDao.getAkurs()
            }.sortedBy { it.date }

            val ADataset = LineDataSet(rates.mapIndexed { index, akurs ->
                Entry(
                    index.toFloat(),
                    akurs.rate.replace(',','.').toFloatOrNull() ?: -1.0F)
            }, "USD по А-Курс")

            ADataset.color = Color.RED
            ADataset.valueTextSize = 12F

            // chart.xAxis.valueFormatter = IndexAxisValueFormatter(rates.map { it.time })
//            val NbDataset = LineDataSet(currencyHistory.mapIndexed { index, nbrbModel ->
//                Entry(
//                    index.toFloat(),
//                    nbrbModel.rate.toFloat()
//                )
//            }, "USD по НБ")
            val datasets = listOf<ILineDataSet>(/*NbDataset,*/ ADataset)
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            val data = LineData(datasets)
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

        // TODO: make red/green triangle as in Lesson - selectors
        CoroutineScope(Dispatchers.Main).launch {

            val nbrb = withContext(Dispatchers.IO) {
                NbrbApi.service.getUsdRate()
            }

            val akursRates = AlfaApi.getAkursRatesOnDateImpl()

            currencyNB.value = getString(R.string.NB) + " : " + nbrb.price
            currency.value = akursRates.firstOrNull()?.price ?: "No data"
        }
    }

    private fun checkInternet(): Boolean? {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting
    }
}