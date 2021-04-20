package com.develop.room530.lis.akursnotify

import androidx.multidex.MultiDexApplication
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AkursApp : MultiDexApplication() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private suspend fun setupRecurringWork() {

        // FIXME nice try)
//        val oldWork = WorkManager.getInstance(applicationContext)
//            .getWorkInfosForUniqueWork(MyWorker.WORK_NAME).get()
//        if (oldWork.size > 0 && oldWork[0].state == WorkInfo.State.CANCELLED)
//            return

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