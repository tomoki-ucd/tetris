package com.arglass.tetris.input

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import com.arglass.tetris.game.GameEngine
import kotlin.math.abs

/**
 * Processes touch gestures and forwards them to the GameEngine.
 *
 * Supported gestures:
 *   Swipe Left  → move piece left
 *   Swipe Right → move piece right
 *   Single Tap  → rotate / start game
 *   Long Press  → quit to menu
 *
 * Usage: pass onTouchEvent calls to [onTouchEvent].
 */
class GestureHandler(context: Context, private val engine: GameEngine) {

    companion object {
        private const val SWIPE_MIN_DISTANCE_PX = 50f
        private const val SWIPE_MIN_VELOCITY_PX = 50f
    }

    private val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            engine.onTap()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            engine.onLongPress()
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val start = e1 ?: return false
            val dx = e2.x - start.x
            val dy = e2.y - start.y

            // Only recognise horizontal swipes (horizontal dominates vertical).
            if (abs(dx) > abs(dy) && abs(dx) > SWIPE_MIN_DISTANCE_PX && abs(velocityX) > SWIPE_MIN_VELOCITY_PX) {
                if (dx < 0) engine.onSwipeLeft() else engine.onSwipeRight()
                return true
            }
            return false
        }
    })

    init {
        detector.setIsLongpressEnabled(true)
    }

    fun onTouchEvent(event: MotionEvent): Boolean = detector.onTouchEvent(event)
}
