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