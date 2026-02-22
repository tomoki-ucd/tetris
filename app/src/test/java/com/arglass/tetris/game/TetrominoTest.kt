package com.arglass.tetris.game

import org.junit.Assert.*
import org.junit.Test

class TetrominoTest {

    @Test
    fun spawnProducesCorrectType() {
        TetrominoType.values().forEach { type ->
            val t = Tetromino.spawn(type)
            assertEquals(type, t.type)
        }
    }

    @Test
    fun spawnHasFourBlocks() {
        TetrominoType.values().forEach { type ->
            val t = Tetromino.spawn(type)
            assertEquals("$type should have 4 blocks", 4, t.getBlocks().size)
        }
    }

    @Test
    fun spawnColumnIsThree() {
        TetrominoType.values().forEach { type ->
            val t = Tetromino.spawn(type)
            assertEquals(3, t.boardCol)
        }
    }

    @Test
    fun rotationCyclesBackToOriginal() {
        TetrominoType.values().forEach { type ->
            val original = Tetromino.spawn(type)
            val rotations = Tetromino.SHAPES.getValue(type).size
            var t = original
            repeat(rotations) { t = t.rotatedClockwise() }
            // After full cycle, blocks should match original
            assertEquals("$type full rotation should restore shape",
                original.getBlocks(), t.getBlocks())
        }
    }

    @Test
    fun oHasSingleRotation() {
        val o = Tetromino.spawn(TetrominoType.O)
        val r1 = o.rotatedClockwise()
        assertEquals(o.getBlocks(), r1.getBlocks())
    }

    @Test
    fun iHasFourRotations() {
        val shapes = Tetromino.SHAPES.getValue(TetrominoType.I)
        assertEquals(4, shapes.size)
    }

    @Test
    fun rotationDoesNotMutateOriginal() {
        val original = Tetromino.spawn(TetrominoType.T)
        val beforeBlocks = original.getBlocks()
        original.rotatedClockwise()
        assertEquals(beforeBlocks, original.getBlocks())
    }

    @Test
    fun movedByShiftsBoardPosition() {
        val t = Tetromino.spawn(TetrominoType.T)
        val moved = t.movedBy(dRow = 2, dCol = -1)
        assertEquals(t.boardRow + 2, moved.boardRow)
        assertEquals(t.boardCol - 1, moved.boardCol)
        assertEquals(t.getBlocks(), moved.getBlocks())
    }

    @Test
    fun getBoardBlocksReflectsOffset() {
        val t = Tetromino.spawn(TetrominoType.T)
        val local = t.getBlocks()
        val board = t.getBoardBlocks()
        assertEquals(local.size, board.size)
        local.zip(board).forEach { (l, b) ->
            assertEquals(l.row + t.boardRow, b.row)
            assertEquals(l.col + t.boardCol, b.col)
        }
    }

    @Test
    fun allPiecesHaveUniqueSpawnPositions() {
        // Each piece's spawn blocks must all fit within a 4×4 bounding box
        TetrominoType.values().forEach { type ->
            val blocks = Tetromino.SHAPES.getValue(type)[0]
            blocks.forEach { b ->
                assertTrue("$type row ${b.row} out of range", b.row in 0..3)
                assertTrue("$type col ${b.col} out of range", b.col in 0..3)
            }
        }
    }
}
