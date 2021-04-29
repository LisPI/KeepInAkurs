package com.develop.room530.lis.akursnotify.features.home

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
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
import com.develop.room530.lis.akursnotify.R
import com.develop.room530.lis.akursnotify.data.database.NbrbHistory
import com.develop.room530.lis.akursnotify.data.database.RatesGoal
import com.develop.room530.lis.akursnotify.data.database.getDatabase
import com.develop.room530.lis.akursnotify.data.database.saveRatesInDb
import com.develop.room530.lis.akursnotify.data.network.AlfaApi
import com.develop.room530.lis.akursnotify.data.network.NbrbApi
import com.develop.room530.lis.akursnotify.databinding.DialogCreateGoalBinding
import com.develop.room530.lis.akursnotify.databinding.FragmentHomeBinding
import com.develop.room530.lis.akursnotify.format
import com.develop.room530.lis.akursnotify.model.mapFromDb
import com.develop.room530.lis.akursnotify.setRateComparingState
import kotlinx.coroutines.*
import java.util.*

const val KEY_CURRENCY = "key_currency"
const val KEY_CURRENCYNB = "key_currencyNB"
const val COMPARING_STATE = "COMPARING_STATE"
const val COMPARING_STATE_NB = "COMPARING_STATE_NB"

const val FAB_ANIM_DURATION = 500L

class HomeFragment : Fragment() {

    private var currency = MutableLiveData<String>()
    private var currencyNB = MutableLiveData<String>()
    private var comparingState = MutableLiveData<Float>()
    private var comparingStateNb = MutableLiveData<Float>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val adapter = RateAdapter()
    private val historyAdapter = HistoryRatesAdapter()

    private var dialog: Dialog? = null

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

    private fun changeHeightView(view: View, value: Int, startValue: Int = view.measuredHeight) {
        val valueAnimator =
            ValueAnimator.ofInt(startValue, startValue + value)
        valueAnimator.duration = FAB_ANIM_DURATION
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("home", "onViewCreated $this")
        binding.alfaRateCard.rateLabel.text = getString(R.string.Akurs)
        binding.nbrbRateCard.rateLabel.text = getString(R.string.NB)

        binding.goalsCard.rates.adapter = adapter
        binding.historyCard.rates.adapter = historyAdapter

        getDatabase(requireContext()).nbrbHistoryDatabaseDao.getNbrbHistory()
            .observe(viewLifecycleOwner) {
                historyAdapter.submitList(it)
                binding.historyCard.goalsLabel.text = "История курсов (${it.size})"
            }

        getDatabase(requireContext()).ratesGoalDatabaseDao.getRatesGoals()
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.goalsCard.goalsLabel.text = "Мои цели (${it.size})"
            }
        var state = 0
        var h = 0
        binding.goalsCard.rateCard.setOnClickListener {
//            binding.goalsCard.rates.animate().setDuration(1000L).alpha(0F).start()
//
////            ObjectAnimator.ofInt(it, "bottom", 0).apply {
////                duration = 1000
////                start()
////            }
            // TODO animate!!!!!!!!!!

            if (state != 1) {

                binding.goalsCard.delimiter.animate()
                    .setDuration(FAB_ANIM_DURATION)
                    .alpha(0F)
                    .start()


//                val mFade: Fade = Fade(Fade.OUT)
//                TransitionManager.beginDelayedTransition(binding.goalsCard.rateCard, )
//                binding.goalsCard.rates.visibility = View.GONE
                //binding.goalsCard.rateCard.animate().setDuration(FAB_ANIM_DURATION).
                h = binding.goalsCard.rates.measuredHeight
                changeHeightView(binding.goalsCard.rates, -binding.goalsCard.rates.measuredHeight)
                state = 1

                binding.goalsCard.goalsCardStatus.setImageResource(R.drawable.ic_baseline_arrow_down_24)
//                ObjectAnimator.ofInt(binding.goalsCard.rateCard, "bottom", 10).apply {
//                    duration = 1000
//                    start()
//                }

                //Handler().postDelayed({binding.goalsCard.rates.visibility = View.GONE}, 1000L)
                //binding.goalsCard.delimiter.visibility = View.INVISIBLE
            } else {
                //val t = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                //binding.goalsCard.rates.measure(ConstraintLayout.LayoutParams.MATCH_PARENT, t)
                changeHeightView(binding.goalsCard.rates,  h, 0)
                state = 0

                binding.goalsCard.goalsCardStatus.setImageResource(R.drawable.ic_baseline_arrow_up_24)

                binding.goalsCard.delimiter.animate()
                    .setDuration(FAB_ANIM_DURATION)
                    .alpha(1F)
                    .start()
            }
        }

