package com.arglass.tetris.game

/**
 * Tracks the player's score and total lines cleared.
 * Scoring table (standard Tetris):
 *   1 line  → 100 pts
 *   2 lines → 300 pts
 *   3 lines → 500 pts
 *   4 lines → 800 pts (Tetris)
 *   Soft drop → 1 pt per row
 */
class ScoreManager {

    var score: Int = 0
        private set

    var totalLinesCleared: Int = 0
        private set

    fun addLinesCleared(lines: Int) {
        totalLinesCleared += lines
        score += when (lines) {
            1 -> 100
            2 -> 300
            3 -> 500
            4 -> 800
            else -> 0
        }
    }

    fun addSoftDrop(rows: Int) {
        score += rows
    }

    fun reset() {
        score = 0
        totalLinesCleared = 0
    }
}
