package com.cozyfitness.data.local.dao

import androidx.room.*
import com.cozyfitness.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workoutplan")
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlan>>

    @Query("SELECT * FROM workoutplan WHERE id = :id")
    suspend fun getWorkoutPlanById(id: String): WorkoutPlan?

    @Query("SELECT * FROM workoutplan WHERE isActive = 1 LIMIT 1")
    fun getActiveWorkoutPlan(): Flow<WorkoutPlan?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(workoutPlan: WorkoutPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workoutPlans: List<WorkoutPlan>)

    @Update
    suspend fun updateWorkoutPlan(workoutPlan: WorkoutPlan)

    @Delete
    suspend fun deleteWorkoutPlan(workoutPlan: WorkoutPlan)

    @Query("UPDATE workoutplan SET isActive = 0")
    suspend fun deactivateAllPlans()

    @Query("UPDATE workoutplan SET isActive = 1 WHERE id = :planId")
    suspend fun activatePlan(planId: String)
}