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
import com.develop.room530.lis.akursnotify.data.database.getDatabase
import com.develop.room530.lis.akursnotify.data.database.saveRatesInDb
import com.develop.room530.lis.akursnotify.data.network.AlfaApi
import com.develop.room530.lis.akursnotify.data.network.NbrbApi
import com.develop.room530.lis.akursnotify.databinding.FragmentHomeBinding
import com.develop.room530.lis.akursnotify.model.mapFromDb
import kotlinx.coroutines.*

const val KEY_CURRENCY = "key_currency"
const val KEY_CURRENCYNB = "key_currencyNB"
const val COMPARING_STATE = "COMPARING_STATE"
const val COMPARING_STATE_NB = "COMPARING_STATE_NB"

class HomeFragment : Fragment() {

    private var currency = MutableLiveData<String>()
    private var currencyNB = MutableLiveData<String>()
    private var comparingState = MutableLiveData<Float>()
    private var comparingStateNb = MutableLiveData<Float>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.alfaRateCard.rateLabel.text = getString(R.string.Akurs)
        binding.nbrbRateCard.rateLabel.text = getString(R.string.NB)

        binding.button.setOnClickListener {
            val sp = requireContext().getSharedPreferences(
                this.getString(R.string.app_pref),
                Context.MODE_PRIVATE
            )
            sp.edit().apply {
                putBoolean(requireContext().getString(R.string.onboarding_complete), false)
                apply()
            }
        }

        currency.observe(
            viewLifecycleOwner,
            { newValue ->
                binding.alfaRateCard.rate.text = newValue
            })
        currencyNB.observe(
            viewLifecycleOwner,
            { newValue ->
                binding.nbrbRateCard.rate.text = newValue
            })

        comparingState.observe(
            viewLifecycleOwner,
            { binding.alfaRateCard.rate.setRateComparingState(it) })
        comparingStateNb.observe(
            viewLifecycleOwner,
            { binding.nbrbRateCard.rate.setRateComparingState(it) })

        binding.swipeToRefresh.setOnRefreshListener {
            binding.alfaRateCard.rate.text = getString(R.string.updateMessage)
            binding.nbrbRateCard.rate.text = getString(R.string.updateMessage)
            binding.swipeToRefresh.isRefreshing = false
            getCurrency()
        }

        if (savedInstanceState != null) {
            currency.value = savedInstanceState.getString(KEY_CURRENCY)
            currencyNB.value = savedInstanceState.getString(KEY_CURRENCYNB)
            comparingState.value = savedInstanceState.getFloat(COMPARING_STATE)
            comparingStateNb.value = savedInstanceState.getFloat(COMPARING_STATE_NB)
        } else getCurrency()
    }

    private fun getCurrency() {
        if (checkInternet() != true) {
            currency.value = getString(R.string.no_internet_message)
            currencyNB.value = ""
            return
        }

        coroutineScope.launch {

            //val db = getDatabase(requireContext())

            val nbrbRateNetwork = NbrbApi.getUsdRateImpl() //"2020-11-23"
            val akursRatesNetwork = AlfaApi.getAkursRatesOnDateImpl() //"01.12.2020"

            if (isActive)
                saveRatesInDb(requireContext(), akursRatesNetwork, nbrbRateNetwork)
            val akursRates = withContext(Dispatchers.IO) {
                getDatabase(requireContext()).akursDatabaseDao.getLastAkurs(2).map { mapFromDb(it) }
            }
            val nbrbRates = withContext(Dispatchers.IO) {
                getDatabase(requireContext()).nbrbDatabaseDao.getLastNbrbKurs(2)
                    .map { mapFromDb(it) }
            }

            // TODO I want add difference between values
            if (akursRates.size > 1) {
                comparingState.value = akursRates[1].rate - akursRates[0].rate
            }
            if (nbrbRates.size > 1) {
                comparingStateNb.value = nbrbRates[1].rate - nbrbRates[0].rate
            }

            // FIXME
            currencyNB.value = nbrbRates.firstOrNull()?.rate?.format(4)
                ?: getString(R.string.no_data_label)
            currency.value =
                akursRates.firstOrNull()?.rate?.format(4) ?: getString(R.string.no_data_label)
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
        outState.putFloat(COMPARING_STATE, comparingState.value ?: 0F)
        outState.putFloat(COMPARING_STATE_NB, comparingStateNb.value ?: 0F)
    }

    override fun onDestroyView() {
        _binding = null
        job.cancel()
        super.onDestroyView()
    }
}