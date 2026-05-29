package com.cozyfitness.ui.profile

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cozyfitness.domain.model.UnitSystem
import com.cozyfitness.ui.theme.MintWhisper
import com.cozyfitness.ui.theme.SageGreen
import com.cozyfitness.ui.theme.SoftWhite

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var stepGoal by remember { mutableFloatStateOf(uiState.user?.dailyStepGoal?.toFloat() ?: 10000f) }
    var calorieGoal by remember { mutableFloatStateOf(uiState.user?.dailyCalorieGoal?.toFloat() ?: 500f) }
    var activeMinutesGoal by remember { mutableFloatStateOf(uiState.user?.dailyActiveMinutesGoal?.toFloat() ?: 30f) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var metricUnits by remember { mutableStateOf(uiState.user?.preferredUnit == UnitSystem.METRIC) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftWhite)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MintWhisper),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.headlineLarge,
                    color = SageGreen
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "我的",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Goals Section
        SectionHeader(title = "目标")
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                GoalSlider(
                    label = "每日步数",
                    value = stepGoal,
                    onValueChange = { stepGoal = it },
                    valueRange = 5000f..20000f,
                    step = 1000f,
                    displayValue = stepGoal.toInt().toString(),
                    onValueChangeComplete = { viewModel.updateStepGoal(it.toInt()) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                GoalSlider(
                    label = "每日卡路里",
                    value = calorieGoal,
                    onValueChange = { calorieGoal = it },
                    valueRange = 200f..1000f,
                    step = 50f,
                    displayValue = calorieGoal.toInt().toString(),
                    onValueChangeComplete = { viewModel.updateCalorieGoal(it.toInt()) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                GoalSlider(
                    label = "每日活动时间",
                    value = activeMinutesGoal,
                    onValueChange = { activeMinutesGoal = it },
                    valueRange = 10f..120f,
                    step = 5f,
                    displayValue = activeMinutesGoal.toInt().toString(),
                    onValueChangeComplete = { viewModel.updateActiveMinutesGoal(it.toInt()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Preferences Section
        SectionHeader(title = "偏好设置")
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                PreferenceToggle(
                    label = "单位",
                    value = if (metricUnits) "公制" else "英制",
                    checked = metricUnits,
                    onCheckedChange = { viewModel.updatePreferredUnit(if (it) UnitSystem.METRIC else UnitSystem.IMPERIAL) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                PreferenceToggle(
                    label = "通知",
                    value = if (notificationsEnabled) "开启" else "关闭",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Support Section
        SectionHeader(title = "支持")
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                SettingsItem(
                    icon = Icons.Default.Share,
                    label = "导出数据",
                    onClick = { }
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    label = "帮助与支持",
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Out Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "退出登录",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun GoalSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    step: Float,
    displayValue: String,
    onValueChangeComplete: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = SageGreen
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeComplete = onValueChangeComplete,
            valueRange = valueRange,
            steps = ((valueRange.endInclusive - valueRange.start) / step).toInt() - 1,
            colors = SliderDefaults.colors(
                thumbColor = SageGreen,
                activeTrackColor = SageGreen,
                inactiveTrackColor = MintWhisper
            )
        )
    }
}

@Composable
fun PreferenceToggle(
    label: String,
    value: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SageGreen,
                checkedTrackColor = MintWhisper
            )
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}