package com.develop.room530.lis.akursnotify.database

import android.content.Context
import androidx.room.*


private lateinit var INSTANCE: CurrencyDatabase
fun getDatabase(context: Context): CurrencyDatabase {
    synchronized(CurrencyDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                CurrencyDatabase::class.java,
                "currency_database").build()
        }
    }
    return INSTANCE
}

@Database(
    entities = [Akurs::class, Nbrbkurs::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract val akursDatabaseDao: AkursDao
    abstract val nbrbDatabaseDao: NbrbDao
}

@Entity(tableName = "akurs")
data class Akurs(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val kurs: String,
    val date: String
)

@Entity(tableName = "nbrbkurs")
data class Nbrbkurs(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val kurs: String,
    val date: String
)

@Dao
interface AkursDao{
    @Query("select * from akurs")
    fun getAkurs(): List<Akurs>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAkurs(akurs: Akurs)

    @Query("DELETE FROM akurs")
    suspend fun deleteAll()
}

@Dao
interface NbrbDao{
    @Query("select * from nbrbkurs")
    fun getNbrbkurs(): List<Nbrbkurs> // TODO live data

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNbrbkurs(vararg nbrbkurs: Nbrbkurs)

    @Query("DELETE FROM nbrbkurs")
    suspend fun deleteAll()
}