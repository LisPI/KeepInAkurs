package com.develop.room530.lis.akursnotify.features.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.develop.room530.lis.akursnotify.MyWorker
import com.develop.room530.lis.akursnotify.R
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

private const val SETTINGS_NAME = "user_preferences"
const val DEFAULT_WORK_INTERVAL = 4F
const val DEFAULT_PUSH_SETTINGS = false
const val DEFAULT_THEME_SETTINGS = false
val Context.dataStore by preferencesDataStore(name = SETTINGS_NAME)

object PrefsKeys {
    val PUSH = booleanPreferencesKey("push_key")
    val NIGHT_THEME = booleanPreferencesKey("night_theme_key")
    val WORK_INTERVAL = floatPreferencesKey("slider_value")
    val PUSH_RATE = floatPreferencesKey("push_rate")
}

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var pushSwitch: SwitchMaterial
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var frequencySlider: Slider
    private lateinit var pushPanel: TextInputLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pushSwitch = view.findViewById(R.id.switch_notification)
        themeSwitch = view.findViewById(R.id.switch_night_theme)
        frequencySlider = view.findViewById(R.id.frequency_slider)
        pushPanel = view.findViewById(R.id.push_goals)

        pushPanel.isEndIconVisible = false

        lifecycleScope.launchWhenCreated {
            pushSwitch.isChecked =
                requireActivity().dataStore.data.first()[PrefsKeys.PUSH] ?: DEFAULT_PUSH_SETTINGS
            themeSwitch.isChecked =
                requireActivity().dataStore.data.first()[PrefsKeys.NIGHT_THEME] ?: DEFAULT_THEME_SETTINGS
            if (!pushSwitch.isChecked)
                pushPanel.visibility = View.GONE
            frequencySlider.value =
                requireActivity().dataStore.data.first()[PrefsKeys.WORK_INTERVAL]
                    ?: DEFAULT_WORK_INTERVAL

            pushPanel.editText?.setText(
                requireActivity().dataStore.data.first()[PrefsKeys.PUSH_RATE].toString()
            )

        }

        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                pushPanel.visibility = View.GONE
            } else {
                pushPanel.visibility = View.VISIBLE
            }
            lifecycleScope.launchWhenCreated {
                requireActivity().dataStore.edit { preferences ->
                    preferences[PrefsKeys.PUSH] = isChecked
                }
            }
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            lifecycleScope.launchWhenCreated {
                requireActivity().dataStore.edit { preferences ->
                    preferences[PrefsKeys.NIGHT_THEME] = isChecked
                }
            }
        }

        frequencySlider.setLabelFormatter { value: Float ->
            when (value) {
                0F -> getString(R.string.slider_0_label)
                1F -> getString(R.string.slider_1_label)
                2F -> getString(R.string.slider_2_label)
                3F -> getString(R.string.slider_3_label)
                4F -> getString(R.string.slider_4_label)
                else -> getString(R.string.slider_5_label)
            }
        }

        pushPanel.editText?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // TODO validate input, save in datastore, change view state
                v.clearFocus()
                lifecycleScope.launchWhenCreated {
                    requireActivity().dataStore.edit { preferences ->
                        preferences[PrefsKeys.PUSH_RATE] =
                            v.text.toString().toFloatOrNull() ?: 0f // FIXME
                    }
                    pushPanel.isEndIconVisible = true
                }
            }
            false
        }

        frequencySlider.addOnChangeListener { rangeSlider, value, fromUser ->
            if (fromUser) {
                lifecycleScope.launchWhenCreated {
                    requireActivity().dataStore.edit { preferences ->
                        preferences[PrefsKeys.WORK_INTERVAL] = value
                    }
                }
                updatePeriodicWork(requireContext(), value) // FIXME update only when change
            }
        }
    }

    private fun updatePeriodicWork(context: Context, newInterval: Float) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
//            .apply {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    setRequiresDeviceIdle(true)
//                }
//            }
            .build()

        if (newInterval == 0F) {
            WorkManager.getInstance(context).cancelUniqueWork(MyWorker.WORK_NAME)
            return
        }

        val repeatingRequest =
            when (newInterval) {
                1F -> PeriodicWorkRequestBuilder<MyWorker>(7, TimeUnit.DAYS)
                2F -> PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.DAYS)
                3F -> PeriodicWorkRequestBuilder<MyWorker>(4, TimeUnit.HOURS)
                4F -> PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.HOURS)
                else -> PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.MINUTES)
            }
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.SECONDS) // FIXME for debug
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MyWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }
}