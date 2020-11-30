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
        val nbrbRate = NbrbApi.service.getUsdRate()

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
            getDatabase(appContext).nbrbDatabaseDao.insertNbrbkurs(
                Nbrbkurs(
                    nbrbRate.price.toString(),
                    nbrbRate.date
                )
            )
        }

        Log.d("nbrb currency", nbrbRate.price.toString())

        return Result.success()
    }
}