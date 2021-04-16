package com.develop.room530.lis.akursnotify

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.flow.first

private const val SETTINGS_NAME = "user_preferences"
val Context.dataStore by preferencesDataStore(name = SETTINGS_NAME)

object PrefsKeys{
    val PUSH = booleanPreferencesKey("push_key")
}

class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private lateinit var pushSwitch: SwitchMaterial

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pushSwitch = view.findViewById(R.id.switch1)

        lifecycleScope.launchWhenCreated {
            pushSwitch.isChecked = requireActivity().dataStore.data.first()[PrefsKeys.PUSH] ?: false
        }

        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launchWhenCreated {
                requireActivity().dataStore.edit { preferences ->
                    preferences[PrefsKeys.PUSH] = isChecked
                }
            }
        }
    }
}