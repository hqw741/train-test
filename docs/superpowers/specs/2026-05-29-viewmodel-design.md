# ViewModel + StateFlow Implementation Design

**Date**: 2026-05-29
**Feature**: ViewModel State Management
**Status**: Approved

---

## 1. Overview

Implement ViewModels with StateFlow for all 5 screens (Home, Workout, Tracking, Stats, Profile). Each screen gets its own ViewModel with a single `UiState` data class exposing state via `StateFlow`.

## 2. Architecture

```
UI Layer (Compose Screen)
    ↓ observes StateFlow
ViewModel (StateFlow<UiState>)
    ↓ calls
AppRepository
    ↓ uses
CozyFitnessDatabase (Room)
```

**Pattern:**
- `@HiltViewModel` annotation on all ViewModels
- `viewModel()` composable from `androidx.lifecycle.viewmodel.compose` for creation
- Single `UiState` data class per ViewModel
- StateFlow for reactive UI updates

## 3. ViewModels

### HomeViewModel
**UiState:**
```kotlin
data class HomeUiState(
    val user: User? = null,
    val activeWorkoutPlan: WorkoutPlan? = null,
    val todayStats: DailyStats? = null,
    val recentSessions: List<WorkoutSession> = emptyList(),
    val isLoading: Boolean = false
)
```

**Operations:**
- Load user on init
- Load active workout plan
- Load today's stats
- Load recent workout sessions

### WorkoutViewModel
**UiState:**
```kotlin
data class WorkoutUiState(
    val workoutPlans: List<WorkoutPlan> = emptyList(),
    val exercises: Map<String, List<Exercise>> = emptyMap(), // planId -> exercises
    val isLoading: Boolean = false
)
```

**Operations:**
- Load all workout plans
- Load exercises for each plan on demand

### TrackingViewModel
**UiState:**
```kotlin
data class TrackingUiState(
    val currentSession: WorkoutSession? = null,
    val currentExercises: List<Exercise> = emptyList(),
    val elapsedSeconds: Int = 0,
    val isTracking: Boolean = false,
    val isLoading: Boolean = false
)
```

**Operations:**
- Start new workout session
- Pause/resume tracking
- Complete workout
- Timer management

### StatsViewModel
**UiState:**
```kotlin
data class StatsUiState(
    val weeklyStats: List<DailyStats> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val totalWorkouts: Int = 0,
    val totalCalories: Int = 0,
    val isLoading: Boolean = false
)
```

**Operations:**
- Load weekly stats
- Load all achievements
- Calculate summary statistics

### ProfileViewModel
**UiState:**
```kotlin
data class ProfileUiState(
    val user: User? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)
```

**Operations:**
- Load user profile
- Update dailyStepGoal
- Update dailyCalorieGoal
- Update dailyActiveMinutesGoal
- Update preferredUnit

## 4. Hilt Integration

**ViewModelFactory:**
- Use `@HiltViewModel` annotation
- Inject `AppRepository` via constructor
- Hilt provides `viewModel()` composable automatically

**Example:**
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
```

## 5. Composable Usage

```kotlin
@Composable
@HiltViewModel
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    // Use uiState in composable
}
```

## 6. Implementation Order

1. HomeViewModel + HomeUiState
2. WorkoutViewModel + WorkoutUiState
3. TrackingViewModel + TrackingUiState
4. StatsViewModel + StatsUiState
5. ProfileViewModel + ProfileUiState

## 7. Screen Updates Required

Each screen currently has static composable functions. They need to:
1. Accept a ViewModel parameter (or create via hiltViewModel())
2. Observe `uiState` via `collectAsState()`
3. Use uiState values to render UI
4. Call ViewModel methods on user interactions

## 8. Key Considerations

- **StateFlow vs MutableStateFlow**: Expose `StateFlow` publicly, keep `MutableStateFlow` private
- **Loading states**: Show loading indicators while data loads from database
- **Error handling**: ViewModels should handle errors gracefully (log, show user message)
- **Initialization**: Load data in ViewModel init block or on first composition