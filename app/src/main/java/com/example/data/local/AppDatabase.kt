package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserStatsEntity::class,
        LeaderboardEntry::class,
        MatchHistoryEntity::class,
        DailyLoginEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nexus_tictactoe_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedDatabase(database.gameDao())
                    }
                }
            }
        }

        suspend fun seedDatabase(dao: GameDao) {
            // Seed initial User Stats
            dao.upsertUserStats(UserStatsEntity())

            // Seed initial Daily Login
            dao.upsertDailyLogin(DailyLoginEntity())

            // Seed initial Global Cyber League & Friends
            val defaultRivals = listOf(
                LeaderboardEntry(
                    id = 1,
                    playerName = "VEX_SYNTH",
                    score = 4250,
                    rank = 1,
                    avatarId = "robot",
                    winRate = 96,
                    badge = "GRANDMASTER",
                    isFriend = false,
                    isUser = false,
                    region = "Orbital Station Alpha"
                ),
                LeaderboardEntry(
                    id = 2,
                    playerName = "CYBER_RUNNER_01",
                    score = 350,
                    rank = 2,
                    avatarId = "user",
                    winRate = 0,
                    badge = "PRODIGY",
                    isFriend = false,
                    isUser = true,
                    region = "Sector 7 Grid"
                ),
                LeaderboardEntry(
                    id = 3,
                    playerName = "NEO_KAI",
                    score = 3400,
                    rank = 3,
                    avatarId = "cyborg",
                    winRate = 89,
                    badge = "DIAMOND",
                    isFriend = true,
                    isUser = false,
                    region = "Neo-Tokyo Central"
                ),
                LeaderboardEntry(
                    id = 4,
                    playerName = "GHOST_BYTE",
                    score = 2950,
                    rank = 4,
                    avatarId = "ghost",
                    winRate = 84,
                    badge = "PLATINUM",
                    isFriend = false,
                    isUser = false,
                    region = "Silicon Nexus"
                ),
                LeaderboardEntry(
                    id = 5,
                    playerName = "AURA_LYRA",
                    score = 2100,
                    rank = 5,
                    avatarId = "neon",
                    winRate = 78,
                    badge = "GOLD",
                    isFriend = true,
                    isUser = false,
                    region = "Moon Colony 4"
                ),
                LeaderboardEntry(
                    id = 6,
                    playerName = "ZERO_PULSE",
                    score = 1650,
                    rank = 6,
                    avatarId = "zap",
                    winRate = 71,
                    badge = "SILVER",
                    isFriend = false,
                    isUser = false,
                    region = "Kyoto Underground"
                ),
                LeaderboardEntry(
                    id = 7,
                    playerName = "KRONOS_AI",
                    score = 1100,
                    rank = 7,
                    avatarId = "cpu",
                    winRate = 65,
                    badge = "BRONZE",
                    isFriend = false,
                    isUser = false,
                    region = "Neural Grid 09"
                )
            )
            dao.insertLeaderboardEntries(defaultRivals)
        }
    }
}
