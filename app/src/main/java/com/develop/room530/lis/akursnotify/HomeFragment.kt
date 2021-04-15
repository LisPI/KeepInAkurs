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

class HomeFragment : Fragment() {

    private var currency = MutableLiveData<String>()
    private var currencyNB = MutableLiveData<String>()
    private var comparingState = MutableLiveData<Float>()

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
                binding.tvCurrency.text = newValue
            })
        currencyNB.observe(
            viewLifecycleOwner,
            { newValue ->
                binding.tvCurrencyNB.text = newValue
            })
        comparingState.observe(viewLifecycleOwner, { setRateComparingState(it) })

        binding.swipeToRefresh.setOnRefreshListener {
            binding.tvCurrency.text = getString(R.string.updateMessage)
            binding.tvCurrencyNB.text = ""
            binding.swipeToRefresh.isRefreshing = false
            getCurrency()
        }

        if (savedInstanceState != null) {
            currency.value = savedInstanceState.getString(KEY_CURRENCY)
            currencyNB.value = savedInstanceState.getString(KEY_CURRENCYNB)
            comparingState.value = savedInstanceState.getFloat(COMPARING_STATE)
        } else getCurrency()
    }

    private fun getCurrency() {
        if (checkInternet() != true) {
            currency.value = "Check your Internet connection"
            currencyNB.value = ""
            return
        }

        coroutineScope.launch {

            //val db = getDatabase(requireContext())

            val nbrbRateNetwork = NbrbApi.getUsdRateImpl() //"2020-11-23"
            val akursRatesNetwork = AlfaApi.getAkursRatesOnDateImpl() //"01.12.2020"

            //val db = getDatabase(requireContext())

            if (isActive)
                saveRatesInDb(requireContext(), akursRatesNetwork, nbrbRateNetwork)
            val akursRates = withContext(Dispatchers.IO) {
                getDatabase(requireContext()).akursDatabaseDao.getLastAkurs(2).map { mapFromDb(it) }
            }
            val nbrbRates = withContext(Dispatchers.IO) {
                getDatabase(requireContext()).nbrbDatabaseDao.getLastNbrbKurs(2)
                    .map { mapFromDb(it) }
            }

            if (akursRates.size > 1) {
                // TODO I want add difference between values
                comparingState.value = akursRates[1].rate - akursRates[0].rate
            }

            currencyNB.value =
                getString(R.string.NB) + " : " + (nbrbRates.firstOrNull()?.rate.toString()
                    ?: getString(R.string.no_data_label)) // FIXME
            currency.value =
                akursRates.firstOrNull()?.rate.toString() ?: getString(R.string.no_data_label)
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
    }

    override fun onDestroyView() {
        _binding = null
        job.cancel()
        super.onDestroyView()
    }

    private fun setRateComparingState(comparingResult: Float) {
        when {
            comparingResult < 0 -> {
                binding.tvCurrency.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_triangle_up,
                    0
                )
//                binding.tvCurrency.compoundDrawables[2].colorFilter = PorterDuffColorFilter(
//                    resources.getColor(R.color.colorPrimary),
//                    PorterDuff.Mode.SRC_IN
//                )
            }
            comparingResult > 0 -> {
                binding.tvCurrency.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_triangle_down,
                    0
                )
            }
            comparingResult == 0F -> {
                binding.tvCurrency.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
                )
            }
        }
    }
}