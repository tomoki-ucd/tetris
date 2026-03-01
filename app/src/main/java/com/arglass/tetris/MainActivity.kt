package com.arglass.tetris

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
 * Input:     KeyEvents (DPAD_LEFT/RIGHT, KEYCODE_ENTER, KEYCODE_BACK, KEYCODE_VOLUME_DOWN)
 *            forwarded to GameEngine.
 * Long press: KEYCODE_VOLUME_DOWN → return to menu.
 * Double tap: KEYCODE_BACK in MENU state → exit app.
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
            KeyEvent.KEYCODE_DPAD_LEFT  -> { engine.onSwipeLeft(); true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { engine.onSwipeRight(); true }
            KeyEvent.KEYCODE_ENTER      -> {
                if (event.repeatCount == 0) { Log.d("Input", "ENTER -> onTap"); engine.onTap() }
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> { Log.d("Input", "VOLUME_DOWN -> onLongPress"); engine.onLongPress(); true }
            KeyEvent.KEYCODE_BACK       -> {
                if (engine.state == GameEngine.State.MENU) { Log.d("Input", "BACK in MENU -> finish"); finish() }
                else { Log.d("Input", "BACK -> onTap"); engine.onTap() }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
//            KeyEvent.KEYCODE_ENTER -> {
//                val held = System.currentTimeMillis() - enterKeyDownTimeMs
//                if (held >= longPressThresholdMs) engine.onLongPress() else engine.onTap()
//                true
//            }
            else -> super.onKeyUp(keyCode, event)
        }
    }

    // override fun onTouchEvent(event: MotionEvent): Boolean {
    //     return gestureHandler.onTouchEvent(event) || super.onTouchEvent(event)
    // }
}
