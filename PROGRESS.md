# AR Tetris - Development Progress

## ✅ Phase 1: Project Setup - COMPLETED

### Summary
Android project successfully created, configured, and tested. App launches without errors on Android 9 emulator.

### Completed Tasks
- [x] Initialize Git repository with proper .gitignore
- [x] Create Android project structure for Android 9 (API 28)
- [x] Configure Kotlin with Gradle build system
- [x] Set up build configuration (Gradle 8.7, Android Gradle Plugin 8.1.4, Kotlin 1.9.22)
- [x] Configure for AR glasses specifications (640x480, landscape orientation)
- [x] Set up grayscale color palette
- [x] Configure multi-architecture support (32-bit and 64-bit)
- [x] Create MainActivity with basic layout
- [x] Test successful build and run on emulator

---

## ✅ Phase 2: Core Game Logic - COMPLETED

### Completed Tasks
- [x] Create Tetromino class (7 piece types: I, O, T, S, Z, J, L)
- [x] Implement Board class (10x20 grid)
- [x] Implement collision detection
- [x] Implement line clearing logic
- [x] Implement rotation logic (wall kicks)
- [x] Create ScoreManager class
- [x] Write unit tests for game logic

### Files Created
- `game/Tetromino.kt` — 7 piece types, 4 rotation states each, spawn/move/rotate helpers
- `game/Board.kt` — 10×20 grid, placement, collision, line clearing, game-over detection
- `game/ScoreManager.kt` — line-based scoring (100/300/500/800) + soft drop

### Unit Tests (all passing)
- `TetrominoTest` — 9 tests: spawn, rotation cycle, wall-kick, block coordinates
- `BoardTest` — 12 tests: placement, collision, line clearing, drop-down, game over
- `ScoreManagerTest` — 9 tests: scoring table, accumulation, reset

### Deliverables
- Complete headless game logic with no UI dependencies
- All 30 unit tests passing

---

## ✅ Phase 3: Rendering System - COMPLETED

### Completed Tasks
- [x] Create TetrisView custom view extending View
- [x] Implement grayscale rendering using Canvas
- [x] Draw grid and blocks with patterns (4 hatch styles to differentiate pieces)
- [x] Implement next piece preview box
- [x] Render score and lines display

### Files Created
- `views/TetrisView.kt` — full Canvas-based renderer; board, side panel, menu, game-over overlay

---

## ✅ Phase 4: Input Handling - COMPLETED

### Completed Tasks
- [x] Implement GestureDetector for touch events
- [x] Map gestures to game actions (swipe, tap, long press)
- [x] Input debouncing handled by Android GestureDetector

### Files Created
- `input/GestureHandler.kt` — wraps GestureDetector; swipe L/R, tap, long press

### Input Mapping
| Gesture | Action |
|---------|--------|
| Swipe Left | Move piece left |
| Swipe Right | Move piece right |
| Single Tap | Rotate piece / Start game / Restart |
| Long Press | Quit to menu |

---

## ✅ Phase 5: Game Loop & Integration - COMPLETED

### Completed Tasks
- [x] Implement game loop with fixed timestep (30 FPS via Handler)
- [x] Connect input → GameEngine → Board
- [x] Connect GameEngine → TetrisView (onBoardChanged callback)
- [x] Implement game states: MENU, PLAYING, GAME_OVER
- [x] Implement piece drop timing (1 row/second)
- [x] Integrate score tracking

### Files Created / Updated
- `game/GameEngine.kt` — orchestrates all game logic, state machine, gravity, wall kicks
- `MainActivity.kt` — 30 FPS Handler loop, gesture forwarding, lifecycle pause/resume
- `activity_main.xml` — hosts TetrisView

---

## ✅ Phase 6: Polish & Optimization - COMPLETED

### Completed Tasks
- [x] Game over overlay with score display
- [x] Tap-to-restart from game over screen
- [x] Menu screen with tap-to-start
- [x] Grayscale aesthetic with hatch patterns for piece differentiation
- [x] Side panel: next piece preview, score, lines cleared

---

## ✅ Input Algorithm Fix: AR Glasses Hardware Compatibility - COMPLETED

### Background
The original input implementation was written assuming the AR glasses' touch sensor would deliver all gestures as `MotionEvent`s through the standard Android touch pipeline. Based on this assumption, all input was handled exclusively in `GestureHandler.kt` using `GestureDetector`.

### What the hardware actually does

| Gesture | Event emitted by hardware |
|---------|--------------------------|
| Swipe left / right | `MotionEvent` (standard touch) |
| Single tap | `KeyEvent(KEYCODE_ENTER)` |
| Double tap | `KeyEvent(KEYCODE_BACK)` |

### Problems with the original algorithm

**Single tap was silently broken.**
`GestureDetector.onSingleTapConfirmed()` only fires in response to `MotionEvent`s. The hardware emits `KEYCODE_ENTER` for a tap, which is a `KeyEvent` — a completely separate Android input channel. The `onTouchEvent` path never received it, so rotate / start / restart never triggered on the physical device.

**Double tap / back had mismatched behaviour.**
The original design mapped long press → "quit to menu" (`engine.onLongPress()`). On the hardware, however, long press is not a supported gesture. Double tap sends `KEYCODE_BACK`, which by default is handled by `Activity.onBackPressed()` and exits the app entirely — bypassing the in-game menu state and offering no way to return to the menu mid-game.

