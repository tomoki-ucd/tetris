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
  - Swipe Left/Right: Move tetromino
  - Single Tap: Rotate tetromino / Start game
  - Long Press: Quit to menu
  - Double Tap: (Reserved)

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
│   │       └── GestureHandler.kt       # Touch input processing
│   ├── res/
│   │   ├── layout/activity_main.xml
│   │   ├── values/strings.xml
│   │   └── values/colors.xml
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Development Status
- [x] Project structure created
- [x] Basic Android configuration
- [ ] Core game logic (Phase 2)
- [ ] Rendering system (Phase 3)
- [ ] Input handling (Phase 4)
- [ ] Game loop integration (Phase 5)
- [ ] Polish & optimization (Phase 6)
- [ ] Testing (Phase 7)

See `DEVELOPMENT_PLAN.md` for detailed development roadmap.

## Controls
| Input | Action |
|-------|--------|
| Swipe Left | Move piece left |
| Swipe Right | Move piece right |
| Single Tap | Rotate piece clockwise |
| Long Press | Quit to menu |

## Game Features
- Standard Tetris gameplay (10x20 board)
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
