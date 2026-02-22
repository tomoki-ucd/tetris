package com.arglass.tetris.game

/**
 * Core game engine: manages game state, the active piece, drop timing,
 * and wires together Board + ScoreManager.
 *
 * Call [update] every frame tick (target: 30 FPS / ~33 ms per frame).
 * Call input methods from the gesture handler on the UI thread.
 */
class GameEngine {

    enum class State { MENU, PLAYING, GAME_OVER }

    val board = Board()
    val scoreManager = ScoreManager()

    var state: State = State.MENU
        private set

    var activePiece: Tetromino? = null
        private set

    var nextPiece: Tetromino = Tetromino.spawnRandom()
        private set

    // Drop timing: piece falls 1 row per DROP_INTERVAL_MS milliseconds.
    private val DROP_INTERVAL_MS = 1000L
    private var dropAccumulatorMs = 0L

    /** Callback invoked whenever the board changes; used to trigger a redraw. */
    var onBoardChanged: (() -> Unit)? = null

    // ── Public API ────────────────────────────────────────────────────────────

    fun startGame() {
        board.reset()
        scoreManager.reset()
        nextPiece = Tetromino.spawnRandom()
        spawnNext()
        state = State.PLAYING
        dropAccumulatorMs = 0L
        onBoardChanged?.invoke()
    }

    fun returnToMenu() {
        state = State.MENU
        onBoardChanged?.invoke()
    }

    /**
     * Advance the game by [deltaMs] milliseconds.
     * Should be called from the game loop at a fixed interval.
     */
    fun update(deltaMs: Long) {
        if (state != State.PLAYING) return

        dropAccumulatorMs += deltaMs
        if (dropAccumulatorMs >= DROP_INTERVAL_MS) {
            dropAccumulatorMs -= DROP_INTERVAL_MS
            gravityDrop()
        }
    }

    // ── Input actions (call from gesture handler) ─────────────────────────────

    fun onTap() {
        when (state) {
            State.MENU, State.GAME_OVER -> startGame()
            State.PLAYING -> rotateActive()
        }
    }

    fun onSwipeLeft() {
        if (state != State.PLAYING) return
        moveActive(dCol = -1)
    }

    fun onSwipeRight() {
        if (state != State.PLAYING) return
        moveActive(dCol = 1)
    }

    fun onLongPress() {
        if (state == State.PLAYING) returnToMenu()
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun spawnNext() {
        val piece = nextPiece
        nextPiece = Tetromino.spawnRandom()
        if (!board.isValidPosition(piece)) {
            // Cannot place new piece → game over
            state = State.GAME_OVER
            activePiece = null
        } else {
            activePiece = piece
        }
    }

    private fun gravityDrop() {
        val piece = activePiece ?: return
        val moved = piece.movedBy(dRow = 1, dCol = 0)
        if (board.isValidPosition(moved)) {
            activePiece = moved
        } else {
            // Lock the piece, clear lines, spawn next
            board.placeTetromino(piece)
            val cleared = board.clearLines()
            if (cleared > 0) scoreManager.addLinesCleared(cleared)
            if (board.isGameOver()) {
                state = State.GAME_OVER
                activePiece = null
            } else {
                spawnNext()
            }
        }
        onBoardChanged?.invoke()
    }

    private fun moveActive(dCol: Int) {
        val piece = activePiece ?: return
        val moved = piece.movedBy(dRow = 0, dCol = dCol)
        if (board.isValidPosition(moved)) {
            activePiece = moved
            onBoardChanged?.invoke()
        }
    }

    private fun rotateActive() {
        val piece = activePiece ?: return
        val rotated = piece.rotatedClockwise()

        // Try plain rotation first, then wall-kick offsets.
        val kicks = listOf(0 to 0, -1 to 0, 1 to 0, -2 to 0, 2 to 0)
        for ((dCol, dRow) in kicks) {
            val candidate = rotated.movedBy(dRow = dRow, dCol = dCol)
            if (board.isValidPosition(candidate)) {
                activePiece = candidate
                onBoardChanged?.invoke()
                return
            }
        }
        // Rotation not possible — silently ignore
    }
}
