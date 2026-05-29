package com.cozyfitness.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cozyfitness.domain.model.DailyStats
import com.cozyfitness.domain.model.WorkoutPlan
import com.cozyfitness.ui.theme.CoralPeach
import com.cozyfitness.ui.theme.MintWhisper
import com.cozyfitness.ui.theme.SageGreen
import com.cozyfitness.ui.theme.SkyBlue
import com.cozyfitness.ui.theme.SoftWhite

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftWhite)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Greeting Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "早上好，Alex",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "今天也要加油哦",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MintWhisper),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.titleLarge,
                    color = SageGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Workout Hero Card
        TodayWorkoutCard(
            workoutPlan = WorkoutPlan(
                title = "晨间有氧",
                estimatedDurationMinutes = 25,
                estimatedCalories = 180,
                difficulty = com.cozyfitness.domain.model.Difficulty.BEGINNER
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Daily Stats Row
        Text(
            text = "今日数据",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatTile(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                value = "8,432",
                label = "步数",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            StatTile(
                icon = Icons.Default.Timer,
                value = "18",
                label = "分钟",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            StatTile(
                icon = Icons.Default.LocalFireDepartment,
                value = "320",
                label = "卡路里",
                modifier = Modifier.weight(1f),
                highlighted = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Start Section
        Text(
            text = "快速开始",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStartButton(
                text = "自由训练",
                modifier = Modifier.weight(1f)
            )
            QuickStartButton(
                text = "计时跑步",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TodayWorkoutCard(workoutPlan: WorkoutPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MintWhisper),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "有氧运动",
                    style = MaterialTheme.typography.titleMedium,
                    color = SageGreen
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = workoutPlan.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${workoutPlan.estimatedDurationMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = " • ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${workoutPlan.estimatedCalories} cal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CoralPeach
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("开始训练")
            }
        }
    }
}

@Composable
fun StatTile(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) MintWhisper else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlighted) SageGreen else SkyBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickStartButton(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 1.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            color = SageGreen
        )
    }
}