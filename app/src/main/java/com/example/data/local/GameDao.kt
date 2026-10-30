package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsOnce(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserStats(stats: UserStatsEntity)

    @Query("SELECT * FROM leaderboard_entries ORDER BY score DESC")
    fun getAllLeaderboard(): Flow<List<LeaderboardEntry>>

    @Query("SELECT * FROM leaderboard_entries WHERE isFriend = 1 OR isUser = 1 ORDER BY score DESC")
    fun getFriendsLeaderboard(): Flow<List<LeaderboardEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntry)

    @Update
    suspend fun updateLeaderboardEntry(entry: LeaderboardEntry)

    @Query("SELECT * FROM leaderboard_entries WHERE isUser = 1 LIMIT 1")
    suspend fun getUserLeaderboardEntry(): LeaderboardEntry?

    @Query("SELECT COUNT(*) FROM leaderboard_entries")
    suspend fun getLeaderboardCount(): Int

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 50")
    fun getRecentMatches(): Flow<List<MatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchHistoryEntity)

    @Query("SELECT * FROM daily_login_state WHERE id = 1")
    fun getDailyLogin(): Flow<DailyLoginEntity?>

    @Query("SELECT * FROM daily_login_state WHERE id = 1")
    suspend fun getDailyLoginOnce(): DailyLoginEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyLogin(state: DailyLoginEntity)
}
