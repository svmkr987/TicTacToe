package com.example.data.repository

import com.example.data.local.DailyLoginEntity
import com.example.data.local.GameDao
import com.example.data.local.LeaderboardEntry
import com.example.data.local.MatchHistoryEntity
import com.example.data.local.UserStatsEntity
import com.example.data.model.DailyRewardCatalog
import com.example.data.model.Difficulty
import com.example.data.model.GameStatus
import com.example.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GameRepository(private val gameDao: GameDao) {

    val userStats: Flow<UserStatsEntity?> = gameDao.getUserStats()
    val globalLeaderboard: Flow<List<LeaderboardEntry>> = gameDao.getAllLeaderboard()
    val friendsLeaderboard: Flow<List<LeaderboardEntry>> = gameDao.getFriendsLeaderboard()
    val recentMatches: Flow<List<MatchHistoryEntity>> = gameDao.getRecentMatches()
    val dailyLoginState: Flow<DailyLoginEntity?> = gameDao.getDailyLogin()

    suspend fun recordMatchResult(
        result: GameStatus,
        difficulty: Difficulty,
        movesCount: Int,
        opponentName: String = "Cyber AI Core"
    ) {
        val currentStats = getCurrentStatsSnapshot()
        val (winXp, winCredits) = when (result) {
            GameStatus.X_WON -> difficulty.winXp to difficulty.winCredits
            GameStatus.DRAW -> 20 to 10
            else -> 10 to 5
        }

        val newWins = currentStats.wins + (if (result == GameStatus.X_WON) 1 else 0)
        val newLosses = currentStats.losses + (if (result == GameStatus.O_WON) 1 else 0)
        val newDraws = currentStats.draws + (if (result == GameStatus.DRAW) 1 else 0)
        val newStreak = if (result == GameStatus.X_WON) currentStats.currentStreak + 1 else 0
        val newBestStreak = maxOf(currentStats.bestStreak, newStreak)
        val newTotalMatches = currentStats.totalMatches + 1
        val newCredits = currentStats.credits + winCredits
        val newXp = currentStats.xp + winXp

        val updatedStats = currentStats.copy(
            wins = newWins,
            losses = newLosses,
            draws = newDraws,
            currentStreak = newStreak,
            bestStreak = newBestStreak,
            totalMatches = newTotalMatches,
            credits = newCredits,
            xp = newXp
        )
        gameDao.upsertUserStats(updatedStats)

        // Record in match history
        val resultString = when (result) {
            GameStatus.X_WON -> "VICTORY"
            GameStatus.O_WON -> "DEFEAT"
            GameStatus.DRAW -> "DRAW"
            else -> "UNKNOWN"
        }
        gameDao.insertMatch(
            MatchHistoryEntity(
                opponent = opponentName,
                difficulty = difficulty.title,
                result = resultString,
                movesCount = movesCount,
                pointsEarned = winXp
            )
        )

        // Sync player's score and rank in the leaderboard
        syncPlayerLeaderboardScore(updatedStats)
    }

    private suspend fun syncPlayerLeaderboardScore(stats: UserStatsEntity) {
        val userEntry = gameDao.getUserLeaderboardEntry()
        val calculatedScore = (stats.wins * 150) + (stats.draws * 50) + (stats.bestStreak * 100) + stats.xp
        if (userEntry != null) {
            gameDao.updateLeaderboardEntry(
                userEntry.copy(
                    playerName = stats.playerName,
                    score = calculatedScore,
                    winRate = stats.winRatePercent
                )
            )
        } else {
            gameDao.insertLeaderboardEntry(
                LeaderboardEntry(
                    id = 2,
                    playerName = stats.playerName,
                    score = calculatedScore,
                    rank = 2,
                    avatarId = "user",
                    winRate = stats.winRatePercent,
                    badge = "PRODIGY",
                    isFriend = false,
                    isUser = true
                )
            )
        }
    }

    suspend fun addFriendToLeaderboard(name: String, score: Int, region: String = "Cyber Sector") {
        val count = gameDao.getLeaderboardCount()
        val rank = count + 1
        val winRate = minOf(95, maxOf(45, (score / 45) + 30))
        val badge = when {
            score >= 3500 -> "GRANDMASTER"
            score >= 2500 -> "DIAMOND"
            score >= 1500 -> "PLATINUM"
            score >= 800 -> "GOLD"
            else -> "SILVER"
        }
        val newFriend = LeaderboardEntry(
            playerName = name.trim().uppercase(),
            score = score,
            rank = rank,
            avatarId = "friend",
            winRate = winRate,
            badge = badge,
            isFriend = true,
            isUser = false,
            region = region
        )
        gameDao.insertLeaderboardEntry(newFriend)
    }

    suspend fun claimDailyReward(): Pair<Int, Int>? {
        val todayEpochDay = LocalDate.now().toEpochDay()
        var currentDaily = gameDao.getDailyLogin()
        // Simple synchronous fetch of current state
        val state = getCurrentDailyStateSnapshot()

        if (state.lastClaimEpochDay == todayEpochDay) {
            return null // Already claimed today
        }

        val isConsecutive = (todayEpochDay - state.lastClaimEpochDay) == 1L
        val nextStreak = if (isConsecutive) {
            (state.streakDays % 7) + 1
        } else {
            1
        }

        val reward = DailyRewardCatalog.REWARDS.getOrNull(nextStreak - 1)
            ?: DailyRewardCatalog.REWARDS.first()

        val updatedDaily = state.copy(
            lastClaimEpochDay = todayEpochDay,
            streakDays = nextStreak,
            totalClaimedCount = state.totalClaimedCount + 1
        )
        gameDao.upsertDailyLogin(updatedDaily)

        // Award stats credits and xp
        val stats = getCurrentStatsSnapshot()
        val updatedStats = stats.copy(
            credits = stats.credits + reward.rewardCredits,
            xp = stats.xp + reward.rewardXp
        )
        gameDao.upsertUserStats(updatedStats)
        syncPlayerLeaderboardScore(updatedStats)

        return reward.rewardCredits to reward.rewardXp
    }

    suspend fun updateTheme(themeMode: ThemeMode) {
        val stats = getCurrentStatsSnapshot()
        gameDao.upsertUserStats(stats.copy(selectedTheme = themeMode.name))
    }

    suspend fun updateDifficulty(difficulty: Difficulty) {
        val stats = getCurrentStatsSnapshot()
        gameDao.upsertUserStats(stats.copy(selectedDifficulty = difficulty.name))
    }

    suspend fun toggleHaptics(enabled: Boolean) {
        val stats = getCurrentStatsSnapshot()
        gameDao.upsertUserStats(stats.copy(hapticsEnabled = enabled))
    }

    suspend fun toggleSound(enabled: Boolean) {
        val stats = getCurrentStatsSnapshot()
        gameDao.upsertUserStats(stats.copy(soundEnabled = enabled))
    }

    suspend fun updatePlayerName(newName: String) {
        val trimmed = newName.trim().uppercase()
        if (trimmed.isNotEmpty()) {
            val stats = getCurrentStatsSnapshot()
            val updated = stats.copy(playerName = trimmed)
            gameDao.upsertUserStats(updated)
            syncPlayerLeaderboardScore(updated)
        }
    }

    private suspend fun getCurrentStatsSnapshot(): UserStatsEntity {
        return gameDao.getUserStatsOnce() ?: UserStatsEntity().also {
            gameDao.upsertUserStats(it)
        }
    }

    private suspend fun getCurrentDailyStateSnapshot(): DailyLoginEntity {
        return gameDao.getDailyLoginOnce() ?: DailyLoginEntity().also {
            gameDao.upsertDailyLogin(it)
        }
    }
}
