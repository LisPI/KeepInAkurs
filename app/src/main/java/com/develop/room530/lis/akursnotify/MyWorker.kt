package com.develop.room530.lis.akursnotify

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.develop.room530.lis.akursnotify.database.Akurs
import com.develop.room530.lis.akursnotify.database.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.*

class MyWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "MyWorker"
    }

    override suspend fun doWork(): Result {

        val doc = Jsoup.connect("https://www.alfabank.by/services/a-kurs/").get()
        val currency = doc.select(".curr-table tr:first-of-type td:first-of-type").text()

        withContext(Dispatchers.IO) {
            getDatabase(appContext).akursDatabaseDao.insertAkurs(
                akurs = Akurs(
                    kurs = currency,
                    date = Date().toString()
                )
            )
        }

        Log.d("currency", currency)

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}