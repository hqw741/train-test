# ViewModel + StateFlow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement ViewModels with StateFlow for all 5 screens (Home, Workout, Tracking, Stats, Profile), each with a single UiState data class.

**Architecture:** Each screen gets a ViewModel with @HiltViewModel annotation, injected AppRepository via constructor, and exposes state via StateFlow<UiState>. Composable screens use hiltViewModel() to obtain ViewModel instances.

**Tech Stack:** Jetpack Compose, Hilt ViewModel, StateFlow, Coroutines

---

## File Structure

```
app/src/main/java/com/cozyfitness/
├── ui/
│   ├── home/
│   │   ├── HomeViewModel.kt      # NEW
│   │   └── HomeScreen.kt         # MODIFY - connect to ViewModel
│   ├── workout/
│   │   ├── WorkoutViewModel.kt   # NEW
│   │   └── WorkoutScreen.kt      # MODIFY - connect to ViewModel
│   ├── tracking/
│   │   ├── TrackingViewModel.kt   # NEW
│   │   └── TrackingScreen.kt      # MODIFY - connect to ViewModel
│   ├── stats/
│   │   ├── StatsViewModel.kt      # NEW
│   │   └── StatsScreen.kt        # MODIFY - connect to ViewModel
│   └── profile/
│       ├── ProfileViewModel.kt    # NEW
│       └── ProfileScreen.kt       # MODIFY - connect to ViewModel
```

---

## Task 1: Add Hilt ViewModel Dependency

**Files:**
- Modify: `app/build.gradle.kts`

- [ ] **Step 1: Check Hilt ViewModel dependency**

Check that this line exists in `app/build.gradle.kts`:
```kotlin
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
```

This dependency provides `@HiltViewModel` annotation and `hiltViewModel()` composable.

- [ ] **Step 2: Commit**

```bash
git add app/build.gradle.kts
git commit -m "chore: ensure Hilt ViewModel dependency present"
```

---

## Task 2: Create HomeViewModel

**Files:**
- Create: `app/src/main/java/com/cozyfitness/ui/home/HomeViewModel.kt`

- [ ] **Step 1: Write HomeViewModel.kt**

```kotlin
package com.cozyfitness.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cozyfitness.data.repository.AppRepository
import com.cozyfitness.domain.model.DailyStats
import com.cozyfitness.domain.model.User
import com.cozyfitness.domain.model.WorkoutPlan
import com.cozyfitness.domain.model.WorkoutSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val activeWorkoutPlan: WorkoutPlan? = null,
    val todayStats: DailyStats? = null,
    val recentSessions: List<WorkoutSession> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load user
            repository.getUser().collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }

        viewModelScope.launch {
            // Load active workout plan
            repository.getActiveWorkoutPlan().collect { plan ->
                _uiState.update { it.copy(activeWorkoutPlan = plan) }
            }
        }

        viewModelScope.launch {
            // Load today's stats
            val today = LocalDate.now().format(dateFormatter)
            repository.getDailyStatsByDate(today).collect { stats ->
                _uiState.update { it.copy(todayStats = stats, isLoading = false) }
            }
        }

        viewModelScope.launch {
            // Load recent sessions (last 7 days)
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            repository.getSessionsSince(sevenDaysAgo).collect { sessions ->
                _uiState.update { it.copy(recentSessions = sessions) }
            }
        }
    }

    fun refresh() {
        loadHomeData()
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/home/HomeViewModel.kt
git commit -m "feat: add HomeViewModel with HomeUiState"
```

---

## Task 3: Update HomeScreen to use HomeViewModel

**Files:**
- Modify: `app/src/main/java/com/cozyfitness/ui/home/HomeScreen.kt`

- [ ] **Step 1: Update imports and function signature**

