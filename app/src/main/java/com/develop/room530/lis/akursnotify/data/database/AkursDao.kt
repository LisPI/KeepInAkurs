package com.develop.room530.lis.akursnotify.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AkursDao {
    @Query("select * from akurs")
    fun getAkurs(): LiveData<List<Akurs>>

    @Query("select * from akurs order by date desc limit :limit")
    fun getLastAkurs(limit: Int = 1): List<Akurs>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAkurs(akurs: Akurs)

    @Query("DELETE FROM akurs")
    suspend fun deleteAll()
}