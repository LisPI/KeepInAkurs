package com.develop.room530.lis.akursnotify

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.develop.room530.lis.akursnotify.database.Akurs
import com.develop.room530.lis.akursnotify.database.getDatabase
import com.develop.room530.lis.akursnotify.network.AlfaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class MyWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "MyWorker"
    }

    override suspend fun doWork(): Result {

        val rates = AlfaApi.getAkursRatesOnDateImpl()

        withContext(Dispatchers.IO) {
            getDatabase(appContext).akursDatabaseDao.insertAkurs(
                akurs = Akurs(
                    kurs = rates.first().price,
                    date = Date().toString()
                )
            )
        }

        Log.d("currency", rates.first().price)

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}