package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserStatsEntity
import com.example.data.model.Difficulty
import com.example.data.model.GameMode
import com.example.data.model.GameStatus
import com.example.data.model.PlayerMark
import com.example.ui.components.DailyRewardModal
import com.example.ui.components.GameEndDialog
import com.example.ui.components.NeonBoard
import com.example.ui.components.ScoreTrackerBar
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.viewmodel.GameUiState
import com.example.ui.viewmodel.GameViewModel
import com.example.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    uiState: GameUiState,
    stats: UserStatsEntity,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDailyAvailable = viewModel.isDailyRewardAvailable()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NEXUS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = NeonCyan
                        )
                        Text(
                            text = " XO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = NeonPink
                        )
                    }
                },
                actions = {
                    // Daily Login Rewards Button with glowing badge
                    Box {
                        IconButton(
                            onClick = { viewModel.openDailyRewardModal() },
                            modifier = Modifier.testTag("daily_rewards_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Daily Rewards",
                                tint = if (isDailyAvailable) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isDailyAvailable) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen)
                                    .align(Alignment.TopEnd)
                                    .padding(2.dp)
                            )
                        }
                    }

                    // Leaderboard Shortcut
                    IconButton(
                        onClick = onNavigateToLeaderboard,
                        modifier = Modifier.testTag("leaderboard_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = "Leaderboards",
                            tint = NeonCyan
                        )
                    }

                    // Settings
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Real-Time Win-Loss Tracker
            ScoreTrackerBar(
                stats = stats,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .widthIn(max = 500.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Game Mode Toggle (VS AI vs Pass & Play)
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .widthIn(max = 440.dp)
            ) {
                SegmentedButton(
                    selected = uiState.gameMode == GameMode.VS_AI,
                    onClick = { viewModel.setGameMode(GameMode.VS_AI) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    modifier = Modifier.testTag("mode_vs_ai")
                ) {
                    Text("VS CYBER AI", style = MaterialTheme.typography.labelSmall)
                }
                SegmentedButton(
                    selected = uiState.gameMode == GameMode.PASS_AND_PLAY,
                    onClick = { viewModel.setGameMode(GameMode.PASS_AND_PLAY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    modifier = Modifier.testTag("mode_pass_play")
                ) {
                    Text("2P PASS & PLAY", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Difficulty Chips (Only when in VS AI Mode)
            AnimatedVisibility(visible = uiState.gameMode == GameMode.VS_AI) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Difficulty.values().forEach { diff ->
                        val isSelected = uiState.difficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setDifficulty(diff) },
                            label = {
                                Text(
                                    text = diff.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = OrbitronFontFamily
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (diff) {
                                    Difficulty.RECRUIT -> Color(0xFF00B4D8).copy(alpha = 0.3f)
                                    Difficulty.OPERATIVE -> NeonCyan.copy(alpha = 0.3f)
                                    Difficulty.SENTINEL -> NeonPink.copy(alpha = 0.3f)
                                    Difficulty.NEXUS -> Color(0xFF7C4DFF).copy(alpha = 0.35f)
                                },
                                selectedLabelColor = when (diff) {
                                    Difficulty.RECRUIT -> Color(0xFF00E5FF)
                                    Difficulty.OPERATIVE -> NeonCyan
                                    Difficulty.SENTINEL -> NeonPink
                                    Difficulty.NEXUS -> Color(0xFFB388FF)
                                }
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) NeonCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("difficulty_${diff.name.lowercase()}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Turn / Neural Status Banner
            TurnStatusBanner(
                currentTurn = uiState.currentTurn,
                isAiThinking = uiState.isAiThinking,
                statusMessage = uiState.aiStatusMessage,
                gameMode = uiState.gameMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 440.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // High-Tech Neon 3x3 Board
            NeonBoard(
                board = uiState.board,
                winningLine = uiState.winningLine,
                lastMoveIndex = uiState.lastMoveIndex,
                onCellClick = { index ->
                    viewModel.onCellClicked(index, context, haptic)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Action Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 440.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Restart Match Button
                Button(
                    onClick = { viewModel.restartGame() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("restart_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RESET GRID",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Share Score Button
                OutlinedButton(
                    onClick = {
                        ShareHelper.sharePlayerScore(context, stats)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("share_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Scorecard",
                        tint = NeonPink
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SHARE SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPink
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Victory / Defeat / Draw End Dialog
    if (uiState.showGameEndDialog) {
        GameEndDialog(
            status = uiState.gameStatus,
            difficulty = uiState.difficulty,
            stats = stats,
            context = context,
            onPlayAgain = {
                viewModel.restartGame()
            },
            onDismiss = {
                viewModel.dismissEndDialog()
            }
        )
    }

    // Daily Rewards Modal
    val dailyLoginState by viewModel.dailyLoginState.collectAsStateWithLifecycle(initialValue = com.example.data.local.DailyLoginEntity())
    if (uiState.showDailyRewardModal) {
        DailyRewardModal(
            dailyState = dailyLoginState,
            claimMessage = uiState.dailyClaimResult,
            context = context,
            onClaim = {
                viewModel.claimDailyReward(context)
            },
            onDismiss = {
                viewModel.closeDailyRewardModal()
            }
        )
    }
}

@Composable
private fun TurnStatusBanner(
    currentTurn: PlayerMark,
    isAiThinking: Boolean,
    statusMessage: String,
    gameMode: GameMode,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_banner")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val activeColor = if (currentTurn == PlayerMark.X) NeonCyan else NeonPink

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        activeColor.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = activeColor.copy(alpha = if (isAiThinking) pulseAlpha else 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Turn Symbol Emblem
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(activeColor.copy(alpha = 0.2f))
                    .border(1.dp, activeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentTurn.name,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = activeColor,
                    fontFamily = OrbitronFontFamily
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.labelMedium,
                color = activeColor,
                fontFamily = OrbitronFontFamily
            )
        }

        if (isAiThinking) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = NeonPink,
                strokeWidth = 2.dp
            )
        }
    }
}
