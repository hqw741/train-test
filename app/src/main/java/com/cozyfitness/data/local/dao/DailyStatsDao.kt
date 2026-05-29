package com.cozyfitness.data.local.dao

import androidx.room.*
import com.cozyfitness.domain.model.DailyStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM dailystats WHERE date = :date")
    fun getDailyStatsByDate(date: String): Flow<DailyStats?>

    @Query("SELECT * FROM dailystats WHERE date = :date")
    suspend fun getDailyStatsByDateOnce(date: String): DailyStats?

    @Query("SELECT * FROM dailystats WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getStatsInRange(startDate: String, endDate: String): Flow<List<DailyStats>>

    @Query("SELECT * FROM dailystats ORDER BY date DESC LIMIT :limit")
    fun getRecentStats(limit: Int): Flow<List<DailyStats>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dailyStats: DailyStats)

    @Update
    suspend fun updateStats(dailyStats: DailyStats)

    @Query("UPDATE dailystats SET steps = steps + :steps WHERE date = :date")
    suspend fun addSteps(date: String, steps: Int)

    @Query("UPDATE dailystats SET caloriesBurned = caloriesBurned + :calories WHERE date = :date")
    suspend fun addCalories(date: String, calories: Int)

    @Query("UPDATE dailystats SET activeMinutes = activeMinutes + :minutes WHERE date = :date")
    suspend fun addActiveMinutes(date: String, minutes: Int)
}