package com.develop.room530.lis.akursnotify.features.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.develop.room530.lis.akursnotify.*
import com.develop.room530.lis.akursnotify.data.database.NbrbHistory
import com.develop.room530.lis.akursnotify.data.database.RatesGoal
import com.develop.room530.lis.akursnotify.data.database.getDatabase
import com.develop.room530.lis.akursnotify.data.database.saveRatesInDb
import com.develop.room530.lis.akursnotify.data.network.AlfaApi
import com.develop.room530.lis.akursnotify.data.network.NbrbApi
import com.develop.room530.lis.akursnotify.databinding.DialogCreateGoalBinding
import com.develop.room530.lis.akursnotify.databinding.FragmentHomeBinding
import com.develop.room530.lis.akursnotify.model.mapFromDb
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

const val KEY_CURRENCY = "key_currency"
const val KEY_CURRENCYNB = "key_currencyNB"
const val COMPARING_STATE = "COMPARING_STATE"
const val COMPARING_STATE_NB = "COMPARING_STATE_NB"

const val FAB_ANIM_DURATION = 500L

class HomeFragment : Fragment() {

    private var currency = MutableLiveData<String>() //TODO init load message
    private var currencyNB = MutableLiveData<String>()
    private var comparingState = MutableLiveData<Float>()
    private var comparingStateNb = MutableLiveData<Float>()
    private var showSnackBar = MutableLiveData(false)

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val adapter = RateAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.delete_goal_item_dialog_label))
            .setPositiveButton(getString(R.string.ok_dialog_label)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    getDatabase(requireContext()).ratesGoalDatabaseDao.deleteGoal(it.id)
                }
            }
            .setNegativeButton(getString(R.string.cancel_dialog_label)) { _, _ -> }
            .create()
            .show()
    }
    private val historyAdapter = HistoryRatesAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.delete_history_item_dialog_label))
            .setPositiveButton(getString(R.string.ok_dialog_label)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    getDatabase(requireContext()).nbrbHistoryDatabaseDao.deleteHistoryItem(it.date)
                }
            }
            .setNegativeButton(getString(R.string.cancel_dialog_label)) { _, _ -> }
            .create()
            .show()
    }

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("home", "onCreate $this")

        if (savedInstanceState != null) {
            currency.value = savedInstanceState.getString(KEY_CURRENCY)
            currencyNB.value = savedInstanceState.getString(KEY_CURRENCYNB)
            comparingState.value = savedInstanceState.getFloat(COMPARING_STATE)
            comparingStateNb.value = savedInstanceState.getFloat(COMPARING_STATE_NB)
        } else getCurrency()
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

        showSnackBar.observe(viewLifecycleOwner) {
            if (it == true) {
                Snackbar.make(
                    binding.root,
                    R.string.no_internet_message,
                    Snackbar.LENGTH_SHORT
                ).show()

                showSnackBar.value = false
            }
        }

        binding.goalsCard.rates.adapter = adapter
        binding.historyCard.rates.adapter = historyAdapter

        getDatabase(requireContext()).nbrbHistoryDatabaseDao.getNbrbHistory()
            .observe(viewLifecycleOwner) {
                historyAdapter.submitList(it)
                binding.historyCard.goalsLabel.text =
                    getString(R.string.rates_history_label, it.size)
                if (it.isEmpty())
                    binding.historyCard.animatedCollapse(FAB_ANIM_DURATION)
            }

        getDatabase(requireContext()).ratesGoalDatabaseDao.getRatesGoals()
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.goalsCard.goalsLabel.text = getString(R.string.rates_goals_label, it.size)
                if (it.isEmpty())
                    binding.goalsCard.animatedCollapse(FAB_ANIM_DURATION)
            }

        binding.goalsCard.rateCard.setOnClickListener {
            if (adapter.itemCount != 0)
                if (binding.goalsCard.delimiter.alpha > 0.5F) {
                    binding.goalsCard.animatedCollapse(FAB_ANIM_DURATION)
                } else {
                    binding.goalsCard.animatedExpand(FAB_ANIM_DURATION)
                }
        }

        binding.historyCard.rateCard.setOnClickListener {
            if (historyAdapter.itemCount != 0)
                if (binding.historyCard.delimiter.alpha > 0.5F) {
                    binding.historyCard.animatedCollapse(FAB_ANIM_DURATION)
                } else {
                    binding.historyCard.animatedExpand(FAB_ANIM_DURATION)
                }
        }

        // FIXME easter egg
        binding.alfaRateCard.rateCard.setOnLongClickListener {
            val sp = requireContext().getSharedPreferences(
                this.getString(R.string.app_pref),
                Context.MODE_PRIVATE
            )
            sp.edit().apply {
                putBoolean(requireContext().getString(R.string.onboarding_complete), false)
                apply()
            }
            true
        }

        currency.observe(
            viewLifecycleOwner,
            { newValue ->
                if (newValue == getString(R.string.no_internet_message)) {
                   lifecycleScope.launch {
                       val lastValue = withContext(Dispatchers.IO){getDatabase(requireContext()).akursDatabaseDao.getLastAkurs(1)}
                            .firstOrNull()?.rate ?: getString(R.string.no_data_label)
                       binding.alfaRateCard.rate.text =
                           Html.fromHtml(getString(R.string.last_value_for_no_internet, lastValue))
                    }
                } else
                    binding.alfaRateCard.rate.text = newValue
            })
        currencyNB.observe(
            viewLifecycleOwner,
            { newValue ->
                if (newValue == getString(R.string.no_internet_message)) {
                    lifecycleScope.launch {
                        val lastValue = withContext(Dispatchers.IO){getDatabase(requireContext()).nbrbDatabaseDao.getLastNbrbKurs(1)}
                            .firstOrNull()?.rate ?: getString(R.string.no_data_label)
                        binding.nbrbRateCard.rate.text =
                            Html.fromHtml(getString(R.string.last_value_for_no_internet, lastValue))
                    }
                } else
                    binding.nbrbRateCard.rate.text = newValue
            })

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
        if (checkInternet(requireContext()) != true) {
            currency.value = getString(R.string.no_internet_message)
            currencyNB.value = getString(R.string.no_internet_message)
            showSnackBar.value = true
            return
        }

        lifecycleScope.launch {
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
        ).apply {
            calendar.set(2000, 0, 1)
            datePicker.minDate = calendar.timeInMillis
            datePicker.maxDate = Date().time
        }
    }

    private fun getNewGoalDialog(): AlertDialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogBinding = DialogCreateGoalBinding.inflate(inflater, null, false)
            builder.setView(dialogBinding.root)

            builder
                .setPositiveButton(
                    getString(R.string.ok_dialog_label)
                ) { dialog, id ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        // проверить перекрытие текущих целей показать снэк "уже есть похожая цель - заменить?"
                        // сделать в настройках переход на системные для отключения пушей
                        // отображать статус целей как-то

                        val selectedTrendInt =
                            if (dialogBinding.selectTrend.selectedItem.toString() == getString(
                                    R.string.expensive_label
                                )
                            ) 1
                            else -1

                        val selectedBank = if (dialogBinding.selectBank.selectedItem.toString() == getString(
                                R.string.NB
                            )
                        ) getString(R.string.NB_non_locale)
                        else getString(R.string.ALFA_non_locale)

                        val goals = withContext(Dispatchers.IO) {
                            getDatabase(requireContext()).ratesGoalDatabaseDao.getRatesGoalsOneTime()
                        }

                        val goalWithTheSameType =
                            goals.firstOrNull() { goal -> goal.bank == selectedBank && goal.trend == selectedTrendInt }

                        if (goalWithTheSameType != null) {
                            Snackbar.make(
                                binding.root,
                                R.string.replace_goal,
                                Snackbar.LENGTH_SHORT
                            ).show()
                            getDatabase(requireContext()).ratesGoalDatabaseDao.insertRatesGoal(
                                RatesGoal(
                                    id = goalWithTheSameType.id,
                                    bank = selectedBank,
                                    trend = selectedTrendInt,
                                    rate = dialogBinding.goalEdit.editText?.text.toString()
                                )
                            )
                        } else {
                            getDatabase(requireContext()).ratesGoalDatabaseDao.insertRatesGoal(
                                RatesGoal(
                                    bank = selectedBank,
                                    trend = selectedTrendInt,
                                    rate = dialogBinding.goalEdit.editText?.text.toString()
                                )
                            )
                        }
                    }
                }
                .setNegativeButton(
                    getString(R.string.cancel_dialog_label)
                ) { dialog, id ->
                }
            builder.create().apply {
                setOnShowListener {
                    getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
                }
                dialogBinding.goalEdit.editText?.doAfterTextChanged { text ->
                    getButton(DialogInterface.BUTTON_POSITIVE).isEnabled =
                        text != null && text.length in 1..6
                }
                dialogBinding.selectTrend.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            dialogBinding.goalEdit.hint = if (parent?.getItemAtPosition(position)
                                    .toString() == getString(R.string.expensive_label)
                            ) getString(
                                R.string.notification_goal_hint,
                                getString(R.string.expensive_label)
                            ) else
                                getString(
                                    R.string.notification_goal_hint,
                                    getString(R.string.cheap_label)
                                )
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}