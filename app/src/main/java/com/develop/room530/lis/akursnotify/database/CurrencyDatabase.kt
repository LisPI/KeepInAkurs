package com.develop.room530.lis.akursnotify.database

import android.content.Context
import androidx.lifecycle.LiveData
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

@Dao
interface AkursDao {
    @Query("select * from akurs")
    fun getAkurs(): LiveData<List<Akurs>>

    @Query("select * from akurs order by date desc limit 1")
    fun getLastAkurs(): Akurs?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAkurs(akurs: Akurs)

    @Query("DELETE FROM akurs")
    suspend fun deleteAll()
}

@Dao
interface NbrbDao {
    @Query("select * from nbrbkurs")
    fun getNbrbkurs(): LiveData<List<Nbrbkurs>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNbrbkurs(vararg nbrbkurs: Nbrbkurs)

    @Query("DELETE FROM nbrbkurs")
    suspend fun deleteAll()
}