package com.cozyfitness.data.repository

import com.cozyfitness.data.local.database.CozyFitnessDatabase
import com.cozyfitness.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val database: CozyFitnessDatabase
) {
    // User operations
    fun getUser(): Flow<User?> = database.userDao().getUser()

    suspend fun getUserOnce(): User? = database.userDao().getUserOnce()

    suspend fun insertUser(user: User) = database.userDao().insertUser(user)

    suspend fun updateUser(user: User) = database.userDao().updateUser(user)

    suspend fun updateUserGoals(userId: String, stepGoal: Int? = null, calorieGoal: Int? = null, minutesGoal: Int? = null) {
        stepGoal?.let { database.userDao().updateStepGoal(userId, it) }
        calorieGoal?.let { database.userDao().updateCalorieGoal(userId, it) }
        minutesGoal?.let { database.userDao().updateActiveMinutesGoal(userId, it) }
    }

    suspend fun updatePreferredUnit(userId: String, unit: UnitSystem) {
        database.userDao().updatePreferredUnit(userId, unit.name)
    }

    // WorkoutPlan operations
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlan>> = database.workoutPlanDao().getAllWorkoutPlans()

    suspend fun getWorkoutPlanById(id: String): WorkoutPlan? = database.workoutPlanDao().getWorkoutPlanById(id)

    fun getActiveWorkoutPlan(): Flow<WorkoutPlan?> = database.workoutPlanDao().getActiveWorkoutPlan()

    suspend fun insertWorkoutPlan(plan: WorkoutPlan) = database.workoutPlanDao().insertWorkoutPlan(plan)

    suspend fun updateWorkoutPlan(plan: WorkoutPlan) = database.workoutPlanDao().updateWorkoutPlan(plan)

    suspend fun setActivePlan(planId: String) {
        database.workoutPlanDao().deactivateAllPlans()
        database.workoutPlanDao().activatePlan(planId)
    }

    // Exercise operations
    fun getExercisesByPlanId(planId: String): Flow<List<Exercise>> = database.exerciseDao().getExercisesByPlanId(planId)

    suspend fun insertExercise(exercise: Exercise) = database.exerciseDao().insertExercise(exercise)

    suspend fun insertExercises(exercises: List<Exercise>) = database.exerciseDao().insertAll(exercises)

    // WorkoutSession operations
    fun getAllSessions(): Flow<List<WorkoutSession>> = database.workoutSessionDao().getAllSessions()

    suspend fun getSessionById(id: String): WorkoutSession? = database.workoutSessionDao().getSessionById(id)

    suspend fun getInProgressSession(): WorkoutSession? = database.workoutSessionDao().getInProgressSession()

    fun getSessionsSince(startTime: Long): Flow<List<WorkoutSession>> = database.workoutSessionDao().getSessionsSince(startTime)

    suspend fun insertSession(session: WorkoutSession) = database.workoutSessionDao().insertSession(session)

    suspend fun updateSession(session: WorkoutSession) = database.workoutSessionDao().updateSession(session)

    suspend fun completeSession(sessionId: String, status: WorkoutStatus, completedAt: Long?) {
        database.workoutSessionDao().completeSession(sessionId, status.name, completedAt)
    }

    // DailyStats operations
    fun getDailyStatsByDate(date: String): Flow<DailyStats?> = database.dailyStatsDao().getDailyStatsByDate(date)

    suspend fun getDailyStatsByDateOnce(date: String): DailyStats? = database.dailyStatsDao().getDailyStatsByDateOnce(date)

    fun getStatsInRange(startDate: String, endDate: String): Flow<List<DailyStats>> =
        database.dailyStatsDao().getStatsInRange(startDate, endDate)

    fun getWeeklyStats(): Flow<List<DailyStats>> = database.dailyStatsDao().getRecentStats(7)

    suspend fun insertOrUpdateDailyStats(stats: DailyStats) = database.dailyStatsDao().insertOrUpdate(stats)

    suspend fun addSteps(date: String, steps: Int) = database.dailyStatsDao().addSteps(date, steps)

    suspend fun addCalories(date: String, calories: Int) = database.dailyStatsDao().addCalories(date, calories)

    // Achievement operations
    fun getAllAchievements(): Flow<List<Achievement>> = database.achievementDao().getAllAchievements()

    suspend fun getAchievementById(id: String): Achievement? = database.achievementDao().getAchievementById(id)

    fun getUnlockedAchievements(): Flow<List<Achievement>> = database.achievementDao().getUnlockedAchievements()

    suspend fun insertAchievement(achievement: Achievement) = database.achievementDao().insertAchievement(achievement)

    suspend fun unlockAchievement(achievementId: String) {
        database.achievementDao().unlockAchievement(achievementId, System.currentTimeMillis())
    }
}