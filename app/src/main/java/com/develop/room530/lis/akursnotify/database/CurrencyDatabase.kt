package com.develop.room530.lis.akursnotify.database

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

@Dao
interface AkursDao {
    @Query("select * from akurs")
    fun getAkurs(): List<Akurs>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAkurs(akurs: Akurs)

    @Query("DELETE FROM akurs")
    suspend fun deleteAll()
}

@Dao
interface NbrbDao {
    @Query("select * from nbrbkurs")
    fun getNbrbkurs(): List<Nbrbkurs> // TODO live data

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNbrbkurs(vararg nbrbkurs: Nbrbkurs)

    @Query("DELETE FROM nbrbkurs")
    suspend fun deleteAll()
}