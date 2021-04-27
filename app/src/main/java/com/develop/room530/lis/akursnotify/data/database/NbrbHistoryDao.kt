package com.develop.room530.lis.akursnotify.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NbrbHistoryDao {
    @Query("select * from nbrbhistory")
    fun getNbrbHistory(): LiveData<List<NbrbHistory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNbrbHistory(vararg nbrbhistory: NbrbHistory)

    @Query("DELETE FROM nbrbhistory")
    suspend fun deleteAll()
}