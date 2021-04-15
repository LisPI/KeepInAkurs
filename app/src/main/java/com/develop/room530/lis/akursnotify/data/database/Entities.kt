package com.develop.room530.lis.akursnotify.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// TODO save date of change data)
@Entity(tableName = "akurs")
data class Akurs(
    val rate: String,
    @PrimaryKey
    val date: Date,
    val time: String,
)

@Entity(tableName = "nbrbkurs")
data class Nbrbkurs(
    val rate: String,
    @PrimaryKey
    val date: Date
)