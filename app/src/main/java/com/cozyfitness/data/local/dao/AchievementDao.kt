package com.cozyfitness.data.local.dao

import androidx.room.*
import com.cozyfitness.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievement WHERE id = :id")
    suspend fun getAchievementById(id: String): Achievement?

    @Query("SELECT * FROM achievement WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievement WHERE isUnlocked = 0")
    fun getLockedAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<Achievement>)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("UPDATE achievement SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: String, unlockedAt: Long)
}