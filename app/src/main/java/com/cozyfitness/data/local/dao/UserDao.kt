package com.cozyfitness.data.local.dao

import androidx.room.*
import com.cozyfitness.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<User?>

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUserOnce(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE user SET dailyStepGoal = :stepGoal WHERE id = :userId")
    suspend fun updateStepGoal(userId: String, stepGoal: Int)

    @Query("UPDATE user SET dailyCalorieGoal = :calorieGoal WHERE id = :userId")
    suspend fun updateCalorieGoal(userId: String, calorieGoal: Int)

    @Query("UPDATE user SET dailyActiveMinutesGoal = :minutesGoal WHERE id = :userId")
    suspend fun updateActiveMinutesGoal(userId: String, minutesGoal: Int)

    @Query("UPDATE user SET preferredUnit = :unit WHERE id = :userId")
    suspend fun updatePreferredUnit(userId: String, unit: String)
}