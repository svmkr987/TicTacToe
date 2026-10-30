package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LeaderboardEntry
import com.example.data.local.UserStatsEntity
import com.example.ui.components.AddFriendDialog
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.viewmodel.GameViewModel
import com.example.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: GameViewModel,
    globalList: List<LeaderboardEntry>,
    friendsList: List<LeaderboardEntry>,
    stats: UserStatsEntity,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = Global, 1 = Friends
    var showAddFriendDialog by remember { mutableStateOf(false) }

    BackHandler {
        onNavigateBack()
    }

    val currentList = if (selectedTab == 0) globalList else friendsList

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CYBER LEAGUE",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonCyan,
                        fontFamily = OrbitronFontFamily
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("leaderboard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { ShareHelper.sharePlayerScore(context, stats) },
                        modifier = Modifier.testTag("share_rank_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Ranking",
                            tint = NeonPink
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAddFriendDialog = true },
                    containerColor = NeonCyan,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("add_friend_fab")
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Friend")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs: Global League vs Friends Division
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "GLOBAL MATRIX (${globalList.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = OrbitronFontFamily
                        )
                    },
                    modifier = Modifier.testTag("tab_global")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "FRIENDS (${friendsList.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = OrbitronFontFamily
                        )
                    },
                    modifier = Modifier.testTag("tab_friends")
                )
            }

            // Top Banner for Friends Tab
            if (selectedTab == 1 && friendsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO FRIENDS ADDED YET",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonCyan,
                            fontFamily = OrbitronFontFamily
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the + button below to add your friends and compete on your private leaderboard!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showAddFriendDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_friend_button")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ADD FIRST FRIEND", color = Color.Black)
                        }
                    }
                }
            }

            // Leaderboard List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(currentList) { index, entry ->
                    val rankNumber = index + 1
                    LeaderboardCard(
                        entry = entry,
                        displayRank = rankNumber,
                        isUser = entry.isUser || entry.playerName == stats.playerName
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showAddFriendDialog) {
        AddFriendDialog(
            onConfirm = { name, score, region ->
                viewModel.addFriend(name, score, region)
                showAddFriendDialog = false
            },
            onDismiss = { showAddFriendDialog = false }
        )
    }
}

@Composable
private fun LeaderboardCard(
    entry: LeaderboardEntry,
    displayRank: Int,
    isUser: Boolean
) {
    val rankBadgeColor = when (displayRank) {
        1 -> NeonGold
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val cardBorder = if (isUser) {
        Brush.linearGradient(listOf(NeonCyan, NeonPink))
    } else {
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
    }

    val cardBg = if (isUser) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(
                width = if (isUser) 2.dp else 1.dp,
                brush = cardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rank Number / Trophy
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(rankBadgeColor.copy(alpha = 0.18f))
                        .border(1.dp, rankBadgeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (displayRank <= 3) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Rank $displayRank",
                            tint = rankBadgeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "#$displayRank",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = rankBadgeColor,
                            fontFamily = OrbitronFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Badges
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.playerName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isUser) FontWeight.Bold else FontWeight.SemiBold
                            ),
                            color = if (isUser) NeonCyan else MaterialTheme.colorScheme.onSurface
                        )
                        if (isUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonCyan.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "YOU",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = NeonCyan,
                                    fontFamily = OrbitronFontFamily
                                )
                            }
                        } else if (entry.isFriend) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonPink.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FRIEND",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = NeonPink,
                                    fontFamily = OrbitronFontFamily
                                )
                            }
                        }
                    }

                    Text(
                        text = "${entry.region} • ${entry.badge}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Score & Win Rate
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.score} PTS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonGold
                )
                Text(
                    text = "${entry.winRate}% WIN RATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
