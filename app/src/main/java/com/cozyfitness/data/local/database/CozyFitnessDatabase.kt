package com.cozyfitness.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cozyfitness.data.local.Converters
import com.cozyfitness.data.local.dao.*
import com.cozyfitness.domain.model.*

@Database(
    entities = [
        User::class,
        WorkoutPlan::class,
        Exercise::class,
        WorkoutSession::class,
        DailyStats::class,
        Achievement::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CozyFitnessDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun achievementDao(): AchievementDao
}