### Changes made (`MainActivity.kt`)

**Added `onKeyDown(KEYCODE_ENTER)` → `engine.onTap()`**
Overriding `onKeyDown` intercepts the `KeyEvent` channel, which is where the hardware delivers single-tap. Calling `engine.onTap()` restores the intended behaviour: rotate the active piece during play, or start / restart the game from the menu and game-over screens.

**Added `onBackPressed()` with state-aware routing**
When `KEYCODE_BACK` arrives (double tap on hardware):
- If the game is in `PLAYING` state → call `engine.onLongPress()`, which returns to the menu. The app stays alive and the player can start a new game.
- Otherwise (MENU or GAME_OVER) → call `super.onBackPressed()`, which exits the app as expected.

This preserves the original intent of "quit to menu" while correctly mapping it to the only available hardware gesture that can serve that purpose.

**Swipe handling was left unchanged.**
The hardware sends swipe as `MotionEvent`, which is exactly what `GestureHandler`'s `onFling` expects. No changes were needed there.

### Files changed
- `MainActivity.kt` — added `onKeyDown` and `onBackPressed` overrides

---

## ✅ Input Refinements: Double Tap & Long Press - COMPLETED

### Background
Two input issues were identified during testing:

1. **Double tap (KEYCODE_BACK) was exiting the app regardless of game state.** The `onBackPressed()` override was being bypassed by `AppCompatActivity`'s back press dispatcher, so the app always exited.
2. **Long press was unsupported.** The hardware cannot emit a long press gesture natively.

### Changes made

**Double tap remapped to single tap**
`KEYCODE_BACK` is now handled in `onKeyDown` and calls `engine.onTap()`, identical to `KEYCODE_ENTER`. Accidental double taps during rapid rotation no longer exit the app.

**Long press emulated via hold duration**
`KEYCODE_ENTER` now fires on key-up instead of key-down. If held ≥ 3 seconds, it calls `engine.onLongPress()` (return to menu); shorter presses call `engine.onTap()` as before.

### Input mapping (updated)

| Gesture | Key | Action |
|---------|-----|--------|
| Swipe left | `KEYCODE_DPAD_LEFT` | Move piece left |
| Swipe right | `KEYCODE_DPAD_RIGHT` | Move piece right |
| Single tap | `KEYCODE_ENTER` (< 3s) | Rotate / Start / Restart |
| Long press | `KEYCODE_ENTER` (≥ 3s) | Return to menu |
| Double tap | `KEYCODE_BACK` | Same as single tap |

### Files changed
- `MainActivity.kt` — `KEYCODE_BACK` → `onTap()`; `KEYCODE_ENTER` moved to `onKeyUp` with hold-duration check; removed `onBackPressed` override

---

## ✅ Difficulty Levels - COMPLETED

### Completed Tasks
- [x] Add `Level` enum to `GameEngine` (EASY 1000ms, MEDIUM 500ms, HARD 333ms drop interval)
- [x] Cycle selected level with swipe left/right in MENU state
- [x] Menu screen shows `< EASY  MEDIUM  HARD >` with active level in white, inactive dimmed
- [x] Add `DPAD_LEFT` / `DPAD_RIGHT` key handlers in `MainActivity` for hardware button support

### Files Changed
- `game/GameEngine.kt` — `Level` enum, level-aware drop timing, menu swipe handling
- `views/TetrisView.kt` — difficulty selector UI in `drawMenu`
- `MainActivity.kt` — `DPAD_LEFT` / `DPAD_RIGHT` key handlers

---

## 📋 Phase 7: Testing & Deployment - PENDING

### Tasks to Complete
- [ ] Comprehensive testing on Android 9 emulator
- [ ] Test on actual AR glasses (if available)
- [ ] Fix device-specific issues
- [ ] Create production APK (32-bit only)
- [ ] Final documentation

---

## Current Status
**Phase**: 6 of 7 complete (+ input fix + difficulty levels)
**Status**: ✅ Fully playable with Easy / Medium / Hard difficulty — ready for device testing
**Next Step**: Phase 7 — build APK and test on emulator / AR glasses

## Project Structure
```
app/src/main/java/com/arglass/tetris/
├── MainActivity.kt              # Entry point + 30 FPS game loop
├── game/
│   ├── Tetromino.kt             # 7 piece types, rotation, movement
│   ├── Board.kt                 # 10x20 grid, collision, line clearing
│   ├── ScoreManager.kt          # Scoring logic
│   └── GameEngine.kt            # State machine, gravity, input dispatch
├── views/
│   └── TetrisView.kt            # Canvas renderer (grayscale)
└── input/
    └── GestureHandler.kt        # Touch gesture → game actions

app/src/test/java/com/arglass/tetris/game/
├── TetrominoTest.kt             # 9 tests
├── BoardTest.kt                 # 12 tests
└── ScoreManagerTest.kt          # 9 tests
```

## Development Environment
- **IDE**: Android Studio
- **Gradle**: 8.7
- **Android Gradle Plugin**: 8.1.4
- **Kotlin**: 1.9.22
- **Java**: 21.0.6
- **Target SDK**: Android 9 (API 28)

## Last Updated
2026-03-01 - Double tap remapped to single tap; long press (3s hold) added to return to menu
