package com.develop.room530.lis.akursnotify.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NbrbDao {
    @Query("select * from nbrbkurs")
    fun getNbrbkurs(): LiveData<List<Nbrbkurs>>

    @Query("select * from nbrbkurs order by date desc limit :limit")
    fun getLastNbrbKurs(limit: Int = 1): List<Nbrbkurs>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNbrbkurs(vararg nbrbkurs: Nbrbkurs)

    @Query("DELETE FROM nbrbkurs")
    suspend fun deleteAll()
}