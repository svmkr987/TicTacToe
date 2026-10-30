package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.DailyLoginEntity
import com.example.data.model.DailyReward
import com.example.data.model.DailyRewardCatalog
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.OrbitronFontFamily
import java.time.LocalDate

@Composable
fun DailyRewardModal(
    dailyState: DailyLoginEntity,
    claimMessage: String?,
    context: Context,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    val todayEpochDay = LocalDate.now().toEpochDay()
    val isClaimedToday = dailyState.lastClaimEpochDay == todayEpochDay
    val currentDayIndex = if (dailyState.streakDays == 0) 1 else ((dailyState.streakDays - 1) % 7) + 1

    val infiniteTransition = rememberInfiniteTransition(label = "reward_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reward_pulse_glow"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(NeonCyan, NeonGreen)),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("daily_reward_modal"),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DAILY LOGIN MATRIX",
                                style = MaterialTheme.typography.titleLarge,
                                color = NeonGreen,
                                fontFamily = OrbitronFontFamily
                            )
                            Text(
                                text = "Streak: ${dailyState.streakDays} Consecutive Days",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7 Days Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DailyRewardCatalog.REWARDS) { reward ->
                        val isDayClaimed = reward.dayNumber < currentDayIndex || (reward.dayNumber == currentDayIndex && isClaimedToday)
                        val isCurrentTarget = reward.dayNumber == currentDayIndex && !isClaimedToday

                        DailyRewardCard(
                            reward = reward,
                            isClaimed = isDayClaimed,
                            isCurrentTarget = isCurrentTarget,
                            pulseGlow = if (isCurrentTarget) pulseGlow else 1f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status message or claim result
                if (claimMessage != null) {
                    Text(
                        text = claimMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = NeonGreen,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Claim Button
                Button(
                    onClick = onClaim,
                    enabled = !isClaimedToday,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("claim_reward_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (isClaimedToday) Icons.Default.CheckCircle else Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = if (isClaimedToday) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isClaimedToday) "SYNCHRONIZED TODAY" else "CLAIM DAILY UPLINK",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isClaimedToday) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyRewardCard(
    reward: DailyReward,
    isClaimed: Boolean,
    isCurrentTarget: Boolean,
    pulseGlow: Float
) {
    val borderColor = when {
        isCurrentTarget -> NeonGreen.copy(alpha = pulseGlow)
        isClaimed -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        reward.dayNumber == 7 -> NeonGold.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }

    val bgColor = when {
        isCurrentTarget -> NeonGreen.copy(alpha = 0.15f * pulseGlow)
        isClaimed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        reward.dayNumber == 7 -> NeonGold.copy(alpha = 0.10f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(width = if (isCurrentTarget) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DAY ${reward.dayNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isCurrentTarget) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = OrbitronFontFamily
            )

            Spacer(modifier = Modifier.height(4.dp))

            Icon(
                imageVector = when {
                    isClaimed -> Icons.Default.CheckCircle
                    reward.dayNumber == 7 -> Icons.Default.Stars
                    else -> Icons.Default.MonetizationOn
                },
                contentDescription = null,
                tint = when {
                    isClaimed -> NeonCyan.copy(alpha = 0.6f)
                    isCurrentTarget -> NeonGreen
                    reward.dayNumber == 7 -> NeonGold
                    else -> NeonGold.copy(alpha = 0.7f)
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "+${reward.rewardCredits}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isCurrentTarget) NeonGreen else MaterialTheme.colorScheme.onSurface,
                fontFamily = OrbitronFontFamily
            )

            if (reward.bonusItem != null) {
                Text(
                    text = reward.bonusItem,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = NeonPink,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
