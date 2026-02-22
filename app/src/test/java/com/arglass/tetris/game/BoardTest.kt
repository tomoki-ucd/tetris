package com.arglass.tetris.game

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BoardTest {

    private lateinit var board: Board

    @Before
    fun setUp() {
        board = Board()
    }

    // ── Placement ─────────────────────────────────────────────────────────────

    @Test
    fun freshBoardIsEmpty() {
        val grid = board.getGrid()
        for (row in grid) row.forEach { assertEquals(Board.EMPTY, it) }
    }

    @Test
    fun placeTetrominoFillsCorrectCells() {
        val t = Tetromino.spawn(TetrominoType.O) // blocks at (0,0),(0,1),(1,0),(1,1) + offset col=3
        board.placeTetromino(t)
        // O at boardRow=0, boardCol=3 → cells (0,3),(0,4),(1,3),(1,4)
        assertEquals(TetrominoType.O.ordinal + 1, board.getCell(0, 3))
        assertEquals(TetrominoType.O.ordinal + 1, board.getCell(0, 4))
        assertEquals(TetrominoType.O.ordinal + 1, board.getCell(1, 3))
        assertEquals(TetrominoType.O.ordinal + 1, board.getCell(1, 4))
    }

    @Test
    fun placeIgnoresBlocksAboveBoard() {
        // Piece above the board (boardRow = -2) should not crash and place nothing
        val t = Tetromino.spawn(TetrominoType.O).movedBy(dRow = -2, dCol = 0)
        board.placeTetromino(t) // should not throw
        val grid = board.getGrid()
        for (row in grid) row.forEach { assertEquals(Board.EMPTY, it) }
    }

    // ── Collision detection ───────────────────────────────────────────────────

    @Test
    fun validPositionReturnsTrueForEmptyBoard() {
        val t = Tetromino.spawn(TetrominoType.T)
        assertTrue(board.isValidPosition(t))
    }

    @Test
    fun invalidPositionLeftWall() {
        val t = Tetromino.spawn(TetrominoType.I).movedBy(dRow = 0, dCol = -4)
        assertFalse(board.isValidPosition(t))
    }

    @Test
    fun invalidPositionRightWall() {
        val t = Tetromino.spawn(TetrominoType.I).movedBy(dRow = 0, dCol = 10)
        assertFalse(board.isValidPosition(t))
    }

    @Test
    fun invalidPositionFloor() {
        val t = Tetromino.spawn(TetrominoType.O).movedBy(dRow = Board.ROWS, dCol = 0)
        assertFalse(board.isValidPosition(t))
    }

    @Test
    fun invalidPositionOverlapsPlacedBlock() {
        val t1 = Tetromino.spawn(TetrominoType.O).movedBy(dRow = 18, dCol = 0)
        board.placeTetromino(t1)
        val t2 = Tetromino.spawn(TetrominoType.O).movedBy(dRow = 18, dCol = 0)
        assertFalse(board.isValidPosition(t2))
    }

    @Test
    fun pieceAboveBoardIsValid() {
        // Piece partially above the board during spawn should still be valid
        val t = Tetromino.spawn(TetrominoType.I).movedBy(dRow = -1, dCol = 0)
        assertTrue(board.isValidPosition(t))
    }

    // ── Line clearing ─────────────────────────────────────────────────────────

    @Test
    fun clearLinesReturnsZeroWhenNothingToRemove() {
        assertEquals(0, board.clearLines())
    }

    @Test
    fun clearsSingleFullRow() {
        fillRow(board, 19)
        assertEquals(1, board.clearLines())
        // Row 19 should now be empty
        for (col in 0 until Board.COLS) assertEquals(Board.EMPTY, board.getCell(19, col))
    }

    @Test
    fun clearsMultipleRows() {
        fillRow(board, 17)
        fillRow(board, 18)
        fillRow(board, 19)
        assertEquals(3, board.clearLines())
    }

    @Test
    fun rowsDropAfterClear() {
        // Place a marker in row 17, then fill rows 18 and 19.
        board.getGrid()  // just to confirm grid access
        fillRow(board, 19)
        fillRow(board, 18)
        // Manually set one cell in row 17 using a piece
        val marker = Tetromino.spawn(TetrominoType.O).movedBy(dRow = 17, dCol = 0)
        board.placeTetromino(marker)

        board.clearLines()  // clears rows 18 and 19

        // The O piece from row 17 should now sit at row 19 (dropped 2)
        assertEquals(TetrominoType.O.ordinal + 1, board.getCell(19, 3))
    }

    @Test
    fun clearsFourLinesAtOnce() {
        for (r in 16..19) fillRow(board, r)
        assertEquals(4, board.clearLines())
    }

    // ── Game over ─────────────────────────────────────────────────────────────

    @Test
    fun gameOverFalseOnFreshBoard() {
        assertFalse(board.isGameOver())
    }

    @Test
    fun gameOverTrueWhenTopRowOccupied() {
        fillRow(board, 0)
        assertTrue(board.isGameOver())
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    @Test
    fun resetClearsAllCells() {
        fillRow(board, 19)
        board.reset()
        val grid = board.getGrid()
        for (row in grid) row.forEach { assertEquals(Board.EMPTY, it) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Fills every cell in [row] with non-empty values.
     * Two I-pieces (rotation 2, cols 0-3 and 4-7) + one O-piece (cols 8-9).
     */
    private fun fillRow(board: Board, row: Int) {
        // I piece (rotation 2) has blocks at local row 2 → boardRow = row - 2
        val left = Tetromino.spawn(TetrominoType.I)
            .rotatedClockwise().rotatedClockwise() // row 2 in bounding box
            .movedBy(dRow = row - 2, dCol = -3)    // cols 0–3
        val right = Tetromino.spawn(TetrominoType.I)
            .rotatedClockwise().rotatedClockwise()
            .movedBy(dRow = row - 2, dCol = 1)     // cols 4–7
        board.placeTetromino(left)
        board.placeTetromino(right)
        // Fill remaining cols 8 and 9
        val extra = Tetromino.spawn(TetrominoType.O).movedBy(dRow = row, dCol = 5) // cols 8-9
        board.placeTetromino(extra)
    }
}
