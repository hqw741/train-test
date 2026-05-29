package com.cozyfitness.domain.model

data class User(
    val id: String = "",
    val name: String = "Alex",
    val avatarUri: String? = null,
    val dailyStepGoal: Int = 10000,
    val dailyCalorieGoal: Int = 500,
    val dailyActiveMinutesGoal: Int = 30,
    val preferredUnit: UnitSystem = UnitSystem.METRIC
)

enum class UnitSystem {
    METRIC, IMPERIAL
}

data class WorkoutPlan(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val coverImageUrl: String? = null,
    val difficulty: Difficulty = Difficulty.BEGINNER,
    val estimatedDurationMinutes: Int = 0,
    val estimatedCalories: Int = 0,
    val exercises: List<Exercise> = emptyList(),
    val isActive: Boolean = false
)

enum class Difficulty {
    BEGINNER, INTERMEDIATE, ADVANCED
}

data class Exercise(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val type: ExerciseType = ExerciseType.CARDIO,
    val durationSeconds: Int = 0,
    val repetitions: Int? = null,
    val sets: Int? = null,
    val caloriesPerRep: Int? = null,
    val restSeconds: Int = 30,
    val mediaUrl: String? = null
)

enum class ExerciseType {
    CARDIO, STRENGTH, FLEXIBILITY, HIIT
}

data class WorkoutSession(
    val id: String = "",
    val planId: String? = null,
    val startedAt: Long = 0,
    val completedAt: Long? = null,
    val totalDurationSeconds: Int = 0,
    val caloriesBurned: Int = 0,
    val averageHeartRate: Int? = null,
    val exercisesCompleted: Int = 0,
    val totalExercises: Int = 0,
    val status: WorkoutStatus = WorkoutStatus.IN_PROGRESS
)

enum class WorkoutStatus {
    IN_PROGRESS, COMPLETED, ABANDONED
}

data class DailyStats(
    val id: String = "",
    val date: String = "",
    val steps: Int = 0,
    val activeMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val averageHeartRate: Int? = null,
    val workoutsCompleted: Int = 0,
    val distanceKm: Float? = null
)

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconName: String = "",
    val unlockedAt: Long? = null,
    val criteriaType: AchievementType = AchievementType.WORKOUTS,
    val criteriaValue: Int = 0,
    val isUnlocked: Boolean = false
)

enum class AchievementType {
    STEPS, WORKOUTS, CALORIES, DAYS_STREAK, TOTAL_MINUTES
}