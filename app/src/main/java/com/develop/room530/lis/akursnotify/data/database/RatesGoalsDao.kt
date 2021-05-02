package com.develop.room530.lis.akursnotify.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RatesGoalsDao {
    @Query("select * from ratesgoal")
    fun getRatesGoals(): LiveData<List<RatesGoal>>

    @Query("select * from ratesgoal")
    fun getRatesGoalsOneTime(): List<RatesGoal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRatesGoal(vararg ratesGoal: RatesGoal)

    @Query("DELETE FROM ratesgoal")
    suspend fun deleteAll()

    @Query("DELETE FROM ratesgoal where id = :goalId")
    suspend fun deleteGoal(goalId: Int)
}