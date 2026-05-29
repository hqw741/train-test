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