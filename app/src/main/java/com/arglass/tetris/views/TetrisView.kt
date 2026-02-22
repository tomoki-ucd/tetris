package com.arglass.tetris.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.arglass.tetris.game.Board
import com.arglass.tetris.game.GameEngine
import com.arglass.tetris.game.TetrominoType

/**
 * Custom view that renders the Tetris board in grayscale.
 *
 * Layout (landscape 640×480):
 *   - Main board:    left section, 10×20 grid
 *   - Side panel:    right section, shows next piece + score
 *
 * Grayscale palette:
 *   Black (0)      → background
 *   Dark gray (64) → grid lines
 *   White (255)    → active piece
 *   Light gray (192) → locked pieces (pattern varies by type)
 *   Medium gray (160) → next piece preview
 */
class TetrisView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var engine: GameEngine? = null
        set(value) {
            field = value
            value?.onBoardChanged = { postInvalidate() }
        }

    // Paints
    private val bgPaint = Paint().apply { color = Color.BLACK }
    private val gridPaint = Paint().apply {
        color = Color.argb(255, 64, 64, 64)
        strokeWidth = 1f
    }
    private val activePaint = Paint().apply { color = Color.WHITE }
    private val lockedPaint = Paint().apply { color = Color.argb(255, 192, 192, 192) }
    private val previewPaint = Paint().apply { color = Color.argb(255, 160, 160, 160) }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }
    private val borderPaint = Paint().apply {
        color = Color.argb(255, 128, 128, 128)
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val cellBorder = Paint().apply {
        color = Color.argb(255, 40, 40, 40)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    // Hatching paints for piece differentiation (same brightness, different texture)
    private val hatchPaints = Array(7) { _ ->
        Paint().apply {
            color = Color.argb(255, 192, 192, 192)
            strokeWidth = 1f
            alpha = 200
        }
    }

    // Layout metrics (computed in onSizeChanged)
    private var cellSize = 0f
    private var boardLeft = 0f
    private var boardTop = 0f
    private var panelLeft = 0f
    private var panelTop = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Board occupies ~65% of width; side panel gets the rest.
        val boardWidth = w * 0.62f
        cellSize = minOf(boardWidth / Board.COLS, h.toFloat() / Board.ROWS)

        boardLeft = (boardWidth - cellSize * Board.COLS) / 2f
        boardTop = (h - cellSize * Board.ROWS) / 2f

        panelLeft = boardLeft + cellSize * Board.COLS + 16f
        panelTop = boardTop

        textPaint.textSize = cellSize * 0.8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val eng = engine ?: return drawPlaceholder(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        when (eng.state) {
            GameEngine.State.MENU -> drawMenu(canvas)
            GameEngine.State.PLAYING -> drawGame(canvas, eng)
            GameEngine.State.GAME_OVER -> {
                drawGame(canvas, eng)
                drawGameOver(canvas)
            }
        }
    }

    // ── Draw helpers ──────────────────────────────────────────────────────────

    private fun drawPlaceholder(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("TETRIS", width / 2f, height / 2f, textPaint)
    }

    private fun drawMenu(canvas: Canvas) {
        textPaint.textAlign = Paint.Align.CENTER
        val cx = width / 2f
        val cy = height / 2f
        textPaint.textSize = cellSize * 1.4f
        canvas.drawText("TETRIS", cx, cy - cellSize * 2, textPaint)
        textPaint.textSize = cellSize * 0.8f
        canvas.drawText("TAP TO START", cx, cy, textPaint)
    }

    private fun drawGame(canvas: Canvas, eng: GameEngine) {
        drawBoard(canvas, eng)
        drawSidePanel(canvas, eng)
    }

    private fun drawBoard(canvas: Canvas, eng: GameEngine) {
        val bL = boardLeft
        val bT = boardTop
        val cs = cellSize

        // Background fill for board area
        canvas.drawRect(bL, bT, bL + cs * Board.COLS, bT + cs * Board.ROWS, bgPaint)

        // Locked cells
        val grid = eng.board.getGrid()
        for (row in 0 until Board.ROWS) {
            for (col in 0 until Board.COLS) {
                val v = grid[row][col]
                if (v != Board.EMPTY) {
                    drawCell(canvas, row, col, v, bL, bT, cs, locked = true)
                }
            }
        }

        // Active piece
        eng.activePiece?.getBoardBlocks()?.forEach { block ->
            if (block.row >= 0) {
                drawCell(canvas, block.row, block.col, 0, bL, bT, cs, locked = false)
            }
        }

        // Grid lines
        for (r in 0..Board.ROWS) {
            val y = bT + r * cs
            canvas.drawLine(bL, y, bL + cs * Board.COLS, y, gridPaint)
        }
        for (c in 0..Board.COLS) {
            val x = bL + c * cs
            canvas.drawLine(x, bT, x, bT + cs * Board.ROWS, gridPaint)
        }

        // Board border
        canvas.drawRect(bL, bT, bL + cs * Board.COLS, bT + cs * Board.ROWS, borderPaint)
    }

    private fun drawCell(
        canvas: Canvas,
        row: Int, col: Int,
        typeValue: Int,
        bL: Float, bT: Float, cs: Float,
        locked: Boolean
    ) {
        val x = bL + col * cs
        val y = bT + row * cs
        val paint = if (locked) lockedPaint else activePaint
        canvas.drawRect(x + 1, y + 1, x + cs - 1, y + cs - 1, paint)

        // Draw a simple hatch pattern based on type to differentiate pieces
        if (locked && typeValue in 1..7) {
            drawHatch(canvas, x, y, cs, typeValue)
        }

        canvas.drawRect(x + 1, y + 1, x + cs - 1, y + cs - 1, cellBorder)
    }

    private fun drawHatch(canvas: Canvas, x: Float, y: Float, cs: Float, typeValue: Int) {
        val hp = hatchPaints[typeValue - 1]
        hp.color = Color.argb(255, 120, 120, 120)
        when (typeValue % 4) {
            1 -> { // diagonal \
                canvas.drawLine(x + 1, y + 1, x + cs - 1, y + cs - 1, hp)
            }
            2 -> { // diagonal /
                canvas.drawLine(x + cs - 1, y + 1, x + 1, y + cs - 1, hp)
            }
            3 -> { // cross
                canvas.drawLine(x + 1, y + 1, x + cs - 1, y + cs - 1, hp)
                canvas.drawLine(x + cs - 1, y + 1, x + 1, y + cs - 1, hp)
            }
            0 -> { // horizontal mid-line
                canvas.drawLine(x + 1, y + cs / 2, x + cs - 1, y + cs / 2, hp)
            }
        }
    }

    private fun drawSidePanel(canvas: Canvas, eng: GameEngine) {
        val cs = cellSize
        val px = panelLeft
        var py = panelTop
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = cs * 0.75f

        // NEXT label
        canvas.drawText("NEXT", px, py + cs * 0.8f, textPaint)
        py += cs * 1.2f

        // Next piece preview (4×4 area)
        val previewSize = cs * 0.85f
        canvas.drawRect(px, py, px + previewSize * 4, py + previewSize * 4, bgPaint)
        canvas.drawRect(px, py, px + previewSize * 4, py + previewSize * 4, borderPaint)

        eng.nextPiece.getBlocks().forEach { block ->
            val bx = px + block.col * previewSize
            val by = py + block.row * previewSize
            canvas.drawRect(bx + 1, by + 1, bx + previewSize - 1, by + previewSize - 1, previewPaint)
        }

        py += previewSize * 4 + cs * 0.5f

        // Score
        canvas.drawText("SCORE", px, py + cs * 0.8f, textPaint)
        py += cs * 1.0f
        canvas.drawText("${eng.scoreManager.score}", px, py + cs * 0.8f, textPaint)
        py += cs * 1.5f

        // Lines
        canvas.drawText("LINES", px, py + cs * 0.8f, textPaint)
        py += cs * 1.0f
        canvas.drawText("${eng.scoreManager.totalLinesCleared}", px, py + cs * 0.8f, textPaint)
    }

    private fun drawGameOver(canvas: Canvas) {
        // Semi-transparent overlay
        val overlayPaint = Paint().apply { color = Color.argb(160, 0, 0, 0) }
        canvas.drawRect(boardLeft, boardTop,
            boardLeft + cellSize * Board.COLS, boardTop + cellSize * Board.ROWS, overlayPaint)

        textPaint.textAlign = Paint.Align.CENTER
        val cx = boardLeft + cellSize * Board.COLS / 2
        val cy = boardTop + cellSize * Board.ROWS / 2
        textPaint.textSize = cellSize * 1.2f
        canvas.drawText("GAME OVER", cx, cy - cellSize, textPaint)
        textPaint.textSize = cellSize * 0.75f
        canvas.drawText("TAP TO RESTART", cx, cy + cellSize, textPaint)
    }
}
