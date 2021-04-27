package com.develop.room530.lis.akursnotify

import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.develop.room530.lis.akursnotify.data.database.getDatabase
import com.develop.room530.lis.akursnotify.data.database.saveRatesInDb
import com.develop.room530.lis.akursnotify.data.network.AlfaApi
import com.develop.room530.lis.akursnotify.data.network.NbrbApi
import com.develop.room530.lis.akursnotify.databinding.FragmentHomeBinding
import com.develop.room530.lis.akursnotify.model.mapFromDb
import kotlinx.coroutines.*
import java.util.*

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

    private val adapter = RateAdapter()

    private var dialog: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("home", "onCreate $this")
    }

    override fun onStart() {
        super.onStart()
        Log.d("home", "onStart $this")
    }

    override fun onResume() {
        super.onResume()
        Log.d("home", "onResume $this")
    }

    override fun onPause() {
        super.onPause()
        Log.d("home", "onPause $this")
    }

    override fun onStop() {
        super.onStop()
        Log.d("home", "onStop $this")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("home", "onDetach $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("home", "onDestroy $this")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("home", "onCreateView $this")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("home", "onViewCreated $this")
        binding.alfaRateCard.rateLabel.text = getString(R.string.Akurs)
        binding.nbrbRateCard.rateLabel.text = getString(R.string.NB)

        binding.goalsCard.rates.adapter = adapter
        binding.historyCard.rates.adapter = adapter
        MockRatesData.liveNbRb.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        MockRatesData.newNbRb2()

        binding.goalsCard.rateCard.setOnClickListener {
//            binding.goalsCard.rates.animate().setDuration(1000L).alpha(0F).start()
//
////            ObjectAnimator.ofInt(it, "bottom", 0).apply {
////                duration = 1000
////                start()
////            }
            // TODO animate!!!!!!!!!!
            if (binding.goalsCard.rates.visibility != View.GONE) {
                binding.goalsCard.rates.visibility = View.GONE
                binding.goalsCard.delimiter.visibility = View.GONE
            }
            else{
                binding.goalsCard.rates.visibility = View.VISIBLE
                binding.goalsCard.delimiter.visibility = View.VISIBLE
            }
        }

        binding.historyCard.goalsLabel.text = "История курсов (8)"
        binding.historyCard.rateCard.setOnClickListener {
            //binding.goalsCard.rates.animate().setDuration(1000L).yBy(0F).start()

//            ObjectAnimator.ofInt(it, "bottom", 0).apply {
//                duration = 1000
//                start()
//            }
            // TODO animate!!!!!!!!!!
            if (binding.historyCard.rates.visibility != View.GONE) {
                binding.historyCard.rates.visibility = View.GONE
                binding.historyCard.delimiter.visibility = View.GONE
            }
            else{
                binding.historyCard.rates.visibility = View.VISIBLE
                binding.historyCard.delimiter.visibility = View.VISIBLE
            }
        }

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

        binding.floatingActionButton.setOnClickListener {
            dialog = showDatePicker()
            dialog?.show()
        }

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

        if (savedInstanceState != null) { // FIXME after theme change bundle is not null but livedata is null
            currency.value = savedInstanceState.getString(KEY_CURRENCY)
            currencyNB.value = savedInstanceState.getString(KEY_CURRENCYNB)
            comparingState.value = savedInstanceState.getFloat(COMPARING_STATE)
            comparingStateNb.value = savedInstanceState.getFloat(COMPARING_STATE_NB)
        } else getCurrency()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("home", "onSaveInstanceState")
        outState.putString(KEY_CURRENCY, currency.value)
        outState.putString(KEY_CURRENCYNB, currencyNB.value)
        outState.putFloat(COMPARING_STATE, comparingState.value ?: 0F)
        outState.putFloat(COMPARING_STATE_NB, comparingStateNb.value ?: 0F)
    }

    override fun onDestroyView() {
        Log.d("home", "onDestroyView $this")
        _binding = null
        dialog?.cancel()
        super.onDestroyView()
    }

    private fun getCurrency() {
        if (checkInternet() != true) {
            // FIXME snackbar?
            currency.value = getString(R.string.no_internet_message)
            currencyNB.value = ""
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            //val db = getDatabase(requireContext())

            val nbrbRateNetwork = NbrbApi.getUsdRateImpl() //"2020-11-23"
            val akursRatesNetwork = AlfaApi.getAkursRatesOnDateImpl() //"01.12.2020"

            if (isActive)
                saveRatesInDb(
                    requireContext(),
                    akursRatesNetwork,
                    nbrbRateNetwork?.let { listOf(it) })
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

    private fun showDatePicker(): DatePickerDialog {
        val calendar = Calendar.getInstance()
        return DatePickerDialog(
            requireContext(),
            { _, pickerYear, monthOfYear, dayOfMonth -> // FIXME implement recycler
                //binding.testCard.rateCard.visibility = View.VISIBLE
                viewLifecycleOwner.lifecycleScope.launch {
                    // TODO through DB
//                    val nbrbRate = NbrbApi.getUsdRateImpl("$pickerYear-${monthOfYear+1}-$dayOfMonth") //"2020-11-23"
//                    binding.testCard.rateLabel.text = "Нацбанк на $pickerYear-${monthOfYear+1}-$dayOfMonth"
//                    binding.testCard.rate.text = nbrbRate?.price.toString()
                }
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }
}