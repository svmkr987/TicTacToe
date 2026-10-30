package com.example.ui.viewmodel

import android.content.Context
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.DailyLoginEntity
import com.example.data.local.LeaderboardEntry
import com.example.data.local.MatchHistoryEntity
import com.example.data.local.UserStatsEntity
import com.example.data.model.Difficulty
import com.example.data.model.GameMode
import com.example.data.model.GameStatus
import com.example.data.model.PlayerMark
import com.example.data.model.ThemeMode
import com.example.data.model.WinningLine
import com.example.data.repository.GameRepository
import com.example.engine.TicTacToeEngine
import com.example.util.HapticHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class GameUiState(
    val board: List<PlayerMark> = List(9) { PlayerMark.NONE },
    val currentTurn: PlayerMark = PlayerMark.X,
    val gameStatus: GameStatus = GameStatus.NOT_STARTED,
    val winningLine: WinningLine? = null,
    val isAiThinking: Boolean = false,
    val gameMode: GameMode = GameMode.VS_AI,
    val difficulty: Difficulty = Difficulty.OPERATIVE,
    val lastMoveIndex: Int? = null,
    val moveCount: Int = 0,
    val aiStatusMessage: String = "GRID MATRIX READY",
    val showGameEndDialog: Boolean = false,
    val showDailyRewardModal: Boolean = false,
    val showAddFriendDialog: Boolean = false,
    val dailyClaimResult: String? = null
)

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val userStats: StateFlow<UserStatsEntity> = repository.userStats
        .map { it ?: UserStatsEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStatsEntity()
        )

    val globalLeaderboard: StateFlow<List<LeaderboardEntry>> = repository.globalLeaderboard
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val friendsLeaderboard: StateFlow<List<LeaderboardEntry>> = repository.friendsLeaderboard
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentMatches: StateFlow<List<MatchHistoryEntity>> = repository.recentMatches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dailyLoginState: StateFlow<DailyLoginEntity> = repository.dailyLoginState
        .map { it ?: DailyLoginEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyLoginEntity()
        )

    init {
        // Sync difficulty from saved stats when available
        viewModelScope.launch {
            repository.userStats.collect { stats ->
                if (stats != null) {
                    val savedDiff = try {
                        Difficulty.valueOf(stats.selectedDifficulty)
                    } catch (_: Exception) {
                        Difficulty.OPERATIVE
                    }
                    _uiState.update { it.copy(difficulty = savedDiff) }
                }
            }
        }
    }

    fun onCellClicked(index: Int, context: Context, haptic: HapticFeedback?) {
        val state = _uiState.value
        val stats = userStats.value

        // Ignore clicks if game is finished, cell occupied, or AI is thinking
        if (state.board[index] != PlayerMark.NONE ||
            state.gameStatus in listOf(GameStatus.X_WON, GameStatus.O_WON, GameStatus.DRAW) ||
            state.isAiThinking
        ) {
            return
        }

        // Haptic feedback for user move
        HapticHelper.vibrateMove(context, haptic, stats.hapticsEnabled)

        val newBoard = state.board.toMutableList()
        val mark = state.currentTurn
        newBoard[index] = mark
        val nextMoveCount = state.moveCount + 1

        val evalStatus = TicTacToeEngine.evaluateGameStatus(newBoard)
        val winningLine = TicTacToeEngine.checkWinningLine(newBoard)

        if (evalStatus != GameStatus.PLAYING) {
            // Game concluded
            handleGameEnd(evalStatus, winningLine, newBoard, nextMoveCount, context, stats)
            return
        }

        // Switch turn
        val nextTurn = mark.opponent()
        _uiState.update {
            it.copy(
                board = newBoard,
                currentTurn = nextTurn,
                gameStatus = GameStatus.PLAYING,
                lastMoveIndex = index,
                moveCount = nextMoveCount,
                aiStatusMessage = if (state.gameMode == GameMode.VS_AI) "AI SYNAPSE ENGAGED..." else "${nextTurn.name}'s MOVE"
            )
        }

        // Trigger AI move if VS_AI and it's AI's turn
        if (state.gameMode == GameMode.VS_AI && nextTurn == PlayerMark.O) {
            executeAiTurn(newBoard, nextMoveCount, context, stats)
        }
    }

    private fun executeAiTurn(
        currentBoard: List<PlayerMark>,
        currentMoves: Int,
        context: Context,
        stats: UserStatsEntity
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAiThinking = true,
                    aiStatusMessage = "AI COMPUTING VECTOR TRAJECTORY..."
                )
            }

            // Realistic thinking latency (400ms - 650ms)
            delay(520L)

            val aiMove = TicTacToeEngine.calculateAiMove(
                board = currentBoard,
                difficulty = _uiState.value.difficulty,
                aiMark = PlayerMark.O,
                humanMark = PlayerMark.X
            )

            if (aiMove in 0..8) {
                HapticHelper.vibrateAiMove(context, stats.hapticsEnabled)

                val newBoard = currentBoard.toMutableList()
                newBoard[aiMove] = PlayerMark.O
                val nextMoveCount = currentMoves + 1

                val evalStatus = TicTacToeEngine.evaluateGameStatus(newBoard)
                val winningLine = TicTacToeEngine.checkWinningLine(newBoard)

                if (evalStatus != GameStatus.PLAYING) {
                    handleGameEnd(evalStatus, winningLine, newBoard, nextMoveCount, context, stats)
                } else {
                    _uiState.update {
                        it.copy(
                            board = newBoard,
                            currentTurn = PlayerMark.X,
                            isAiThinking = false,
                            lastMoveIndex = aiMove,
                            moveCount = nextMoveCount,
                            aiStatusMessage = "YOUR TURN • MATRIX READY"
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isAiThinking = false) }
            }
        }
    }

    private fun handleGameEnd(
        status: GameStatus,
        winningLine: WinningLine?,
        board: List<PlayerMark>,
        movesCount: Int,
        context: Context,
        stats: UserStatsEntity
    ) {
        _uiState.update {
            it.copy(
                board = board,
                gameStatus = status,
                winningLine = winningLine,
                isAiThinking = false,
                showGameEndDialog = true,
                aiStatusMessage = when (status) {
                    GameStatus.X_WON -> "VICTORY • MATRIX OVERRIDDEN"
                    GameStatus.O_WON -> "DEFEAT • CORE BREACH"
                    GameStatus.DRAW -> "DRAW • GRID STALEMATE"
                    else -> "COMPLETE"
                }
            )
        }

        // Haptic feedback
        if (status == GameStatus.X_WON) {
            HapticHelper.vibrateWin(context, stats.hapticsEnabled)
        } else {
            HapticHelper.vibrateDefeatOrDraw(context, stats.hapticsEnabled)
        }

        // Persist to database if VS_AI
        if (_uiState.value.gameMode == GameMode.VS_AI) {
            viewModelScope.launch {
                repository.recordMatchResult(
                    result = status,
                    difficulty = _uiState.value.difficulty,
                    movesCount = movesCount,
                    opponentName = "${_uiState.value.difficulty.title} AI"
                )
            }
        }
    }

    fun restartGame() {
        _uiState.update {
            it.copy(
                board = List(9) { PlayerMark.NONE },
                currentTurn = PlayerMark.X,
                gameStatus = GameStatus.NOT_STARTED,
                winningLine = null,
                isAiThinking = false,
                lastMoveIndex = null,
                moveCount = 0,
                showGameEndDialog = false,
                aiStatusMessage = "GRID MATRIX READY"
            )
        }
    }

    fun dismissEndDialog() {
        _uiState.update { it.copy(showGameEndDialog = false) }
    }

    fun setDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(difficulty = difficulty) }
        viewModelScope.launch {
            repository.updateDifficulty(difficulty)
        }
        restartGame()
    }

    fun setGameMode(mode: GameMode) {
        _uiState.update { it.copy(gameMode = mode) }
        restartGame()
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.updateTheme(mode)
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleHaptics(enabled)
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleSound(enabled)
        }
    }

    fun updatePlayerName(newName: String) {
        viewModelScope.launch {
            repository.updatePlayerName(newName)
        }
    }

    fun openDailyRewardModal() {
        _uiState.update { it.copy(showDailyRewardModal = true, dailyClaimResult = null) }
    }

    fun closeDailyRewardModal() {
        _uiState.update { it.copy(showDailyRewardModal = false) }
    }

    fun claimDailyReward(context: Context) {
        viewModelScope.launch {
            val rewardPair = repository.claimDailyReward()
            if (rewardPair != null) {
                val (credits, xp) = rewardPair
                HapticHelper.vibrateWin(context, userStats.value.hapticsEnabled)
                _uiState.update {
                    it.copy(
                        dailyClaimResult = "+$credits Cyber Credits & +$xp XP Acquired!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(dailyClaimResult = "Uplink already synchronized today!")
                }
            }
        }
    }

    fun openAddFriendDialog() {
        _uiState.update { it.copy(showAddFriendDialog = true) }
    }

    fun closeAddFriendDialog() {
        _uiState.update { it.copy(showAddFriendDialog = false) }
    }

    fun addFriend(name: String, score: Int, region: String) {
        viewModelScope.launch {
            repository.addFriendToLeaderboard(name, score, region)
            closeAddFriendDialog()
        }
    }

    fun isDailyRewardAvailable(): Boolean {
        val today = LocalDate.now().toEpochDay()
        return dailyLoginState.value.lastClaimEpochDay != today
    }
}

class GameViewModelFactory(
    private val repository: GameRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