        binding.historyCard.rateCard.setOnClickListener {
            if (binding.historyCard.rates.visibility != View.GONE) {
                binding.historyCard.rates.visibility = View.GONE
                binding.historyCard.delimiter.visibility = View.INVISIBLE
                binding.historyCard.goalsCardStatus.setImageResource(R.drawable.ic_baseline_arrow_down_24)

            } else {
                binding.historyCard.rates.visibility = View.VISIBLE
                binding.historyCard.delimiter.visibility = View.VISIBLE
                binding.historyCard.goalsCardStatus.setImageResource(R.drawable.ic_baseline_arrow_up_24)
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

        // TODO youtube example
        binding.fabHistory.setOnClickListener {
            closeFabMenu()
            dialog = getDatePickerDialog()
            dialog?.show()
        }

        binding.fabGoal.setOnClickListener {
            closeFabMenu()
            dialog = getNewGoalDialog()
            dialog?.show()
        }

        binding.floatingActionButton.setOnClickListener {
            if (binding.box.visibility == View.INVISIBLE) {
                showFabMenu()
            } else {
                closeFabMenu()
            }
        }

        binding.box.setOnClickListener {
            closeFabMenu()
        }

        comparingState.observe(
            viewLifecycleOwner,
            { binding.alfaRateCard.rate.setRateComparingState(it) })
        comparingStateNb.observe(
            viewLifecycleOwner,
            { binding.nbrbRateCard.rate.setRateComparingState(it) })

        binding.swipeToRefresh.setOnRefreshListener {
            binding.alfaRateCard.rate.text = getString(R.string.updateMessage)
            binding.alfaRateCard.rate.setRateComparingState(0F)
            binding.nbrbRateCard.rate.text = getString(R.string.updateMessage)
            binding.nbrbRateCard.rate.setRateComparingState(0F)
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

    private fun showFabMenu() {
        binding.floatingActionButton.animate().rotation(225F).setDuration(FAB_ANIM_DURATION).start()
        binding.box.visibility = View.VISIBLE

        binding.fabHistory.animate().setDuration(FAB_ANIM_DURATION).alpha(1F)
            .translationY(-binding.floatingActionButton.height.toFloat())
            .start()
        binding.fabGoal.animate().setDuration(FAB_ANIM_DURATION).alpha(1F)
            .translationY(-binding.floatingActionButton.height.toFloat() - binding.fabHistory.height.toFloat())
            .start()
    }

    private fun closeFabMenu() {
        binding.box.visibility = View.INVISIBLE
        binding.floatingActionButton.animate().rotation(0F).setDuration(FAB_ANIM_DURATION).start()

        binding.fabHistory.animate().setDuration(FAB_ANIM_DURATION).translationY(0F).alpha(0F)
            .start()
        binding.fabGoal.animate().setDuration(FAB_ANIM_DURATION).translationY(0F).alpha(0F).start()
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
        binding.goalsCard.rates.adapter = null
        binding.historyCard.rates.adapter = null
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

    private fun getDatePickerDialog(): DatePickerDialog {
        val calendar = Calendar.getInstance()
        return DatePickerDialog(
            requireContext(),
            { _, pickerYear, monthOfYear, dayOfMonth ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val nbrbRate =
                        NbrbApi.getUsdRateImpl("$pickerYear-${monthOfYear + 1}-$dayOfMonth") //"2020-11-23"
                    nbrbRate?.let {
                        getDatabase(requireContext()).nbrbHistoryDatabaseDao.insertNbrbHistory(
                            NbrbHistory(it.price.toString(), it.date)
                        )
                    }
                }
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }

    private fun getNewGoalDialog(): AlertDialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogBinding = DialogCreateGoalBinding.inflate(inflater, null, false)
            builder.setView(dialogBinding.root)

            builder.setMessage("Здарова отец")
                .setPositiveButton(
                    "ok"
                ) { dialog, id ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        getDatabase(requireContext()).ratesGoalDatabaseDao.insertRatesGoal(
                            RatesGoal(
                                bank = dialogBinding.selectBank.selectedItem.toString(),
                                trend = if (dialogBinding.selectTrend.selectedItem.toString() == "дороже") 1 else -1,
                                rate = dialogBinding.goalEdit.editText?.text.toString()
                            )
                        )
                    }
                }
                .setNegativeButton(
                    "cancel"
                ) { dialog, id ->
                    // User cancelled the dialog
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}