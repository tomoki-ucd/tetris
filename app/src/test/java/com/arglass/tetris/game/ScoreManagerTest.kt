package com.arglass.tetris.game

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ScoreManagerTest {

    private lateinit var sm: ScoreManager

    @Before
    fun setUp() {
        sm = ScoreManager()
    }

    @Test
    fun initialScoreIsZero() {
        assertEquals(0, sm.score)
    }

    @Test
    fun initialLinesIsZero() {
        assertEquals(0, sm.totalLinesCleared)
    }

    @Test
    fun oneLine100Points() {
        sm.addLinesCleared(1)
        assertEquals(100, sm.score)
        assertEquals(1, sm.totalLinesCleared)
    }

    @Test
    fun twoLines300Points() {
        sm.addLinesCleared(2)
        assertEquals(300, sm.score)
        assertEquals(2, sm.totalLinesCleared)
    }

    @Test
    fun threeLines500Points() {
        sm.addLinesCleared(3)
        assertEquals(500, sm.score)
        assertEquals(3, sm.totalLinesCleared)
    }

    @Test
    fun tetris800Points() {
        sm.addLinesCleared(4)
        assertEquals(800, sm.score)
        assertEquals(4, sm.totalLinesCleared)
    }

    @Test
    fun zeroLinesNoPoints() {
        sm.addLinesCleared(0)
        assertEquals(0, sm.score)
        assertEquals(0, sm.totalLinesCleared)
    }

    @Test
    fun softDropAddsOnePointPerRow() {
        sm.addSoftDrop(5)
        assertEquals(5, sm.score)
        assertEquals(0, sm.totalLinesCleared)
    }

    @Test
    fun scoresAccumulate() {
        sm.addLinesCleared(1)  // 100
        sm.addLinesCleared(4)  // 800
        sm.addSoftDrop(3)      // 3
        assertEquals(903, sm.score)
        assertEquals(5, sm.totalLinesCleared)
    }

    @Test
    fun resetClearsScore() {
        sm.addLinesCleared(4)
        sm.addSoftDrop(10)
        sm.reset()
        assertEquals(0, sm.score)
        assertEquals(0, sm.totalLinesCleared)
    }
}
