package com.arglass.tetris

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Main Activity for AR Tetris Game
 * Target: Android 9 (API 28) AR Glasses
 * Display: 640x480 Grayscale
 * Input: Swipe left/right, single tap, double tap, long press
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: Initialize TetrisView and add to layout
        // TODO: Set up game engine
        // TODO: Configure gesture handling
    }

    override fun onPause() {
        super.onPause()
        // TODO: Pause game when activity loses focus
    }

    override fun onResume() {
        super.onResume()
        // TODO: Resume game when activity gains focus
    }
}
