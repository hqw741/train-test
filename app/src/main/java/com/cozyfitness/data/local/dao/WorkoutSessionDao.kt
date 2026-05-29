package com.cozyfitness.data.local.dao

import androidx.room.*
import com.cozyfitness.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workoutsession ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workoutsession WHERE id = :id")
    suspend fun getSessionById(id: String): WorkoutSession?

    @Query("SELECT * FROM workoutsession WHERE status = 'IN_PROGRESS' LIMIT 1")
    suspend fun getInProgressSession(): WorkoutSession?

    @Query("SELECT * FROM workoutsession WHERE startedAt >= :startTime ORDER BY startedAt DESC")
    fun getSessionsSince(startTime: Long): Flow<List<WorkoutSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession)

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)

    @Query("UPDATE workoutsession SET status = :status, completedAt = :completedAt WHERE id = :sessionId")
    suspend fun completeSession(sessionId: String, status: String, completedAt: Long?)
}