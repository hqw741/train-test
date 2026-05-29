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