package com.cozyfitness.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cozyfitness.domain.model.Difficulty
import com.cozyfitness.domain.model.Exercise
import com.cozyfitness.domain.model.WorkoutPlan
import com.cozyfitness.ui.theme.CoralPeach
import com.cozyfitness.ui.theme.MintWhisper
import com.cozyfitness.ui.theme.SageGreen
import com.cozyfitness.ui.theme.SoftWhite

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("我的计划", "发现")

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val myWorkouts = listOf(
        WorkoutPlan(
            id = "1",
            title = "晨间有氧",
            description = "用活力开始新的一天",
            difficulty = Difficulty.BEGINNER,
            estimatedDurationMinutes = 25,
            estimatedCalories = 180,
            exercises = listOf(
                Exercise(name = "热身步行", durationSeconds = 300),
                Exercise(name = "慢跑", durationSeconds = 300),
                Exercise(name = "高抬腿", durationSeconds = 180)
            ),
            isActive = true
        ),
        WorkoutPlan(
            id = "2",
            title = "HIIT 燃脂",
            description = "高强度间歇训练",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedDurationMinutes = 30,
            estimatedCalories = 350,
            exercises = listOf(
                Exercise(name = "波比跳", durationSeconds = 45, restSeconds = 15),
                Exercise(name = "登山者", durationSeconds = 45, restSeconds = 15)
            ),
            isActive = false
        )
    )

    val discoverWorkouts = listOf(
        WorkoutPlan(
            id = "3",
            title = "晚间散步",
            description = "轻松散步结束一天",
            difficulty = Difficulty.BEGINNER,
            estimatedDurationMinutes = 20,
            estimatedCalories = 100
        ),
        WorkoutPlan(
            id = "4",
            title = "跳绳训练",
            description = "有趣的跳绳有氧",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedDurationMinutes = 15,
            estimatedCalories = 200
        ),
        WorkoutPlan(
            id = "5",
            title = "舞蹈有氧",
            description = "跳舞保持健康",
            difficulty = Difficulty.BEGINNER,
            estimatedDurationMinutes = 30,
            estimatedCalories = 250
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftWhite)
    ) {
        // Header
        Text(
            text = "训练计划",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = SoftWhite,
            contentColor = SageGreen
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        if (selectedTab == 0) {
            MyPlansContent(uiState.workoutPlans)
        } else {
            DiscoverContent(uiState.workoutPlans)
        }
    }
}

@Composable
fun MyPlansContent(workouts: List<WorkoutPlan>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(workouts) { workout ->
            WorkoutCard(workout = workout, showActiveBadge = true)
        }
    }
}

@Composable
fun DiscoverContent(workouts: List<WorkoutPlan>) {
    Column {
        // Filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("全部", "初级", "有氧", "HIIT", "力量")
            items(filters.size) { index ->
                FilterChip(
                    selected = index == 0,
                    onClick = { },
                    label = { Text(filters[index]) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SageGreen,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(workouts) { workout ->
                WorkoutCard(workout = workout, showActiveBadge = false)
            }
        }
    }
}

@Composable
fun WorkoutCard(
    workout: WorkoutPlan,
    showActiveBadge: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (showActiveBadge && workout.isActive) {
                            Box(
                                modifier = Modifier
                                    .background(SageGreen, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "进行中",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        DifficultyBadge(difficulty = workout.difficulty)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = workout.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = workout.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MintWhisper),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${workout.estimatedDurationMinutes}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${workout.estimatedDurationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = " • ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${workout.estimatedCalories} cal",
                    style = MaterialTheme.typography.bodySmall,
                    color = CoralPeach
                )
            }
        }
    }
}

@Composable
fun DifficultyBadge(difficulty: Difficulty) {
    val (color, text) = when (difficulty) {
        Difficulty.BEGINNER -> SageGreen to "初级"
        Difficulty.INTERMEDIATE -> CoralPeach to "中级"
        Difficulty.ADVANCED -> CoralPeach to "高级"
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}