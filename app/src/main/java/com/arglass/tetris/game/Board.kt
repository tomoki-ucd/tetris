package com.arglass.tetris.game

/**
 * 10x20 Tetris board.
 * Grid values: 0 = empty, 1–7 = locked piece (TetrominoType.ordinal + 1).
 * Row 0 is the top; row ROWS-1 is the bottom.
 */
class Board {

    companion object {
        const val COLS = 10
        const val ROWS = 20
        const val EMPTY = 0
    }

    private val grid = Array(ROWS) { IntArray(COLS) { EMPTY } }

    fun getCell(row: Int, col: Int): Int = grid[row][col]

    fun getGrid(): Array<IntArray> = Array(ROWS) { grid[it].clone() }

    /**
     * Returns true if the tetromino can occupy its current board position:
     * within horizontal bounds, above the floor, and not overlapping locked blocks.
     * Blocks above row 0 (still entering the board) are allowed.
     */
    fun isValidPosition(tetromino: Tetromino): Boolean {
        for (block in tetromino.getBoardBlocks()) {
            if (block.col < 0 || block.col >= COLS) return false
            if (block.row >= ROWS) return false
            if (block.row >= 0 && grid[block.row][block.col] != EMPTY) return false
        }
        return true
    }

    /** Locks the tetromino into the grid; blocks above the board are ignored. */
    fun placeTetromino(tetromino: Tetromino) {
        val cellValue = tetromino.type.ordinal + 1
        for (block in tetromino.getBoardBlocks()) {
            if (block.row in 0 until ROWS && block.col in 0 until COLS) {
                grid[block.row][block.col] = cellValue
            }
        }
    }

    /**
     * Clears all full rows, drops remaining rows down, and returns the count cleared (0–4).
     */
    fun clearLines(): Int {
        val fullIndices = (0 until ROWS).filter { row -> grid[row].all { it != EMPTY } }
        if (fullIndices.isEmpty()) return 0

        val fullSet = fullIndices.toHashSet()
        val remaining = (0 until ROWS).filterNot { it in fullSet }.map { grid[it].clone() }
        val blanks = List(fullIndices.size) { IntArray(COLS) { EMPTY } }
        val newRows = blanks + remaining

        for (i in 0 until ROWS) grid[i] = newRows[i]
        return fullIndices.size
    }

    /** Game over when any cell in the top row is occupied. */
    fun isGameOver(): Boolean = grid[0].any { it != EMPTY }

    fun reset() {
        for (row in grid) row.fill(EMPTY)
    }
}
