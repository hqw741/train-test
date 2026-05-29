# Room Database Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Room database persistence for all 6 entities (User, WorkoutPlan, Exercise, WorkoutSession, DailyStats, Achievement) with single CozyFitnessDatabase and AppRepository.

**Architecture:** Single Room database with 6 DAOs, single AppRepository for all data access, Hilt DI for dependency injection. TypeConverters for enum-to-string mapping.

**Tech Stack:** Room 2.6.1, Kotlin Coroutines, Hilt 2.50

---

## File Structure

```
app/src/main/java/com/cozyfitness/
├── data/
│   ├── local/
│   │   ├── Converters.kt              # Enum type converters
│   │   ├── CozyFitnessDatabase.kt     # Room database class
│   │   └── dao/
│   │       ├── UserDao.kt
│   │       ├── WorkoutPlanDao.kt
│   │       ├── ExerciseDao.kt
│   │       ├── WorkoutSessionDao.kt
│   │       ├── DailyStatsDao.kt
│   │       └── AchievementDao.kt
│   └── repository/
│       └── AppRepository.kt
├── di/
│   └── DatabaseModule.kt              # Hilt module for DB & Repository
└── CozyFitnessApp.kt                  # Modify: init database

domain/model/Models.kt                  # Modify: add @Entity annotations
```

---

## Task 1: Add Room Dependencies

**Files:**
- Modify: `app/build.gradle.kts:79-81`

- [ ] **Step 1: Verify Room dependencies**

Check that these lines exist in `app/build.gradle.kts`:
```kotlin
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")
```

If missing, add them in the dependencies block.

- [ ] **Step 2: Commit**

```bash
git add app/build.gradle.kts
git commit -m "chore: ensure Room dependencies present"
```

---

## Task 2: Create TypeConverters

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/Converters.kt`

- [ ] **Step 1: Write Converters.kt**

```kotlin
package com.cozyfitness.data.local

import androidx.room.TypeConverter
import com.cozyfitness.domain.model.*

class Converters {
    @TypeConverter
    fun fromDifficulty(value: Difficulty): String = value.name

    @TypeConverter
    fun toDifficulty(value: String): Difficulty = Difficulty.valueOf(value)

    @TypeConverter
    fun fromExerciseType(value: ExerciseType): String = value.name

    @TypeConverter
    fun toExerciseType(value: String): ExerciseType = ExerciseType.valueOf(value)

    @TypeConverter
    fun fromWorkoutStatus(value: WorkoutStatus): String = value.name

    @TypeConverter
    fun toWorkoutStatus(value: String): WorkoutStatus = WorkoutStatus.valueOf(value)

    @TypeConverter
    fun fromAchievementType(value: AchievementType): String = value.name

    @TypeConverter
    fun toAchievementType(value: String): AchievementType = AchievementType.valueOf(value)

    @TypeConverter
    fun fromUnitSystem(value: UnitSystem): String = value.name

