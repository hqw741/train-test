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