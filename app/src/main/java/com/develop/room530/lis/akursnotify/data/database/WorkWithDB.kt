package com.develop.room530.lis.akursnotify.data.database

import android.content.Context
import com.develop.room530.lis.akursnotify.data.network.AlfaAkursRate
import com.develop.room530.lis.akursnotify.data.network.NbrbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun saveRatesInDb(context: Context, akursRates: List<AlfaAkursRate>, nbrb: NbrbModel?) {
    withContext(Dispatchers.IO) {
        for (rate in akursRates) {
            getDatabase(context).akursDatabaseDao.insertAkurs(
                akurs = Akurs(
                    rate = rate.price,
                    date = rate.date,
                    time = rate.time,
                )
            )
        }
        nbrb?.let {
            getDatabase(context).nbrbDatabaseDao.insertNbrbkurs(
                Nbrbkurs(
                    it.price.toString(),
                    it.date
                )
            )
        }
    }
}