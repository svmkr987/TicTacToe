package com.example

import com.example.data.model.Difficulty
import com.example.data.model.GameStatus
import com.example.data.model.PlayerMark
import com.example.engine.TicTacToeEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWinningLineDetection() {
        val board = listOf(
            PlayerMark.X, PlayerMark.X, PlayerMark.X,
            PlayerMark.O, PlayerMark.O, PlayerMark.NONE,
            PlayerMark.NONE, PlayerMark.NONE, PlayerMark.NONE
        )
        val winningLine = TicTacToeEngine.checkWinningLine(board)
        assertNotNull(winningLine)
        assertEquals(listOf(0, 1, 2), winningLine?.indices)
        assertEquals(GameStatus.X_WON, TicTacToeEngine.evaluateGameStatus(board))
    }

    @Test
    fun testDrawDetection() {
        val board = listOf(
            PlayerMark.X, PlayerMark.O, PlayerMark.X,
            PlayerMark.X, PlayerMark.O, PlayerMark.O,
            PlayerMark.O, PlayerMark.X, PlayerMark.X
        )
        assertEquals(GameStatus.DRAW, TicTacToeEngine.evaluateGameStatus(board))
    }

    @Test
    fun testAiBlocksPlayerWinOnOperative() {
        // Player X has 0 and 1, threatening 2. AI as O should block at 2.
        val board = listOf(
            PlayerMark.X, PlayerMark.X, PlayerMark.NONE,
            PlayerMark.O, PlayerMark.NONE, PlayerMark.NONE,
            PlayerMark.NONE, PlayerMark.NONE, PlayerMark.NONE
        )
        val move = TicTacToeEngine.calculateAiMove(
            board = board,
            difficulty = Difficulty.OPERATIVE,
            aiMark = PlayerMark.O,
            humanMark = PlayerMark.X
        )
        assertEquals(2, move)
    }

    @Test
    fun testNexusAiWinsWhenGivenOpportunity() {
        // AI has 0 and 1, cell 2 is empty. AI should take cell 2 for the win.
        val board = listOf(
            PlayerMark.O, PlayerMark.O, PlayerMark.NONE,
            PlayerMark.X, PlayerMark.X, PlayerMark.NONE,
            PlayerMark.NONE, PlayerMark.NONE, PlayerMark.NONE
        )
        val move = TicTacToeEngine.calculateAiMove(
            board = board,
            difficulty = Difficulty.NEXUS,
            aiMark = PlayerMark.O,
            humanMark = PlayerMark.X
        )
        assertEquals(2, move)
    }
}
