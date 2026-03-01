package com.arglass.tetris

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
// import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.arglass.tetris.game.GameEngine
// import com.arglass.tetris.input.GestureHandler
import com.arglass.tetris.views.TetrisView

/**
 * Main Activity for AR Tetris.
 * Target: Android 9 (API 28) AR Glasses — 640×480 grayscale display.
 *
 * Game loop: 30 FPS using a Handler-based repeating tick.
 * Input:     KeyEvents (DPAD_LEFT/RIGHT, KEYCODE_ENTER, KEYCODE_BACK) forwarded to GameEngine.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tetrisView: TetrisView
    private lateinit var engine: GameEngine
    // private lateinit var gestureHandler: GestureHandler

    private val handler = Handler(Looper.getMainLooper())
    private val frameIntervalMs = 33L  // ~30 FPS

    private var lastTickMs = 0L

    private val gameLoopRunnable = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()
            val delta = if (lastTickMs == 0L) frameIntervalMs else (now - lastTickMs)
            lastTickMs = now

            engine.update(delta)
            handler.postDelayed(this, frameIntervalMs)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        engine = GameEngine()
        // gestureHandler = GestureHandler(this, engine)

        tetrisView = findViewById(R.id.tetrisView)
        tetrisView.engine = engine
    }

    override fun onResume() {
        super.onResume()
        lastTickMs = 0L
        handler.post(gameLoopRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(gameLoopRunnable)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT  -> { engine.onSwipeLeft();  true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { engine.onSwipeRight(); true }
            KeyEvent.KEYCODE_ENTER      -> { engine.onTap();        true }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (engine.state == GameEngine.State.PLAYING) {
            engine.onLongPress()
        } else {
            super.onBackPressed()
        }
    }

    // override fun onTouchEvent(event: MotionEvent): Boolean {
    //     return gestureHandler.onTouchEvent(event) || super.onTouchEvent(event)
    // }
}
