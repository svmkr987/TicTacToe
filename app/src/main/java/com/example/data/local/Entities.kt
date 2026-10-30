package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "CYBER_RUNNER_01",
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalMatches: Int = 0,
    val credits: Int = 100,
    val xp: Int = 0,
    val selectedTheme: String = "CYBER_NEON",
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val selectedDifficulty: String = "OPERATIVE"
) {
    val winRatePercent: Int
        get() = if (totalMatches > 0) ((wins * 100) / totalMatches) else 0

    val playerLevel: Int
        get() = (xp / 150) + 1

    val levelTitle: String
        get() = when (playerLevel) {
            1 -> "Novice Grid Hacker"
            2 -> "Tactical Operative"
            3 -> "Cyber Enforcer"
            4 -> "Grid Vanguard"
            5 -> "Quantum Strategist"
            6 -> "Nexus Overlord"
            else -> "God-Tier Cyber Architect"
        }
}

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val score: Int,
    val rank: Int,
    val avatarId: String,
    val winRate: Int,
    val badge: String,
    val isFriend: Boolean,
    val isUser: Boolean = false,
    val region: String = "Neo-Tokyo Sector"
)

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val opponent: String,
    val difficulty: String,
    val result: String, // "VICTORY", "DEFEAT", "DRAW"
    val movesCount: Int,
    val pointsEarned: Int
)

@Entity(tableName = "daily_login_state")
data class DailyLoginEntity(
    @PrimaryKey val id: Int = 1,
    val lastClaimEpochDay: Long = -1L,
    val streakDays: Int = 0,
    val totalClaimedCount: Int = 0
)
