package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.local.UserStatsEntity
import com.example.data.model.ThemeMode
import com.example.data.repository.GameRepository
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.GameViewModelFactory

enum class AppNavDestination(val label: String, val testTag: String) {
    GAME("Matrix", "nav_game"),
    LEADERBOARD("League", "nav_leaderboard"),
    STATS("Dossier", "nav_stats"),
    SETTINGS("Settings", "nav_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexusAppRoot()
        }
    }
}

@Composable
fun NexusAppRoot() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val database = remember { AppDatabase.getDatabase(context, coroutineScope) }
    val repository = remember { GameRepository(database.gameDao()) }
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(repository)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val globalLeaderboard by viewModel.globalLeaderboard.collectAsStateWithLifecycle()
    val friendsLeaderboard by viewModel.friendsLeaderboard.collectAsStateWithLifecycle()
    val recentMatches by viewModel.recentMatches.collectAsStateWithLifecycle()

    val activeThemeMode = try {
        ThemeMode.valueOf(stats.selectedTheme)
    } catch (_: Exception) {
        ThemeMode.CYBER_NEON
    }

    var currentDestination by remember { mutableStateOf(AppNavDestination.GAME) }

    MyApplicationTheme(themeMode = activeThemeMode) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("app_bottom_bar"),
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.GAME,
                        onClick = { currentDestination = AppNavDestination.GAME },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.GridOn,
                                contentDescription = "Matrix"
                            )
                        },
                        label = {
                            Text(
                                text = "ARENA",
                                fontFamily = OrbitronFontFamily
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.2f),
                            unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_game")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.LEADERBOARD,
                        onClick = { currentDestination = AppNavDestination.LEADERBOARD },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = "League"
                            )
                        },
                        label = {
                            Text(
                                text = "LEAGUE",
                                fontFamily = OrbitronFontFamily
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.2f),
                            unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_leaderboard")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.STATS,
                        onClick = { currentDestination = AppNavDestination.STATS },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Dossier"
                            )
                        },
                        label = {
                            Text(
                                text = "DOSSIER",
                                fontFamily = OrbitronFontFamily
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonPink,
                            selectedTextColor = NeonPink,
                            indicatorColor = NeonPink.copy(alpha = 0.2f),
                            unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_stats")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.SETTINGS,
                        onClick = { currentDestination = AppNavDestination.SETTINGS },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = {
                            Text(
                                text = "SYSTEM",
                                fontFamily = OrbitronFontFamily
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.2f),
                            unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            when (currentDestination) {
                AppNavDestination.GAME -> {
                    GameScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        stats = stats,
                        onNavigateToLeaderboard = { currentDestination = AppNavDestination.LEADERBOARD },
                        onNavigateToStats = { currentDestination = AppNavDestination.STATS },
                        onNavigateToSettings = { currentDestination = AppNavDestination.SETTINGS },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppNavDestination.LEADERBOARD -> {
                    LeaderboardScreen(
                        viewModel = viewModel,
                        globalList = globalLeaderboard,
                        friendsList = friendsLeaderboard,
                        stats = stats,
                        onNavigateBack = { currentDestination = AppNavDestination.GAME },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppNavDestination.STATS -> {
                    StatsScreen(
                        stats = stats,
                        matchHistory = recentMatches,
                        onNavigateBack = { currentDestination = AppNavDestination.GAME },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppNavDestination.SETTINGS -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        stats = stats,
                        onNavigateBack = { currentDestination = AppNavDestination.GAME },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
