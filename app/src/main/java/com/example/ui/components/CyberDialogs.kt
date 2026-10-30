package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserStatsEntity
import com.example.data.model.Difficulty
import com.example.data.model.GameStatus
import com.example.ui.theme.DefeatRed
import com.example.ui.theme.DrawYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.VictoryGreen
import com.example.util.ShareHelper

@Composable
fun GameEndDialog(
    status: GameStatus,
    difficulty: Difficulty,
    stats: UserStatsEntity,
    context: Context,
    onPlayAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    val isWin = status == GameStatus.X_WON
    val isLoss = status == GameStatus.O_WON
    val isDraw = status == GameStatus.DRAW

    val outcomeColor = when {
        isWin -> VictoryGreen
        isLoss -> DefeatRed
        else -> DrawYellow
    }

    val outcomeTitle = when {
        isWin -> "VICTORY ACHIEVED"
        isLoss -> "DEFEAT DETECTED"
        else -> "TACTICAL STALEMATE"
    }

    val outcomeSubtitle = when {
        isWin -> "Opponent matrix breached on ${difficulty.title} protocol."
        isLoss -> "Nexus algorithm overpowered your defenses."
        else -> "Zero vulnerabilities found on both sides."
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = outcomeColor.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("game_end_dialog"),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                outcomeColor.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Glowing Emblem
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(outcomeColor.copy(alpha = 0.2f))
                        .border(2.dp, outcomeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isWin -> Icons.Default.EmojiEvents
                            isLoss -> Icons.Default.Warning
                            else -> Icons.Default.Celebration
                        },
                        contentDescription = outcomeTitle,
                        tint = outcomeColor,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = outcomeTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = OrbitronFontFamily,
                        fontSize = 20.sp
                    ),
                    color = outcomeColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = outcomeSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rewards / Stats Snapshot Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isWin) "+${difficulty.winXp} XP" else "+10 XP",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonCyan,
                            fontFamily = OrbitronFontFamily
                        )
                        Text(
                            text = "EXPERIENCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isWin) "+${difficulty.winCredits} CC" else "+5 CC",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonGold,
                            fontFamily = OrbitronFontFamily
                        )
                        Text(
                            text = "CREDITS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${stats.currentStreak}X",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFF6D00),
                            fontFamily = OrbitronFontFamily
                        )
                        Text(
                            text = "STREAK",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("play_again_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = outcomeColor),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RE-ENGAGE MATRIX",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        ShareHelper.sharePlayerScore(context, stats)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("share_result_button"),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(NeonCyan, NeonPink))
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SHARE CYBER SCORECARD",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dismiss_dialog_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "CLOSE TELEMETRY",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AddFriendDialog(
    onConfirm: (name: String, score: Int, region: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var scoreText by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("Neo-Tokyo Grid") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .testTag("add_friend_dialog"),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ADD RIVAL FRIEND",
                            style = MaterialTheme.typography.titleLarge,
                            color = NeonCyan,
                            fontFamily = OrbitronFontFamily
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(
                    text = "Add your friend's codename and score to compete on your personal friends leaderboard.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Friend's Codename (e.g. CYBER_ALEX)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("friend_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = scoreText,
                    onValueChange = { scoreText = it },
                    label = { Text("High Score (Points)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("friend_score_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Sector / Region (Optional)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("friend_region_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = DefeatRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val parsedScore = scoreText.toIntOrNull()
                        if (name.isBlank()) {
                            errorMessage = "Please enter a valid codename"
                        } else if (parsedScore == null || parsedScore < 0) {
                            errorMessage = "Please enter a valid score (positive number)"
                        } else {
                            onConfirm(name, parsedScore, region.ifBlank { "Cyber Sector" })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_friend_confirm_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "REGISTER FRIEND ON GRID",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
