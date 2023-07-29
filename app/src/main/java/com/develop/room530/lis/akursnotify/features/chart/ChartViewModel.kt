package com.develop.room530.lis.akursnotify.features.chart

import android.app.Application
import androidx.lifecycle.*
import com.develop.room530.lis.akursnotify.data.database.CurrencyDatabase
import com.develop.room530.lis.akursnotify.data.database.RateEntity
import com.develop.room530.lis.akursnotify.data.database.getDatabase
import com.develop.room530.lis.akursnotify.getDateWithOffset
import com.develop.room530.lis.akursnotify.model.mapFromDb

class ChartViewModel(application: Application) : AndroidViewModel(application) {
    private val chartState = MutableLiveData(1)

    private val database: CurrencyDatabase = getDatabase(getApplication())

    val alfaRatesData = getRatesData(database) { akursDatabaseDao.getAkurs() }
    val nbrbRatesData = // for tests MockRatesData.liveNbRb
        getRatesData(database) { nbrbDatabaseDao.getNbrbkurs() }

    val rates = MediatorLiveData<Boolean>()

    init {
        // for tests MockRatesData.newNbRb2()

        rates.addSource(alfaRatesData) {
            nbrbRatesData.value?.let {
                rates.value = true
            }
        }
        rates.addSource(nbrbRatesData) {
            alfaRatesData.value?.let {
                rates.value = true
            }
        }
    }

    fun updateRatesForChart(index: Int) {
        chartState.value = index
    }

    private fun <T : RateEntity> getRatesData(
        database: CurrencyDatabase,
        body: CurrencyDatabase.() -> LiveData<List<T>>
    ) =
        chartState.switchMap {
            val offset = when (it) {
                1 -> -3
                2 -> -10
                3 -> -30
                else -> - 300 // FIXME
            }
            database.body().map { rates ->
                rates
                    .filter { rate -> rate.date > getDateWithOffset(offset) }
                    .map { rate -> mapFromDb(rate) }
            }
        }
}