Add these imports:
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
```

Change the composable function signature from:
```kotlin
@Composable
fun HomeScreen() {
```
to:
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
```

- [ ] **Step 2: Add state collection**

At the start of the composable body, add:
```kotlin
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

- [ ] **Step 3: Update static data to use uiState**

Replace the hardcoded `WorkoutPlan(...)` on line 91-97 with:
```kotlin
            uiState.activeWorkoutPlan?.let { plan ->
                TodayWorkoutCard(workoutPlan = plan)
            } ?: run {
                // Show placeholder if no active plan
                TodayWorkoutCard(
                    workoutPlan = WorkoutPlan(
                        title = "暂无训练计划",
                        estimatedDurationMinutes = 0,
                        estimatedCalories = 0,
                        difficulty = com.cozyfitness.domain.model.Difficulty.BEGINNER
                    )
                )
            }
```

Replace hardcoded stat values with uiState:
- Steps: `uiState.todayStats?.steps?.toString() ?: "0"` instead of "8,432"
- Minutes: `uiState.todayStats?.activeMinutes?.toString() ?: "0"` instead of "18"
- Calories: `uiState.todayStats?.caloriesBurned?.toString() ?: "0"` instead of "320"

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/home/HomeScreen.kt
git commit -m "refactor: HomeScreen connects to HomeViewModel"
```

---

## Task 4: Create WorkoutViewModel

**Files:**
- Create: `app/src/main/java/com/cozyfitness/ui/workout/WorkoutViewModel.kt`

- [ ] **Step 1: Write WorkoutViewModel.kt**

```kotlin
package com.cozyfitness.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cozyfitness.data.repository.AppRepository
import com.cozyfitness.domain.model.Exercise
import com.cozyfitness.domain.model.WorkoutPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutUiState(
    val workoutPlans: List<WorkoutPlan> = emptyList(),
    val exercises: Map<String, List<Exercise>> = emptyMap(),
    val isLoading: Boolean = false
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    init {
        loadWorkoutPlans()
    }

    private fun loadWorkoutPlans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAllWorkoutPlans().collect { plans ->
                _uiState.update { it.copy(workoutPlans = plans, isLoading = false) }
            }
        }
    }

    fun loadExercisesForPlan(planId: String) {
        viewModelScope.launch {
            repository.getExercisesByPlanId(planId).collect { exercises ->
                _uiState.update { currentState ->
                    currentState.copy(
                        exercises = currentState.exercises + (planId to exercises)
                    )
                }
            }
        }
    }

    fun setActivePlan(planId: String) {
        viewModelScope.launch {
            repository.setActivePlan(planId)
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/workout/WorkoutViewModel.kt
git commit -m "feat: add WorkoutViewModel with WorkoutUiState"
```

---

## Task 5: Update WorkoutScreen to use WorkoutViewModel

**Files:**
- Modify: `app/src/main/java/com/cozyfitness/ui/workout/WorkoutScreen.kt`

- [ ] **Step 1: Add imports**

Add at top:
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
```

- [ ] **Step 2: Update function signature**

Change from:
```kotlin
@Composable
fun WorkoutScreen() {
```
to:
```kotlin
@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel()
) {
```

- [ ] **Step 3: Add state collection and update UI**

Add after modifier:
```kotlin
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

The WorkoutScreen currently shows static workout plan cards. Update to use `uiState.workoutPlans` to render the list dynamically.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/workout/WorkoutScreen.kt
git commit -m "refactor: WorkoutScreen connects to WorkoutViewModel"
```

---

## Task 6: Create TrackingViewModel

**Files:**
- Create: `app/src/main/java/com/cozyfitness/ui/tracking/TrackingViewModel.kt`

- [ ] **Step 1: Write TrackingViewModel.kt**

```kotlin
package com.cozyfitness.ui.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cozyfitness.data.repository.AppRepository
import com.cozyfitness.domain.model.Exercise
import com.cozyfitness.domain.model.WorkoutSession
import com.cozyfitness.domain.model.WorkoutStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TrackingUiState(
    val currentSession: WorkoutSession? = null,
    val currentExercises: List<Exercise> = emptyList(),
    val elapsedSeconds: Int = 0,
    val isTracking: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        checkForInProgressSession()
    }

    private fun checkForInProgressSession() {
        viewModelScope.launch {
            val session = repository.getInProgressSession()
            if (session != null) {
                _uiState.update {
                    it.copy(
                        currentSession = session,
                        isTracking = true,
                        elapsedSeconds = session.totalDurationSeconds
                    )
                }
                startTimer()
            }
        }
    }

    fun startWorkout(planId: String? = null) {
        viewModelScope.launch {
            val session = WorkoutSession(
                id = UUID.randomUUID().toString(),
                planId = planId,
                startedAt = System.currentTimeMillis(),
                status = WorkoutStatus.IN_PROGRESS
            )
            repository.insertSession(session)

            if (planId != null) {
                repository.getExercisesByPlanId(planId).collect { exercises ->
                    _uiState.update { it.copy(currentExercises = exercises) }
                }
            }

            _uiState.update {
                it.copy(currentSession = session, isTracking = true)
            }
            startTimer()
        }
    }

    fun pauseWorkout() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTracking = false) }
    }

    fun resumeWorkout() {
        startTimer()
        _uiState.update { it.copy(isTracking = true) }
    }

    fun completeWorkout() {
        viewModelScope.launch {
            timerJob?.cancel()
            val session = _uiState.value.currentSession
            if (session != null) {
                val completedSession = session.copy(
                    completedAt = System.currentTimeMillis(),
                    totalDurationSeconds = _uiState.value.elapsedSeconds,
                    status = WorkoutStatus.COMPLETED
                )
                repository.updateSession(completedSession)
            }
            _uiState.update {
                TrackingUiState()
            }
        }
    }

    fun abandonWorkout() {
        viewModelScope.launch {
            timerJob?.cancel()
            val session = _uiState.value.currentSession
            if (session != null) {
                val abandonedSession = session.copy(
                    completedAt = System.currentTimeMillis(),
                    totalDurationSeconds = _uiState.value.elapsedSeconds,
                    status = WorkoutStatus.ABANDONED
                )
                repository.updateSession(abandonedSession)
            }
            _uiState.update {
                TrackingUiState()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/tracking/TrackingViewModel.kt
git commit -m "feat: add TrackingViewModel with timer functionality"
```

---

## Task 7: Update TrackingScreen to use TrackingViewModel

**Files:**
- Modify: `app/src/main/java/com/cozyfitness/ui/tracking/TrackingScreen.kt`

- [ ] **Step 1: Add imports**

Add at top:
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
```

- [ ] **Step 2: Update function signature**

Change from:
```kotlin
@Composable
fun TrackingScreen() {
```
to:
```kotlin
@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel = hiltViewModel()
) {
```

- [ ] **Step 3: Add state collection**

Add after modifier:
```kotlin
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

- [ ] **Step 4: Update UI to use state**

The TrackingScreen should display:
- Timer based on `uiState.elapsedSeconds`
- Start/Pause/Resume/Complete buttons based on `uiState.isTracking` and `uiState.currentSession`
- Current exercises from `uiState.currentExercises`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/tracking/TrackingScreen.kt
git commit -m "refactor: TrackingScreen connects to TrackingViewModel"
```

---

## Task 8: Create StatsViewModel

**Files:**
- Create: `app/src/main/java/com/cozyfitness/ui/stats/StatsViewModel.kt`

- [ ] **Step 1: Write StatsViewModel.kt**

```kotlin
package com.cozyfitness.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cozyfitness.data.repository.AppRepository
import com.cozyfitness.domain.model.Achievement
import com.cozyfitness.domain.model.DailyStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val weeklyStats: List<DailyStats> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val totalWorkouts: Int = 0,
    val totalCalories: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getWeeklyStats().collect { stats ->
                val totalCalories = stats.sumOf { it.caloriesBurned }
                _uiState.update {
                    it.copy(
                        weeklyStats = stats,
                        totalCalories = totalCalories
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getAllAchievements().collect { achievements ->
                _uiState.update {
                    it.copy(
                        achievements = achievements,
                        totalWorkouts = achievements.count { a -> a.isUnlocked }
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getAllSessions().collect { sessions ->
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/stats/StatsViewModel.kt
git commit -m "feat: add StatsViewModel with StatsUiState"
```

---

## Task 9: Update StatsScreen to use StatsViewModel

**Files:**
- Modify: `app/src/main/java/com/cozyfitness/ui/stats/StatsScreen.kt`

- [ ] **Step 1: Add imports**

Add at top:
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
```

- [ ] **Step 2: Update function signature**

Change from:
```kotlin
@Composable
fun StatsScreen() {
```
to:
```kotlin
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
```

- [ ] **Step 3: Add state collection**

Add after modifier:
```kotlin
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

- [ ] **Step 4: Update UI to use state**

Replace static stats with `uiState.totalWorkouts`, `uiState.totalCalories`, `uiState.weeklyStats`.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/stats/StatsScreen.kt
git commit -m "refactor: StatsScreen connects to StatsViewModel"
```

---

## Task 10: Create ProfileViewModel

**Files:**
- Create: `app/src/main/java/com/cozyfitness/ui/profile/ProfileViewModel.kt`

- [ ] **Step 1: Write ProfileViewModel.kt**

```kotlin
package com.cozyfitness.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cozyfitness.data.repository.AppRepository
import com.cozyfitness.domain.model.UnitSystem
import com.cozyfitness.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            repository.getUser().collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun updateStepGoal(stepGoal: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val userId = _uiState.value.user?.id ?: return@launch
            repository.updateUserGoals(userId, stepGoal = stepGoal)
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun updateCalorieGoal(calorieGoal: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val userId = _uiState.value.user?.id ?: return@launch
            repository.updateUserGoals(userId, calorieGoal = calorieGoal)
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun updateActiveMinutesGoal(minutesGoal: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val userId = _uiState.value.user?.id ?: return@launch
            repository.updateUserGoals(userId, minutesGoal = minutesGoal)
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun updatePreferredUnit(unit: UnitSystem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val userId = _uiState.value.user?.id ?: return@launch
            repository.updatePreferredUnit(userId, unit)
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/profile/ProfileViewModel.kt
git commit -m "feat: add ProfileViewModel with goal update methods"
```

---

## Task 11: Update ProfileScreen to use ProfileViewModel

**Files:**
- Modify: `app/src/main/java/com/cozyfitness/ui/profile/ProfileScreen.kt`

- [ ] **Step 1: Add imports**

Add at top:
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
```

- [ ] **Step 2: Update function signature**

Change from:
```kotlin
@Composable
fun ProfileScreen() {
```
to:
```kotlin
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
```

- [ ] **Step 3: Add state collection**

Add after modifier:
```kotlin
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

- [ ] **Step 4: Update sliders to call ViewModel methods**

Replace `stepGoal`, `calorieGoal`, `activeMinutesGoal` with values from `uiState.user`:
- Initial slider values from `uiState.user?.dailyStepGoal ?: 10000`
- OnValueChangeComplete calls: `viewModel.updateStepGoal(it.toInt())`, etc.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/cozyfitness/ui/profile/ProfileScreen.kt
git commit -m "refactor: ProfileScreen connects to ProfileViewModel"
```

---

## Task 12: Verify Build

- [ ] **Step 1: Attempt build**

```bash
cd D:/train
./gradlew assembleDebug 2>&1
```

Expected: BUILD SUCCESSFUL (or specific compile errors to fix)

- [ ] **Step 2: Commit any final changes**

```bash
git add -A
git commit -m "chore: verify ViewModel implementation builds"
```

---

## Spec Coverage Check

- [x] HomeViewModel + HomeUiState - Task 2, 3
- [x] WorkoutViewModel + WorkoutUiState - Task 4, 5
- [x] TrackingViewModel + TrackingUiState - Task 6, 7
- [x] StatsViewModel + StatsUiState - Task 8, 9
- [x] ProfileViewModel + ProfileUiState - Task 10, 11
- [x] All screens connected to ViewModels - Tasks 3, 5, 7, 9, 11
- [x] Hilt integration - Task 1

---

**Plan complete and saved to `docs/superpowers/plans/2026-05-29-viewmodel-plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**