package com.develop.room530.lis.akursnotify.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// TODO save date of change data)
@Entity(tableName = "akurs")
data class Akurs(
    override val rate: String,
    @PrimaryKey
    override val date: Date,
    val time: String,
): RateEntity()

@Entity(tableName = "nbrbkurs")
data class Nbrbkurs(
    override val rate: String,
    @PrimaryKey
    override val date: Date
): RateEntity()

sealed class RateEntity {
    abstract val rate: String
    abstract val date: Date
}