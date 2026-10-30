package com.example.data.model

data class DailyReward(
    val dayNumber: Int,
    val title: String,
    val rewardCredits: Int,
    val rewardXp: Int,
    val bonusItem: String? = null,
    val iconName: String = "credit"
)

object DailyRewardCatalog {
    val REWARDS = listOf(
        DailyReward(1, "Daily Uplink", 100, 50, null),
        DailyReward(2, "Matrix Surge", 175, 75, "Neon Cyan Trail"),
        DailyReward(3, "Grid Infiltration", 250, 100, "Tactician Badge"),
        DailyReward(4, "Cyber Overclock", 350, 150, "Laser Audio FX"),
        DailyReward(5, "Subroutine Sync", 500, 200, "2x Score Booster"),
        DailyReward(6, "Neural Overdrive", 750, 300, "Cyber Elite Title"),
        DailyReward(7, "Nexus Ascension", 1500, 600, "Quantum Nexus Skin")
    )
}
