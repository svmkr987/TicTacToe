package com.example.data.model

enum class Difficulty(
    val title: String,
    val subtitle: String,
    val winXp: Int,
    val winCredits: Int
) {
    RECRUIT("Recruit", "Casual AI • Training Matrix", 25, 15),
    OPERATIVE("Operative", "Adaptive Heuristic • Tactical Defense", 50, 35),
    SENTINEL("Sentinel", "Advanced Minimax • Aggressive Threat", 100, 75),
    NEXUS("Nexus Core", "Unbeatable Minimax • Mathematical Perfect", 250, 150)
}

enum class GameMode(val title: String) {
    VS_AI("VS Cyber AI"),
    PASS_AND_PLAY("Pass & Play (2P)")
}

enum class PlayerMark {
    NONE,
    X,
    O;

    fun opponent(): PlayerMark = when (this) {
        X -> O
        O -> X
        NONE -> NONE
    }
}

enum class GameStatus {
    NOT_STARTED,
    PLAYING,
    X_WON,
    O_WON,
    DRAW
}

data class WinningLine(
    val indices: List<Int>, // 3 cell indices: e.g. [0, 1, 2]
    val type: WinningLineType
)

enum class WinningLineType {
    ROW_0, ROW_1, ROW_2,
    COL_0, COL_1, COL_2,
    DIAG_MAIN, DIAG_ANTI
}
