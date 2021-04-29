package com.develop.room530.lis.akursnotify

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.work.*
import com.develop.room530.lis.akursnotify.features.settings.DEFAULT_THEME_SETTINGS
import com.develop.room530.lis.akursnotify.features.settings.PrefsKeys
import com.develop.room530.lis.akursnotify.features.settings.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class AkursApp : MultiDexApplication() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()
        delayedInit()
        changeThemeIfNeeded()
        Log.d("app", "onCreate")
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun changeThemeIfNeeded() {
        runBlocking {
            val isNightTheme =
                dataStore.data.first()[PrefsKeys.NIGHT_THEME] ?: DEFAULT_THEME_SETTINGS
            if (isNightTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private suspend fun setupRecurringWork() {

        val currentWorkInterval = dataStore.data.first()[PrefsKeys.WORK_INTERVAL]
        if (currentWorkInterval == 0F)
            return

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
//            .apply {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    setRequiresDeviceIdle(true)
//                }
//            }
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            MyWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}