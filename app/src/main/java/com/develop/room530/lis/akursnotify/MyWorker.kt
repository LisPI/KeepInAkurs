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
import com.develop.room530.lis.akursnotify.data.database.saveRatesInDb
import com.develop.room530.lis.akursnotify.data.network.AlfaApi
import com.develop.room530.lis.akursnotify.data.network.NbrbApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MyWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "MyWorker"
    }

    override suspend fun doWork(): Result {

        val akursRates = AlfaApi.getAkursRatesOnDateImpl()
        val nbrbRates = NbrbApi.getUsdRateHistoryImpl()

        saveRatesInDb(appContext, akursRates, nbrbRates)

        Log.d("nbrb currency", nbrbRates.lastOrNull()?.price.toString())
        val pushEnabled = runBlocking { appContext.dataStore.data.first()[PrefsKeys.PUSH] }
        if (pushEnabled == true) {
            sendNotification(nbrbRates.lastOrNull()?.price.toString())
        }
        Log.d("push enabled", pushEnabled.toString())

        return Result.success()
    }

    private fun sendNotification(rate: String) {
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
            .setContentText(rate)
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