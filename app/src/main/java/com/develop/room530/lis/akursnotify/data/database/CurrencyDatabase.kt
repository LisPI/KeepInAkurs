package com.develop.room530.lis.akursnotify.data.database

import android.content.Context
import androidx.room.*
import java.util.*

private lateinit var INSTANCE: CurrencyDatabase
fun getDatabase(context: Context): CurrencyDatabase {
    synchronized(CurrencyDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                CurrencyDatabase::class.java,
                "currency_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}

@Database(
    entities = [Akurs::class, Nbrbkurs::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract val akursDatabaseDao: AkursDao
    abstract val nbrbDatabaseDao: NbrbDao
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}