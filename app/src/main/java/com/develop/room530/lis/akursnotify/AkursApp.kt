package com.develop.room530.lis.akursnotify

import androidx.multidex.MultiDexApplication
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AkursApp: MultiDexApplication() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)
// FIXME some problem on start app - UI is blocked
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
//            .apply {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    setRequiresDeviceIdle(true)
//                }
//            }
            .build()

        val repeatingRequest
                = PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            MyWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,  // FIXME for debug purposes
            repeatingRequest)
    }
}