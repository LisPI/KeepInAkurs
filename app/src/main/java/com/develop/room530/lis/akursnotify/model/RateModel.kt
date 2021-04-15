package com.develop.room530.lis.akursnotify.model

import com.develop.room530.lis.akursnotify.data.database.Akurs
import com.develop.room530.lis.akursnotify.data.database.Nbrbkurs
import com.develop.room530.lis.akursnotify.data.database.RateEntity
import com.develop.room530.lis.akursnotify.getDateDDMMFormat
import com.develop.room530.lis.akursnotify.getDateHHMMDDMMFormat
import java.util.*

sealed class RateModel {
    abstract val rate: Float
    abstract val date: Date
    abstract val time: String
    abstract val dateUI: String
}

data class NbRbRateModel(
    override val rate: Float,
    override val date: Date,
    override val time: String = "",
) : RateModel() {
    override val dateUI = getDateDDMMFormat(date)
}

data class AlfaRateModel(
    override val rate: Float,
    override val date: Date,
    override val time: String,
) : RateModel() {
    override val dateUI = getDateHHMMDDMMFormat(date)
}

fun mapFromDb(dbItem: RateEntity) = when(dbItem){
    is Akurs -> AlfaRateModel(rate = dbItem.rate.toFloatOrNull() ?: 0.0F, date = dbItem.date, time = dbItem.time)
    is Nbrbkurs -> NbRbRateModel(rate = dbItem.rate.toFloatOrNull() ?: 0.0F, date = dbItem.date)
}