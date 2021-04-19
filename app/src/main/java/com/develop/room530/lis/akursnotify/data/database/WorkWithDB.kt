package com.develop.room530.lis.akursnotify.data.database

import android.content.Context
import com.develop.room530.lis.akursnotify.data.network.AlfaAkursRate
import com.develop.room530.lis.akursnotify.data.network.NbrbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun saveRatesInDb(
    context: Context,
    akursRates: List<AlfaAkursRate>?,
    nbrbRates: List<NbrbModel>?
) {
    withContext(Dispatchers.IO) {
        akursRates?.forEach { rate ->
            getDatabase(context).akursDatabaseDao.insertAkurs(
                akurs = Akurs(
                    rate = rate.price,
                    date = rate.date,
                    time = rate.time,
                )
            )
        }
        nbrbRates?.forEach {
            getDatabase(context).nbrbDatabaseDao.insertNbrbkurs(
                Nbrbkurs(
                    it.price.toString(),
                    it.date
                )
            )
        }
    }
}