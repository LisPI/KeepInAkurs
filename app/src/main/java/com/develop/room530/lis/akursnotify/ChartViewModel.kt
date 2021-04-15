package com.develop.room530.lis.akursnotify

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.develop.room530.lis.akursnotify.data.database.getDatabase
import com.develop.room530.lis.akursnotify.model.mapFromDb

class ChartViewModel(application: Application) : AndroidViewModel(application) {

    val alfaRates =
        Transformations.map(
            getDatabase(getApplication()).akursDatabaseDao.getAkurs()
        ) { it.map { rate -> mapFromDb(rate) } }

    val nbrbRates = //MockRatesData.liveNbRb
        Transformations.map(
            getDatabase(getApplication()).nbrbDatabaseDao.getNbrbkurs()
        ) { it.map { rate -> mapFromDb(rate) } }

    val rates = MediatorLiveData<Boolean>()

    init {
        //MockRatesData.newNbRb2()

        rates.addSource(alfaRates) {
            nbrbRates.value?.let {
                rates.value = true
            }
        }
        rates.addSource(nbrbRates) {
            alfaRates.value?.let {
                rates.value = true
            }
        }
    }
}