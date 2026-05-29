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