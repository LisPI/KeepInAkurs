package com.develop.room530.lis.akursnotify

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.jsoup.Jsoup
import kotlin.random.Random

class MyWorker(private val appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "MyWorker"
    }

    override suspend fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        val doc = Jsoup.connect("https://www.alfabank.by/services/a-kurs/").get()
        val currency = doc.select(".curr-table tr:first-of-type td:first-of-type").text()

        val sh = appContext.getSharedPreferences("1", Context.MODE_PRIVATE)
        with(sh.edit()) {
            putString("cur" + Random.nextInt(1,100).toString(), currency)
            apply()
        }

        Log.d("1", currency)

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}