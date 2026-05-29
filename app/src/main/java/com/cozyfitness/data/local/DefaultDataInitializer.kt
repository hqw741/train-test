package com.cozyfitness.data.local

import com.cozyfitness.data.local.dao.*
import com.cozyfitness.domain.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDataInitializer @Inject constructor(
    private val database: CozyFitnessDatabase
) {
    fun initializeIfNeeded(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val existingUser = database.userDao().getUserOnce()
            if (existingUser == null) {
                insertDefaultUser()
            }
            insertDefaultWorkoutPlans()
            insertDefaultAchievements()
        }
    }

    private suspend fun insertDefaultUser() {
        val defaultUser = User(
            id = UUID.randomUUID().toString(),
            name = "Alex",
            dailyStepGoal = 10000,
            dailyCalorieGoal = 500,
            dailyActiveMinutesGoal = 30,
            preferredUnit = UnitSystem.METRIC
        )
        database.userDao().insertUser(defaultUser)
    }

    private suspend fun insertDefaultWorkoutPlans() {
        val morningCardioPlan = WorkoutPlan(
            id = UUID.randomUUID().toString(),
            title = "晨间有氧",
            description = "轻松的有氧训练，适合早晨进行",
            difficulty = Difficulty.BEGINNER,
            estimatedDurationMinutes = 25,
            estimatedCalories = 180,
            isActive = true
        )
        database.workoutPlanDao().insertWorkoutPlan(morningCardioPlan)

        // Add exercises for morning cardio
        val exercises = listOf(
            Exercise(
                id = UUID.randomUUID().toString(),
                name = "热身拉伸",
                description = "全身热身拉伸",
                type = ExerciseType.FLEXIBILITY,
                durationSeconds = 180,
                restSeconds = 0,
                planId = morningCardioPlan.id
            ),
            Exercise(
                id = UUID.randomUUID().toString(),
                name = "慢跑",
                description = "中等速度慢跑",
                type = ExerciseType.CARDIO,
                durationSeconds = 600,
                restSeconds = 60,
                planId = morningCardioPlan.id
            ),
            Exercise(
                id = UUID.randomUUID().toString(),
                name = "高抬腿",
                description = "激活下肢",
                type = ExerciseType.HIIT,
                durationSeconds = 120,
                restSeconds = 30,
                planId = morningCardioPlan.id
            ),
            Exercise(
                id = UUID.randomUUID().toString(),
                name = "放松整理",
                description = "拉伸放松",
                type = ExerciseType.FLEXIBILITY,
                durationSeconds = 180,
                restSeconds = 0,
                planId = morningCardioPlan.id
            )
        )
        database.exerciseDao().insertAll(exercises)
    }

    private suspend fun insertDefaultAchievements() {
        val achievements = listOf(
            Achievement(
                id = UUID.randomUUID().toString(),
                title = "初次训练",
                description = "完成第一次训练",
                iconName = "fitness_center",
                criteriaType = AchievementType.WORKOUTS,
                criteriaValue = 1
            ),
            Achievement(
                id = UUID.randomUUID().toString(),
                title = "训练达人",
                description = "累计完成10次训练",
                iconName = "emoji_events",
                criteriaType = AchievementType.WORKOUTS,
                criteriaValue = 10
            ),
            Achievement(
                id = UUID.randomUUID().toString(),
                title = "燃烧吧",
                description = "累计消耗1000卡路里",
                iconName = "local_fire_department",
                criteriaType = AchievementType.CALORIES,
                criteriaValue = 1000
            ),
            Achievement(
                id = UUID.randomUUID().toString(),
                title = "步行者",
                description = "累计步数达到10000步",
                iconName = "directions_walk",
                criteriaType = AchievementType.STEPS,
                criteriaValue = 10000
            ),
            Achievement(
                id = UUID.randomUUID().toString(),
                title = "坚持不懈",
                description = "连续7天完成训练",
                iconName = "calendar_today",
                criteriaType = AchievementType.DAYS_STREAK,
                criteriaValue = 7
            )
        )
        database.achievementDao().insertAll(achievements)
    }
}