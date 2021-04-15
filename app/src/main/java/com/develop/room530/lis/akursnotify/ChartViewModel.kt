package com.develop.room530.lis.akursnotify

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.develop.room530.lis.akursnotify.data.database.getDatabase
import com.develop.room530.lis.akursnotify.model.mapFromDb

class ChartViewModel(application: Application) : AndroidViewModel(application) {
    fun updateRatesForChart(index: Int) {
        chartState.value = index
    }

    val chartState = MutableLiveData<Int>(0)

    val alfaRates =
        Transformations.switchMap(chartState) {
            when(it){
                1 -> Transformations.map(getDatabase(getApplication()).akursDatabaseDao.getAkurs()){it.filter { it.date > getDateWithOffset(-3) }}
                2 -> Transformations.map(getDatabase(getApplication()).akursDatabaseDao.getAkurs()){it.filter { it.date > getDateWithOffset(-10) }}
                3 -> Transformations.map(getDatabase(getApplication()).akursDatabaseDao.getAkurs()){it.filter { it.date > getDateWithOffset(-30) }}
                else -> getDatabase(getApplication()).akursDatabaseDao.getAkurs()
            }
        }

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