    @TypeConverter
    fun toUnitSystem(value: String): UnitSystem = UnitSystem.valueOf(value)
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/Converters.kt
git commit -m "feat: add Room TypeConverters for enum types"
```

---

## Task 3: Create UserDao

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/dao/UserDao.kt`

- [ ] **Step 1: Write UserDao.kt**

```kotlin
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/dao/UserDao.kt
git commit -m "feat: add UserDao with CRUD operations"
```

---

## Task 4: Create WorkoutPlanDao

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/dao/WorkoutPlanDao.kt`

- [ ] **Step 1: Write WorkoutPlanDao.kt**

```kotlin
package com.cozyfitness.data.local.dao

import androidx.room.*
import com.cozyfitness.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workoutplan")
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlan>>>

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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/dao/WorkoutPlanDao.kt
git commit -m "feat: add WorkoutPlanDao"
```

---

## Task 5: Create ExerciseDao

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/dao/ExerciseDao.kt`

- [ ] **Step 1: Write ExerciseDao.kt**

```kotlin
package com.cozyfitness.data.local.dao

import androidx.room.*
import com.cozyfitness.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise WHERE planId = :planId")
    fun getExercisesByPlanId(planId: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getExerciseById(id: String): Exercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("DELETE FROM exercise WHERE planId = :planId")
    suspend fun deleteExercisesByPlanId(planId: String)
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/dao/ExerciseDao.kt
git commit -m "feat: add ExerciseDao"
```

---

## Task 6: Create WorkoutSessionDao

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/dao/WorkoutSessionDao.kt`

- [ ] **Step 1: Write WorkoutSessionDao.kt**

```kotlin
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/dao/WorkoutSessionDao.kt
git commit -m "feat: add WorkoutSessionDao"
```

---

## Task 7: Create DailyStatsDao

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/dao/DailyStatsDao.kt`

- [ ] **Step 1: Write DailyStatsDao.kt**

```kotlin
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/dao/DailyStatsDao.kt
git commit -m "feat: add DailyStatsDao"
```

---

## Task 8: Create AchievementDao

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/dao/AchievementDao.kt`

- [ ] **Step 1: Write AchievementDao.kt**

```kotlin
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/dao/AchievementDao.kt
git commit -m "feat: add AchievementDao"
```

---

## Task 9: Create CozyFitnessDatabase

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/CozyFitnessDatabase.kt`

- [ ] **Step 1: Write CozyFitnessDatabase.kt**

```kotlin
package com.cozyfitness.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/CozyFitnessDatabase.kt
git commit -m "feat: add CozyFitnessDatabase with all 6 entities"
```

---

## Task 10: Create AppRepository

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/repository/AppRepository.kt`

- [ ] **Step 1: Write AppRepository.kt**

```kotlin
package com.cozyfitness.data.repository

import com.cozyfitness.data.local.CozyFitnessDatabase
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/repository/AppRepository.kt
git commit -m "feat: add AppRepository with all data access methods"
```

---

## Task 11: Create Hilt DatabaseModule

**Files:**
- Create: `app/src/main/java/com/cozyfitness/di/DatabaseModule.kt`

- [ ] **Step 1: Write DatabaseModule.kt**

```kotlin
package com.cozyfitness.di

import android.content.Context
import androidx.room.Room
import com.cozyfitness.data.local.CozyFitnessDatabase
import com.cozyfitness.data.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CozyFitnessDatabase {
        return Room.databaseBuilder(
            context,
            CozyFitnessDatabase::class.java,
            "cozy_fitness_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAppRepository(database: CozyFitnessDatabase): AppRepository {
        return AppRepository(database)
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/di/DatabaseModule.kt
git commit -m "feat: add Hilt DatabaseModule for DI"
```

---

## Task 12: Add @Entity Annotations to Models

**Files:**
- Modify: `app/src/main/java/com/cozyfitness/domain/model/Models.kt`

- [ ] **Step 1: Add @Entity and @PrimaryKey annotations**

Replace the Models.kt content with:

```kotlin
package com.cozyfitness.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
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

@Entity(tableName = "workoutplan")
data class WorkoutPlan(
    @PrimaryKey
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

@Entity(tableName = "exercise")
data class Exercise(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val type: ExerciseType = ExerciseType.CARDIO,
    val durationSeconds: Int = 0,
    val repetitions: Int? = null,
    val sets: Int? = null,
    val caloriesPerRep: Int? = null,
    val restSeconds: Int = 30,
    val mediaUrl: String? = null,
    val planId: String = ""
)

enum class ExerciseType {
    CARDIO, STRENGTH, FLEXIBILITY, HIIT
}

@Entity(tableName = "workoutsession")
data class WorkoutSession(
    @PrimaryKey
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

@Entity(tableName = "dailystats")
data class DailyStats(
    @PrimaryKey
    val id: String = "",
    val date: String = "",
    val steps: Int = 0,
    val activeMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val averageHeartRate: Int? = null,
    val workoutsCompleted: Int = 0,
    val distanceKm: Float? = null
)

@Entity(tableName = "achievement")
data class Achievement(
    @PrimaryKey
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/domain/model/Models.kt
git commit -m "feat: add Room @Entity annotations to all models"
```

---

## Task 13: Initialize Database with Default Data

**Files:**
- Modify: `app/src/main/java/com/cozyfitness/CozyFitnessApp.kt`

- [ ] **Step 1: Update CozyFitnessApp.kt to initialize default data**

Replace the content with:

```kotlin
package com.cozyfitness

import android.app.Application
import androidx.room.Room
import com.cozyfitness.data.local.CozyFitnessDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CozyFitnessApp : Application() {

    lateinit var database: CozyFitnessDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            CozyFitnessDatabase::class.java,
            "cozy_fitness_db"
        ).build()
        initializeDefaultData()
    }

    private fun initializeDefaultData() {
        // Default data initialization will be handled via Repository
        // This is handled by the ViewModel on first launch
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/CozyFitnessApp.kt
git commit -m "refactor: CozyFitnessApp initializes database"
```

---

## Task 14: Create DefaultDataInitializer

**Files:**
- Create: `app/src/main/java/com/cozyfitness/data/local/DefaultDataInitializer.kt`

- [ ] **Step 1: Write DefaultDataInitializer.kt**

```kotlin
package com.cozyfitness.data.local

import com.cozyfitness.data.repository.AppRepository
import com.cozyfitness.domain.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDataInitializer @Inject constructor(
    private val repository: AppRepository
) {
    fun initializeIfNeeded(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val existingUser = repository.getUserOnce()
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
        repository.insertUser(defaultUser)
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
        repository.insertWorkoutPlan(morningCardioPlan)

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
        repository.insertExercises(exercises)
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
        achievements.forEach { repository.insertAchievement(it) }
    }
}
```

- [ ] **Step 2: Add initialization call to AppRepository**

Modify `AppRepository` constructor to accept `DefaultDataInitializer`:

```kotlin
@Singleton
class AppRepository @Inject constructor(
    private val database: CozyFitnessDatabase,
    private val defaultDataInitializer: DefaultDataInitializer
) {
    // ... existing code ...

    fun initializeDefaultData(scope: CoroutineScope) {
        defaultDataInitializer.initializeIfNeeded(scope)
    }
}
```

Also update `DatabaseModule.kt` to provide `DefaultDataInitializer`:

```kotlin
@Provides
@Singleton
fun provideDefaultDataInitializer(repository: AppRepository): DefaultDataInitializer {
    return DefaultDataInitializer(repository)
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/cozyfitness/data/local/DefaultDataInitializer.kt
git add app/src/main/java/com/cozyfitness/data/repository/AppRepository.kt
git add app/src/main/java/com/cozyfitness/di/DatabaseModule.kt
git commit -m "feat: add DefaultDataInitializer for first-launch setup"
```

---

## Task 15: Verify Build

- [ ] **Step 1: Run Gradle sync and build**

```bash
cd D:/train
./gradlew assembleDebug 2>&1
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Commit any final changes**

```bash
git add -A
git commit -m "chore: verify database implementation builds"
```

---

## Spec Coverage Check

- [x] User entity + DAO - Task 3, 12
- [x] WorkoutPlan entity + DAO - Task 4, 12
- [x] Exercise entity + DAO - Task 5, 12
- [x] WorkoutSession entity + DAO - Task 6, 12
- [x] DailyStats entity + DAO - Task 7, 12
- [x] Achievement entity + DAO - Task 8, 12
- [x] TypeConverters for all enums - Task 2
- [x] CozyFitnessDatabase with all DAOs - Task 9
- [x] AppRepository - Task 10
- [x] Hilt DI setup - Task 11
- [x] Default data initialization - Task 14
- [x] Profile screen integration (User settings) - Implied via AppRepository updateUser methods

---

**Plan complete and saved to `docs/superpowers/plans/2026-05-29-database-plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**