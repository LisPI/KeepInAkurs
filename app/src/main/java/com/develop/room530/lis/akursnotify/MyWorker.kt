package com.develop.room530.lis.akursnotify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.develop.room530.lis.akursnotify.data.database.RatesGoal
import com.develop.room530.lis.akursnotify.data.database.getDatabase
import com.develop.room530.lis.akursnotify.data.database.saveRatesInDb
import com.develop.room530.lis.akursnotify.data.network.AlfaApi
import com.develop.room530.lis.akursnotify.data.network.NbrbApi
import com.develop.room530.lis.akursnotify.features.settings.DEFAULT_PUSH_SETTINGS
import com.develop.room530.lis.akursnotify.features.settings.PrefsKeys
import com.develop.room530.lis.akursnotify.features.settings.dataStore
import com.develop.room530.lis.akursnotify.model.mapFromDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MyWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "MyWorker"
    }

    override suspend fun doWork(): Result {

        val akursRates = AlfaApi.getAkursRatesOnDateImpl()
        val nbrbRates = NbrbApi.getUsdRateHistoryImpl()

        saveRatesInDb(appContext, akursRates, nbrbRates)

        val lastAkursRate = withContext(Dispatchers.IO) {
            getDatabase(appContext).akursDatabaseDao.getLastAkurs(1).map { mapFromDb(it) }
        }.firstOrNull()

        val lastNbrbRate = withContext(Dispatchers.IO) {
            getDatabase(appContext).nbrbDatabaseDao.getLastNbrbKurs(1).map { mapFromDb(it) }
        }.firstOrNull()

        val pushEnabled = runBlocking { appContext.dataStore.data.first()[PrefsKeys.PUSH] ?: DEFAULT_PUSH_SETTINGS }
        if (pushEnabled) {

            val goals = withContext(Dispatchers.IO) {
                getDatabase(appContext).ratesGoalDatabaseDao.getRatesGoalsOneTime()
            }

            Log.d("worker for goals", "$goals")

            fun StringBuilder.appendIfNeeded(goal: RatesGoal, rate: Float, trendString: String) {

                val bank = if (goal.bank == appContext.getString(
                        R.string.NB_non_locale
                    )
                ) appContext.getString(R.string.NB)
                else appContext.getString(R.string.Akurs)

                if ((-goal.rate.toFloat() + rate) * goal.trend > 0)
                    appendLine(
                        appContext.getString(
                            R.string.goal_reached,
                            bank,
                            trendString,
                            goal.rate
                        )
                    )
            }

            val notificationText = StringBuilder()
            goals.forEach { goal ->
                val trendString =
                    if (goal.trend == -1) appContext.getString(R.string.cheap_label)
                    else appContext.getString(R.string.expensive_label)

                if (goal.bank == appContext.getString(R.string.NB_non_locale)) {
                    lastNbrbRate?.let {
                        notificationText.appendIfNeeded(goal, it.rate, trendString)
                    }
                }
                if (goal.bank == appContext.getString(R.string.ALFA_non_locale)) {
                    lastAkursRate?.let {
                        notificationText.appendIfNeeded(goal, it.rate, trendString)
                    }
                }
            }
            if (notificationText.isNotEmpty())
                sendNotification(notificationText.toString())
        }

        return Result.success()
    }

    private fun sendNotification(notificationText: String) {
        val intent = Intent(appContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            appContext, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = appContext.getString(R.string.channelId)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
            .setContentTitle(appContext.getString(R.string.interestRate))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationText)
            )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelId,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}