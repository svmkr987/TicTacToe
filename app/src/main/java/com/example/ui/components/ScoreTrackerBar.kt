package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserStatsEntity
import com.example.ui.theme.DefeatRed
import com.example.ui.theme.DrawYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.VictoryGreen

@Composable
fun ScoreTrackerBar(
    stats: UserStatsEntity,
    modifier: Modifier = Modifier
) {
    val winRateFloat by animateFloatAsState(
        targetValue = stats.winRatePercent / 100f,
        animationSpec = tween(600),
        label = "win_rate_anim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("score_tracker_bar")
    ) {
        // Top Row: Player Level & Currency
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .border(1.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Level",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "LVL ${stats.playerLevel} • ${stats.levelTitle}",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonCyan,
                        fontFamily = OrbitronFontFamily
                    )
                    Text(
                        text = stats.playerName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Streak & Credits Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Streak
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2E1A0E))
                        .border(1.dp, Color(0xFFFF6D00), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Win Streak",
                        tint = Color(0xFFFF6D00),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stats.currentStreak}X",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFFAB40),
                        fontFamily = OrbitronFontFamily
                    )
                }

                // Credits
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF26200A))
                        .border(1.dp, NeonGold, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Credits",
                        tint = NeonGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stats.credits}",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonGold,
                        fontFamily = OrbitronFontFamily
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Real-Time Match Statistics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                label = "VICTORIES",
                count = stats.wins,
                accentColor = VictoryGreen
            )
            StatItem(
                label = "DEFEATS",
                count = stats.losses,
                accentColor = DefeatRed
            )
            StatItem(
                label = "DRAWS",
                count = stats.draws,
                accentColor = DrawYellow
            )
            StatItem(
                label = "WIN RATE",
                countString = "${stats.winRatePercent}%",
                accentColor = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Win Rate Progress Bar
        LinearProgressIndicator(
            progress = { winRateFloat },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = NeonCyan,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    count: Int? = null,
    countString: String? = null,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = countString ?: count?.toString() ?: "0",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = accentColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}
