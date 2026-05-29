# Database Implementation Design

**Date**: 2026-05-29
**Feature**: Room Database Persistence
**Status**: Approved

---

## 1. Overview

Implement Room database persistence for all 6 entities (User, WorkoutPlan, Exercise, WorkoutSession, DailyStats, Achievement) with a single AppRepository and single CozyFitnessDatabase.

## 2. Architecture

```
UI Layer (Compose Screen)
    ↓ StateFlow
ViewModel Layer
    ↓
AppRepository (single for all data access)
    ↓
CozyFitnessDatabase
    ├── UserDao
    ├── WorkoutPlanDao
    ├── ExerciseDao
    ├── WorkoutSessionDao
    ├── DailyStatsDao
    └── AchievementDao
    ↓
SQLite (Room)
```

## 3. Database Schema

### User
| Column | Type | Constraints |
|--------|------|-------------|
| id | TEXT | PRIMARY KEY |
| name | TEXT | NOT NULL, DEFAULT "Alex" |
| avatarUri | TEXT | NULLABLE |
| dailyStepGoal | INTEGER | DEFAULT 10000 |
| dailyCalorieGoal | INTEGER | DEFAULT 500 |
| dailyActiveMinutesGoal | INTEGER | DEFAULT 30 |
| preferredUnit | TEXT | DEFAULT "METRIC" |

### WorkoutPlan
| Column | Type | Constraints |
|--------|------|-------------|
| id | TEXT | PRIMARY KEY |
| title | TEXT | NOT NULL |
| description | TEXT | |
| coverImageUrl | TEXT | NULLABLE |
| difficulty | TEXT | DEFAULT "BEGINNER" |
| estimatedDurationMinutes | INTEGER | DEFAULT 0 |
| estimatedCalories | INTEGER | DEFAULT 0 |
| isActive | INTEGER | DEFAULT 0 (Boolean) |

### Exercise
| Column | Type | Constraints |
|--------|------|-------------|
| id | TEXT | PRIMARY KEY |
| name | TEXT | NOT NULL |
| description | TEXT | |
| type | TEXT | DEFAULT "CARDIO" |
| durationSeconds | INTEGER | DEFAULT 0 |
| repetitions | INTEGER | NULLABLE |
| sets | INTEGER | NULLABLE |
| caloriesPerRep | INTEGER | NULLABLE |
| restSeconds | INTEGER | DEFAULT 30 |
| mediaUrl | TEXT | NULLABLE |
| planId | TEXT | FOREIGN KEY → WorkoutPlan.id |

### WorkoutSession
| Column | Type | Constraints |
|--------|------|-------------|
| id | TEXT | PRIMARY KEY |
| planId | TEXT | NULLABLE, FK → WorkoutPlan.id |
| startedAt | INTEGER | NOT NULL |
| completedAt | INTEGER | NULLABLE |
| totalDurationSeconds | INTEGER | DEFAULT 0 |
| caloriesBurned | INTEGER | DEFAULT 0 |
| averageHeartRate | INTEGER | NULLABLE |
| exercisesCompleted | INTEGER | DEFAULT 0 |
| totalExercises | INTEGER | DEFAULT 0 |
| status | TEXT | DEFAULT "IN_PROGRESS" |

### DailyStats
| Column | Type | Constraints |
|--------|------|-------------|
| id | TEXT | PRIMARY KEY |
| date | TEXT | NOT NULL, UNIQUE |
| steps | INTEGER | DEFAULT 0 |
| activeMinutes | INTEGER | DEFAULT 0 |
| caloriesBurned | INTEGER | DEFAULT 0 |
| averageHeartRate | INTEGER | NULLABLE |
| workoutsCompleted | INTEGER | DEFAULT 0 |
| distanceKm | REAL | NULLABLE |

### Achievement
| Column | Type | Constraints |
|--------|------|-------------|
| id | TEXT | PRIMARY KEY |
| title | TEXT | NOT NULL |
| description | TEXT | |
| iconName | TEXT | |
| unlockedAt | INTEGER | NULLABLE |
| criteriaType | TEXT | DEFAULT "WORKOUTS" |
| criteriaValue | INTEGER | DEFAULT 0 |
| isUnlocked | INTEGER | DEFAULT 0 (Boolean) |

## 4. Component Structure

### Database Setup
- `CozyFitnessDatabase` class with `@Database` annotation
- Version: 1
- Export schema: false (simplified)
- No migrations (cold start rebuilds data on schema change)

### DAOs
Each DAO extends `Room DAO` with:
- `Insert`, `Update`, `Delete` operations
- `Query` for read operations
- Suspend functions for coroutine support
- `Flow` returns for reactive updates where needed

### AppRepository
Single repository class providing:
- `getUser()` / `updateUser()`
- `getWorkoutPlans()` / `getWorkoutPlan(id)` / `insertWorkoutPlan()`
- `getExercises(planId)` / `insertExercise()`
- `getWorkoutSessions()` / `insertWorkoutSession()` / `updateWorkoutSession()`
- `getDailyStats(date)` / `getWeeklyStats()` / `insertOrUpdateDailyStats()`
- `getAchievements()` / `unlockAchievement()`

### Hilt Integration
- `CozyFitnessDatabase` instance provided via `@Singleton`
- `AppRepository` provided via `@Singleton`
- Database injected into Application class for initialization

## 5. Type Converters

Room TypeConverters for:
- `Difficulty` enum ↔ String
- `ExerciseType` enum ↔ String
- `WorkoutStatus` enum ↔ String
- `AchievementType` enum ↔ String
- `UnitSystem` enum ↔ String

## 6. Initial Data Strategy

- Database pre-populated with sample WorkoutPlan on first launch
- User record created with defaults if not exists
- Default workout: "晨间有氧" (Morning Cardio) - 25 min, 180 cal

## 7. Profile Screen Integration

ProfileScreen reads/writes:
- `dailyStepGoal`
- `dailyCalorieGoal`
- `dailyActiveMinutesGoal`
- `preferredUnit`

UI: TextField or Slider for numeric goals, Dropdown for unit system.

## 8. Implementation Order

1. Add Room dependencies to build.gradle.kts
2. Create TypeConverters
3. Create all 6 DAO interfaces
4. Create CozyFitnessDatabase class
5. Create AppRepository
6. Add Hilt modules for database and repository
7. Add default data initialization
8. Test CRUD operations