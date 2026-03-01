# AR Tetris for Android 9

A simple Tetris game designed for AR glasses with grayscale display and touch sensor input.

## Requirements
- Android Studio (Arctic Fox or later)
- Android SDK 28 (Android 9)
- Kotlin 1.9.0
- Gradle 8.0+

## Project Specifications
- **Target Device**: AR Glasses (Android 9)
- **SoC**: Unisoc (32-bit ARM)
- **Architecture**: armeabi-v7a (32-bit)
- **Display**: 640x480 Grayscale (green tinted)
- **Frame Rate**: 30 FPS
- **Input Methods**:
  - DPAD Left/Right: Move tetromino / navigate difficulty (in menu)
  - Single Tap (KEYCODE_ENTER): Rotate tetromino / Start game
  - Double Tap (KEYCODE_BACK): Rotate / Start (during play); Exit app (in menu)
  - Volume Down (KEYCODE_VOLUME_DOWN): Return to menu (when playing)
  - Note: Hardware long press is **not supported** — the touch sensor always emits DOWN+UP ~48ms apart regardless of hold duration. Use Volume Down instead.

## Setup Instructions

### Option 1: Open in Android Studio (Recommended)
1. Open Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to this directory and select it
4. Android Studio will automatically download the Gradle wrapper and dependencies
5. Wait for Gradle sync to complete
6. Run the app on an emulator or device

### Option 2: Command Line Build
If you have Gradle installed:
```bash
# Generate Gradle wrapper (first time only)
gradle wrapper --gradle-version 8.0

# Build the project
./gradlew build

# Install on connected device
./gradlew installDebug
```

## Project Structure
```
app/
├── src/main/
│   ├── java/com/arglass/tetris/
│   │   ├── MainActivity.kt              # Entry point
│   │   ├── views/
│   │   │   └── TetrisView.kt           # Custom view for rendering
│   │   ├── game/
│   │   │   ├── GameEngine.kt           # Core game logic
│   │   │   ├── Board.kt                # Game board state
│   │   │   ├── Tetromino.kt            # Block pieces
│   │   │   └── ScoreManager.kt         # Scoring logic
│   │   └── input/
│   │       └── GestureHandler.kt       # (inactive — all input handled via KeyEvents in MainActivity)
│   ├── res/
│   │   ├── layout/activity_main.xml
│   │   ├── values/strings.xml
│   │   └── values/colors.xml
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Development Status
**Current Phase**: 6 of 7 Complete ✅

- [x] **Phase 1**: Project setup
- [x] **Phase 2**: Core game logic
- [x] **Phase 3**: Rendering system
- [x] **Phase 4**: Input handling (KeyEvent-based for AR glasses hardware)
- [x] **Phase 5**: Game loop integration
- [x] **Phase 6**: Polish & optimization (difficulty levels, game states)
- [ ] **Phase 7**: Testing & deployment

See `DEVELOPMENT_PLAN.md` for detailed roadmap and `PROGRESS.md` for current status.

## Controls

All input is delivered as `KeyEvent`s by the AR glasses hardware — there is no touch/swipe handling.

| Key | Action |
|-----|--------|
| DPAD Left | Move piece left / Previous difficulty (menu) |
| DPAD Right | Move piece right / Next difficulty (menu) |
| KEYCODE_ENTER (single tap) | Rotate piece / Start game / Restart |
| KEYCODE_VOLUME_DOWN | Return to menu (during play) |
| KEYCODE_BACK (double tap) | Rotate / Start (during play); Exit app (in menu) |

> **Hardware note**: Long press via KEYCODE_ENTER is not supported. The touch sensor
> always emits DOWN+UP ~48ms apart regardless of how long you physically hold.
> Volume Down is used as the long press substitute.

## Game Features
- Standard Tetris gameplay (10x20 board)
- Three difficulty levels: Easy (1s drop), Medium (0.5s drop), Hard (0.33s drop)
- Score tracking
- Next piece preview
- Grayscale graphics optimized for AR display
- No sound (AR glasses limitation)

## Building APK
```bash
./gradlew assembleDebug
# APK will be in: app/build/outputs/apk/debug/
```

## Notes
- The game is locked to landscape orientation (640x480)
- Designed for 30 FPS performance
- Uses grayscale rendering for monochrome AR display
- No external dependencies beyond AndroidX

## License
MIT License - Free to use and modify
