package com.arglass.tetris.game

enum class TetrominoType {
    I, O, T, S, Z, J, L
}

data class Point(val row: Int, val col: Int)

/**
 * Represents a Tetris piece with its type, rotation state, and board position.
 * boardRow/boardCol define the top-left offset of the piece's bounding box on the board.
 * Blocks at row < 0 are above the board (allowed during spawn, not rendered).
 */
class Tetromino private constructor(
    val type: TetrominoType,
    private var rotation: Int,
    var boardRow: Int,
    var boardCol: Int
) {
    /** Returns block positions in local (bounding-box) coordinates. */
    fun getBlocks(): List<Point> = SHAPES.getValue(type)[rotation]

    /** Returns block positions in board coordinates. */
    fun getBoardBlocks(): List<Point> = getBlocks().map {
        Point(it.row + boardRow, it.col + boardCol)
    }

    /** Returns a new Tetromino rotated 90° clockwise without modifying this one. */
    fun rotatedClockwise(): Tetromino {
        val rotations = SHAPES.getValue(type)
        return Tetromino(type, (rotation + 1) % rotations.size, boardRow, boardCol)
    }

    fun copy(): Tetromino = Tetromino(type, rotation, boardRow, boardCol)

    fun movedBy(dRow: Int, dCol: Int): Tetromino =
        Tetromino(type, rotation, boardRow + dRow, boardCol + dCol)

    companion object {
        // Standard spawn column centres a piece in a 10-wide board.
        private const val SPAWN_COL = 3

        fun spawn(type: TetrominoType): Tetromino = Tetromino(type, 0, 0, SPAWN_COL)

        fun spawnRandom(): Tetromino = spawn(TetrominoType.values().random())

        /**
         * Piece shapes encoded as lists of (row, col) offsets per rotation state.
         * Row 0 is the top of the bounding box; col 0 is the left.
         */
        val SHAPES: Map<TetrominoType, List<List<Point>>> = mapOf(
            TetrominoType.I to listOf(
                listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3)),
                listOf(Point(0, 2), Point(1, 2), Point(2, 2), Point(3, 2)),
                listOf(Point(2, 0), Point(2, 1), Point(2, 2), Point(2, 3)),
                listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))
            ),
            TetrominoType.O to listOf(
                listOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))
            ),
            TetrominoType.T to listOf(
                listOf(Point(0, 1), Point(1, 0), Point(1, 1), Point(1, 2)),
                listOf(Point(0, 1), Point(1, 1), Point(1, 2), Point(2, 1)),
                listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 1)),
                listOf(Point(0, 1), Point(1, 0), Point(1, 1), Point(2, 1))
            ),
            TetrominoType.S to listOf(
                listOf(Point(0, 1), Point(0, 2), Point(1, 0), Point(1, 1)),
                listOf(Point(0, 1), Point(1, 1), Point(1, 2), Point(2, 2)),
                listOf(Point(1, 1), Point(1, 2), Point(2, 0), Point(2, 1)),
                listOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(2, 1))
            ),
            TetrominoType.Z to listOf(
                listOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(1, 2)),
                listOf(Point(0, 2), Point(1, 1), Point(1, 2), Point(2, 1)),
                listOf(Point(1, 0), Point(1, 1), Point(2, 1), Point(2, 2)),
                listOf(Point(0, 1), Point(1, 0), Point(1, 1), Point(2, 0))
            ),
            TetrominoType.J to listOf(
                listOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(1, 2)),
                listOf(Point(0, 1), Point(0, 2), Point(1, 1), Point(2, 1)),
                listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 2)),
                listOf(Point(0, 1), Point(1, 1), Point(2, 0), Point(2, 1))
            ),
            TetrominoType.L to listOf(
                listOf(Point(0, 2), Point(1, 0), Point(1, 1), Point(1, 2)),
                listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 2)),
                listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 0)),
                listOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(2, 1))
            )
        )
    }
}
