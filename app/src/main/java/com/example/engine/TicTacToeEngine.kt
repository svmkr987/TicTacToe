package com.example.engine

import com.example.data.model.Difficulty
import com.example.data.model.GameStatus
import com.example.data.model.PlayerMark
import com.example.data.model.WinningLine
import com.example.data.model.WinningLineType
import kotlin.random.Random

object TicTacToeEngine {

    val WINNING_COMBOS = listOf(
        Triple(0, 1, 2) to WinningLineType.ROW_0,
        Triple(3, 4, 5) to WinningLineType.ROW_1,
        Triple(6, 7, 8) to WinningLineType.ROW_2,
        Triple(0, 3, 6) to WinningLineType.COL_0,
        Triple(1, 4, 7) to WinningLineType.COL_1,
        Triple(2, 5, 8) to WinningLineType.COL_2,
        Triple(0, 4, 8) to WinningLineType.DIAG_MAIN,
        Triple(2, 4, 6) to WinningLineType.DIAG_ANTI
    )

    fun checkWinningLine(board: List<PlayerMark>): WinningLine? {
        for ((triple, type) in WINNING_COMBOS) {
            val (a, b, c) = triple
            if (board[a] != PlayerMark.NONE &&
                board[a] == board[b] &&
                board[b] == board[c]
            ) {
                return WinningLine(listOf(a, b, c), type)
            }
        }
        return null
    }

    fun evaluateGameStatus(board: List<PlayerMark>): GameStatus {
        val winningLine = checkWinningLine(board)
        if (winningLine != null) {
            val winnerMark = board[winningLine.indices[0]]
            return if (winnerMark == PlayerMark.X) GameStatus.X_WON else GameStatus.O_WON
        }
        if (board.none { it == PlayerMark.NONE }) {
            return GameStatus.DRAW
        }
        return GameStatus.PLAYING
    }

    fun getEmptyCells(board: List<PlayerMark>): List<Int> {
        return board.indices.filter { board[it] == PlayerMark.NONE }
    }

    /**
     * Calculates the AI move based on the selected difficulty.
     * AI plays as PlayerMark.O by default against PlayerMark.X.
     */
    fun calculateAiMove(
        board: List<PlayerMark>,
        difficulty: Difficulty,
        aiMark: PlayerMark = PlayerMark.O,
        humanMark: PlayerMark = PlayerMark.X
    ): Int {
        val emptyCells = getEmptyCells(board)
        if (emptyCells.isEmpty()) return -1
        if (emptyCells.size == 1) return emptyCells.first()

        return when (difficulty) {
            Difficulty.RECRUIT -> calculateRecruitMove(board, emptyCells, aiMark, humanMark)
            Difficulty.OPERATIVE -> calculateOperativeMove(board, emptyCells, aiMark, humanMark)
            Difficulty.SENTINEL -> calculateSentinelMove(board, emptyCells, aiMark, humanMark)
            Difficulty.NEXUS -> calculateNexusMinimaxMove(board, aiMark, humanMark)
        }
    }

    private fun calculateRecruitMove(
        board: List<PlayerMark>,
        emptyCells: List<Int>,
        aiMark: PlayerMark,
        humanMark: PlayerMark
    ): Int {
        // 30% chance to make smart tactical move, 70% random
        if (Random.nextFloat() < 0.30f) {
            findWinningMove(board, emptyCells, aiMark)?.let { return it }
            findWinningMove(board, emptyCells, humanMark)?.let { return it }
        }
        return emptyCells.random()
    }

    private fun calculateOperativeMove(
        board: List<PlayerMark>,
        emptyCells: List<Int>,
        aiMark: PlayerMark,
        humanMark: PlayerMark
    ): Int {
        // 1. Can AI win?
        findWinningMove(board, emptyCells, aiMark)?.let { return it }

        // 2. Can player win? Block it!
        findWinningMove(board, emptyCells, humanMark)?.let { return it }

        // 3. Take center if available
        if (board[4] == PlayerMark.NONE && Random.nextFloat() < 0.85f) return 4

        // 4. Take available corners
        val availableCorners = listOf(0, 2, 6, 8).filter { it in emptyCells }
        if (availableCorners.isNotEmpty() && Random.nextFloat() < 0.70f) {
            return availableCorners.random()
        }

        // 5. Random empty
        return emptyCells.random()
    }

    private fun calculateSentinelMove(
        board: List<PlayerMark>,
        emptyCells: List<Int>,
        aiMark: PlayerMark,
        humanMark: PlayerMark
    ): Int {
        // 85% optimal minimax, 15% operative heuristic
        return if (Random.nextFloat() < 0.85f) {
            calculateNexusMinimaxMove(board, aiMark, humanMark)
        } else {
            calculateOperativeMove(board, emptyCells, aiMark, humanMark)
        }
    }

    private fun calculateNexusMinimaxMove(
        board: List<PlayerMark>,
        aiMark: PlayerMark,
        humanMark: PlayerMark
    ): Int {
        // First move optimization: if center empty, pick center, or pick corner
        val emptyCells = getEmptyCells(board)
        if (emptyCells.size == 9) {
            // Pick center or corner
            return listOf(4, 0, 2, 6, 8).random()
        }
        if (emptyCells.size == 8 && board[4] == PlayerMark.NONE) {
            return 4
        }

        var bestScore = Int.MIN_VALUE
        var bestMove = emptyCells.first()

        for (cell in emptyCells) {
            val nextBoard = board.toMutableList()
            nextBoard[cell] = aiMark
            val score = minimax(nextBoard, 0, false, aiMark, humanMark, Int.MIN_VALUE, Int.MAX_VALUE)
            if (score > bestScore) {
                bestScore = score
                bestMove = cell
            }
        }
        return bestMove
    }

    private fun minimax(
        board: List<PlayerMark>,
        depth: Int,
        isMaximizing: Boolean,
        aiMark: PlayerMark,
        humanMark: PlayerMark,
        alpha: Int,
        beta: Int
    ): Int {
        val winningLine = checkWinningLine(board)
        if (winningLine != null) {
            val winner = board[winningLine.indices[0]]
            return if (winner == aiMark) 10 - depth else depth - 10
        }

        val emptyCells = getEmptyCells(board)
        if (emptyCells.isEmpty()) return 0 // Draw

        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (cell in emptyCells) {
                val nextBoard = board.toMutableList()
                nextBoard[cell] = aiMark
                val evaluation = minimax(nextBoard, depth + 1, false, aiMark, humanMark, currentAlpha, currentBeta)
                maxEval = maxOf(maxEval, evaluation)
                currentAlpha = maxOf(currentAlpha, evaluation)
                if (currentBeta <= currentAlpha) break
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (cell in emptyCells) {
                val nextBoard = board.toMutableList()
                nextBoard[cell] = humanMark
                val evaluation = minimax(nextBoard, depth + 1, true, aiMark, humanMark, currentAlpha, currentBeta)
                minEval = minOf(minEval, evaluation)
                currentBeta = minOf(currentBeta, evaluation)
                if (currentBeta <= currentAlpha) break
            }
            return minEval
        }
    }

    private fun findWinningMove(
        board: List<PlayerMark>,
        emptyCells: List<Int>,
        mark: PlayerMark
    ): Int? {
        for (cell in emptyCells) {
            val testBoard = board.toMutableList()
            testBoard[cell] = mark
            if (checkWinningLine(testBoard) != null) {
                return cell
            }
        }
        return null
    }
}
