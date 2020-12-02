package com.develop.room530.lis.akursnotify

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.develop.room530.lis.akursnotify.database.Akurs
import com.develop.room530.lis.akursnotify.database.Nbrbkurs
import com.develop.room530.lis.akursnotify.database.getDatabase
import com.develop.room530.lis.akursnotify.network.AlfaApi
import com.develop.room530.lis.akursnotify.network.NbrbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "MyWorker"
    }

    override suspend fun doWork(): Result {

        val akursRates = AlfaApi.getAkursRatesOnDateImpl()
        val nbrbRates = NbrbApi.getUsdRateHistoryImpl()

        withContext(Dispatchers.IO) {
            for (rate in akursRates) {
                getDatabase(appContext).akursDatabaseDao.insertAkurs(
                    akurs = Akurs(
                        rate = rate.price,
                        date = rate.date,
                        time = rate.time,
                    )
                )
            }
            for (rate in nbrbRates) {
                getDatabase(appContext).nbrbDatabaseDao.insertNbrbkurs(
                    Nbrbkurs(
                        rate.price.toString(),
                        rate.date
                    )
                )
            }
        }

        Log.d("nbrb currency", nbrbRates[0].price.toString())

        return Result.success()
    }
}