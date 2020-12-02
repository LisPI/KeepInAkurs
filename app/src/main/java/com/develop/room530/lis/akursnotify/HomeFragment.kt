package com.develop.room530.lis.akursnotify

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.develop.room530.lis.akursnotify.network.AlfaApi
import com.develop.room530.lis.akursnotify.network.NbrbApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var currency = MutableLiveData<String>()
    private var currencyNB = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currency.observe(
            this,
            { newValue -> view.findViewById<TextView>(R.id.tv_currency).text = newValue })
        currencyNB.observe(
            this,
            { newValue -> view.findViewById<TextView>(R.id.tv_currencyNB).text = newValue })

        if (savedInstanceState != null) {
            currency.value = savedInstanceState.getString(KEY_CURRENCY)
            currencyNB.value = savedInstanceState.getString(KEY_CURRENCYNB)
        } else getCurrency()
    }

    private fun getCurrency() {
        if (checkInternet() != true) {
            currency.value = "Check your Internet connection"
            currencyNB.value = ""
            return
        }

        // TODO: make red/green triangle as in Lesson - selectors
        CoroutineScope(Dispatchers.Main).launch {

            val nbrb = NbrbApi.getUsdRateImpl()
            val akursRates = AlfaApi.getAkursRatesOnDateImpl()

            currencyNB.value = getString(R.string.NB) + " : " + (nbrb?.price ?: "no data")
            currency.value = akursRates.firstOrNull()?.price ?: "No data"
        }
    }

    private fun checkInternet(): Boolean? {
        val cm = this@HomeFragment.requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting
    }
}