package com.develop.room530.lis.akursnotify

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.develop.room530.lis.akursnotify.database.Akurs
import com.develop.room530.lis.akursnotify.database.Nbrbkurs
import com.develop.room530.lis.akursnotify.database.getDatabase
import com.develop.room530.lis.akursnotify.databinding.FragmentHomeBinding
import com.develop.room530.lis.akursnotify.network.AlfaAkursRate
import com.develop.room530.lis.akursnotify.network.AlfaApi
import com.develop.room530.lis.akursnotify.network.NbrbApi
import com.develop.room530.lis.akursnotify.network.NbrbModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val KEY_CURRENCY = "key_currency"
const val KEY_CURRENCYNB = "key_currencyNB"

class HomeFragment : Fragment() {

    private var currency = MutableLiveData<String>()
    private var currencyNB = MutableLiveData<String>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currency.observe(
            viewLifecycleOwner,
            { newValue ->
                binding.tvCurrency.text = newValue
            })
        currencyNB.observe(
            viewLifecycleOwner,
            { newValue ->
                binding.tvCurrencyNB.text = newValue
            })

        binding.swipeToRefresh.setOnRefreshListener {
            binding.tvCurrency.text = getString(R.string.updateMessage)
            binding.tvCurrencyNB.text = ""
            binding.swipeToRefresh.isRefreshing = false
            getCurrency()
        }

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

            val nbrb = NbrbApi.getUsdRateImpl() //"2020-11-23"
            val akursRates = AlfaApi.getAkursRatesOnDateImpl() //"01.12.2020"

            saveRatesInDb(akursRates, nbrb)

            val akursRate = withContext(Dispatchers.IO){getDatabase(requireContext()).akursDatabaseDao.getLastAkurs()}

            currencyNB.value = getString(R.string.NB) + " : " + (nbrb?.price ?: "no data")
            currency.value = akursRate?.rate ?: "No data"
        }
    }

    private fun checkInternet(): Boolean? {
        val cm = this@HomeFragment.requireActivity()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENCY, currency.value)
        outState.putString(KEY_CURRENCYNB, currencyNB.value)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    suspend fun saveRatesInDb(akursRates: List<AlfaAkursRate>, nbrb: NbrbModel?) {
        withContext(Dispatchers.IO) {
            for (rate in akursRates) {
                getDatabase(requireContext()).akursDatabaseDao.insertAkurs(
                    akurs = Akurs(
                        rate = rate.price,
                        date = rate.date,
                        time = rate.time,
                    )
                )
            }
            nbrb?.let {
                getDatabase(requireContext()).nbrbDatabaseDao.insertNbrbkurs(
                    Nbrbkurs(
                        it.price.toString(),
                        it.date
                    )
                )
            }
        }
    }
}