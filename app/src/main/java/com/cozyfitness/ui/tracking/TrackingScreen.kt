package com.cozyfitness.ui.tracking

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cozyfitness.ui.theme.Amber
import com.cozyfitness.ui.theme.CoralPeach
import com.cozyfitness.ui.theme.MintWhisper
import com.cozyfitness.ui.theme.SageGreen
import com.cozyfitness.ui.theme.SkyBlue
import com.cozyfitness.ui.theme.SoftWhite

@Composable
fun TrackingScreen() {
    var isRunning by remember { mutableStateOf(true) }
    var currentExercise by remember { mutableIntStateOf(0) }
    var currentSet by remember { mutableIntStateOf(2) }
    var restTime by remember { mutableIntStateOf(25) }

    val exercises = listOf(
        "热身步行",
        "慢跑",
        "高抬腿",
        "后踢腿",
        "跳绳",
        "登山者",
        "放松步行",
        "拉伸"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Timer Display
        Text(
            text = if (isRunning) "0:12:34" else "已暂停",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (isRunning) MaterialTheme.colorScheme.onBackground else Amber
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Current Exercise Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = exercises[currentExercise],
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (currentExercise < 3) "第 $currentSet 组" else "持续时间",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = CoralPeach,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "142",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "次/分",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "45",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CoralPeach
                        )
                        Text(
                            text = "千卡",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress dots
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(8) { index ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index <= currentExercise) SageGreen
                                    else MintWhisper
                                )
                        )
                        if (index < 7) Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Rest Timer (when applicable)
        if (restTime > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Amber.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "休息",
                        style = MaterialTheme.typography.labelLarge,
                        color = Amber
                    )
                    Text(
                        text = "0:${restTime.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Amber
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("跳过")
            }

            FilledIconButton(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.size(80.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = SageGreen
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "暂停" else "继续",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            OutlinedButton(
                onClick = { },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CoralPeach
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("结束")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}