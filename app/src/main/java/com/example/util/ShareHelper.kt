package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.local.UserStatsEntity

object ShareHelper {

    fun sharePlayerScore(context: Context, stats: UserStatsEntity, playerRank: Int = 2) {
        val shareMessage = buildString {
            append("⚡ NEXUS XO CYBER CHALLENGE ⚡\n")
            append("================================\n")
            append("👤 Agent: ${stats.playerName}\n")
            append("🏆 Rank: #$playerRank in Cyber League\n")
            append("🔥 Streak: ${stats.currentStreak} Wins (Best: ${stats.bestStreak})\n")
            append("🎯 Win Rate: ${stats.winRatePercent}% (${stats.wins}W / ${stats.losses}L / ${stats.draws}D)\n")
            append("⚡ Level ${stats.playerLevel}: ${stats.levelTitle}\n")
            append("💎 Cyber Credits: ${stats.credits}\n\n")
            append("\"Think you can beat the impossible Nexus AI core? Step onto the grid!\"\n")
            append("#NexusXO #CyberpunkGaming #TicTacToeAI")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Cyber Scorecard")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareVictory(context: Context, stats: UserStatsEntity, difficulty: String) {
        val shareMessage = buildString {
            append("🎮 VICTORY OVER $difficulty AI! 🎮\n")
            append("Just dismantled the $difficulty matrix in Nexus XO!\n")
            append("Current Win Streak: ${stats.currentStreak} 🔥\n")
            append("Total Triumphs: ${stats.wins} 🏆\n\n")
            append("Can your tactical algorithms match mine? Challenge me in Nexus XO!")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Victory")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
