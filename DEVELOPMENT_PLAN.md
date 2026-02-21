# Tetris for AR Glasses - Development Policy and Procedure

## Project Overview
A simple Tetris game for Android 9 AR glasses with monochrome green display and touch sensor input.

## Technical Constraints
- **Platform**: Android 9 (API Level 28)
- **Language**: Kotlin
- **Display**: Grayscale (green tinted), 640x480 resolution (horizontal x vertical)
- **Input**: Touch sensor (swipe left/right only, single tap, double tap, long press)
- **No Sound**: Audio not available
- **Target Device**: AR Glasses

## Game Features
### Included
- Core Tetris gameplay (falling blocks, line clearing)
- Scoring system
- Next piece preview
- Simple touch controls

### Excluded
- No levels/difficulty progression
- No complex animations (display limitation)
- No color-based gameplay
- No sound (AR glasses may not support it)

## Development Architecture

### 1. Project Structure
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
│   │   ├── layout/
│   │   │   └── activity_main.xml
│   │   └── values/
│   │       ├── strings.xml
│   │       └── colors.xml              # Green color definitions
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

### 2. Design Patterns
- **MVC Pattern**: Separate game logic from rendering
- **Game Loop**: Fixed time step for consistent gameplay
- **State Pattern**: Game states (MENU, PLAYING, PAUSED, GAME_OVER)

### 3. Grayscale Display Strategy
Display supports grayscale with green tint:
- Use **grayscale values** (0-255) for different elements
- Draw using **patterns/textures** (solid, hatched, dotted) to differentiate pieces
- Use **borders and outlines** for clarity
- **High contrast** between active piece, placed blocks, and background
- **Clean retro aesthetic** suitable for AR glasses

#### Visual Representation Plan
- Background: Black (grayscale 0)
- Grid lines: Dark gray (grayscale 64)
- Placed blocks: Light gray (grayscale 192) with pattern fill
- Active piece: White (grayscale 255) solid
- Next piece preview: Medium gray (grayscale 160)
- Text/Score: White (grayscale 255)
- Different tetromino types: Same brightness but different fill patterns

### 4. Input Mapping
**Available Gestures**: Swipe left/right, single tap, double tap, long press
**NOT Available**: Swipe up/down

| Gesture | Action | Game State |
|---------|--------|------------|
| Single Tap | Start Game | MENU, GAME_OVER |
| Single Tap | Rotate Clockwise | PLAYING |
| Swipe Left | Move Left | PLAYING |
| Swipe Right | Move Right | PLAYING |
| Long Press | Quit to Menu | PLAYING, PAUSED |
| Double Tap | (Reserved/Unused) | - |

## Development Procedure

### Phase 1: Project Setup
1. Create Android project with Kotlin support
2. Configure `build.gradle` for Android 9 (API 28)
3. Set up project structure and packages
4. Create basic MainActivity and layout

**Deliverables**:
- Working Android project that builds
- Empty MainActivity that launches

### Phase 2: Core Game Logic (No UI)
1. Implement Tetromino class (7 piece types: I, O, T, S, Z, J, L)
2. Implement Board class (10x20 grid standard)
3. Implement collision detection
4. Implement line clearing logic
5. Implement rotation logic (SRS - Super Rotation System or simpler)
6. Create unit tests for game logic

**Deliverables**:
- Complete game logic that can run headless
- Unit tests passing

### Phase 3: Rendering System
1. Create TetrisView custom view extending View
2. Implement monochrome rendering using Canvas
3. Draw grid, blocks with patterns
4. Implement next piece preview box
5. Render score display
6. Test rendering on emulator

**Deliverables**:
- Visual representation of game board
- Monochrome green aesthetic
- Next piece preview visible

### Phase 4: Input Handling
1. Implement GestureDetector for touch events
2. Map gestures to game actions
3. Add input debouncing/throttling
4. Handle edge cases (multiple quick taps, etc.)
5. Test gesture recognition

**Deliverables**:
- Responsive touch controls
- Reliable gesture detection

### Phase 5: Game Loop & Integration
1. Implement game loop with fixed timestep
2. Connect input to game logic
3. Connect game logic to rendering
4. Implement game states (MENU, PLAYING, GAME_OVER)
5. Add scoring system
6. Implement piece drop timing

**Deliverables**:
- Fully playable game
- Score tracking

### Phase 6: Polish & Optimization
1. Optimize rendering for AR glasses
2. Add simple game over screen
3. Add restart functionality
4. Fine-tune drop speed and timing
5. Performance testing on Android 9

**Deliverables**:
- Polished, performant game
- Smooth 60 FPS on target device

### Phase 7: Testing & Deployment
1. Test on Android 9 emulator
2. If possible, test on actual AR glasses
3. Fix any device-specific issues
4. Create APK for deployment
5. Documentation

**Deliverables**:
- Tested APK
- User manual (if needed)

## Technical Decisions

### Display Resolution
- **Fixed Resolution**: 640x480 (width x height)
- Grid will be positioned to fit within this resolution
- Block size: ~20px (allows 10x20 grid = 200x400, leaving room for UI)

### Game Parameters
- **Board Size**: 10 columns × 20 rows (standard)
- **Initial Drop Speed**: 1 row per second
- **Speed Increase**: None (no levels)
- **Scoring**:
  - 1 line: 100 points
  - 2 lines: 300 points
  - 3 lines: 500 points
  - 4 lines (Tetris): 800 points
  - Soft drop: 1 point per row
- **Next Piece**: Show 1 piece ahead

### Performance Targets
- **Frame Rate**: 30 FPS (sufficient for this game)
- **Input Latency**: <50ms
- **Memory**: <50MB RAM usage

## Development Tools
- **IDE**: Android Studio (latest compatible with Android 9)
- **Build System**: Gradle with Kotlin DSL
- **Testing**: JUnit for unit tests
- **Version Control**: Git

## Risk Mitigation

### Risk 1: AR Glasses Display Limitations
- **Mitigation**: Use emulator for development, make rendering highly configurable
- **Fallback**: Test on regular Android device with monochrome filter

### Risk 2: Touch Sensor Gesture Recognition
- **Mitigation**: Implement generous thresholds and debouncing
- **Fallback**: Make gesture sensitivity configurable

### Risk 3: Performance on AR Hardware
- **Mitigation**: Optimize early, profile frequently, minimize allocations
- **Fallback**: Reduce visual effects, simplify rendering

## Code Quality Standards
- **Kotlin Conventions**: Follow official Kotlin style guide
- **Documentation**: KDoc for public APIs
- **Testing**: Unit tests for game logic (minimum 70% coverage)
- **No Magic Numbers**: Use constants for all game parameters
- **Immutability**: Prefer `val` over `var`, use data classes

## Success Criteria
1. Game runs smoothly at 30 FPS on Android 9 emulator
2. All core Tetris mechanics work correctly
3. Controls are responsive and intuitive (swipe left/right, tap, long press)
4. Grayscale display is clear and readable at 640x480
5. Score system works accurately
6. Next piece preview functions correctly
7. Project opens correctly in Android Studio
8. No crashes or major bugs

## Timeline Estimate
- **Phase 1**: 2 hours
- **Phase 2**: 4 hours
- **Phase 3**: 4 hours
- **Phase 4**: 3 hours
- **Phase 5**: 4 hours
- **Phase 6**: 3 hours
- **Phase 7**: 2 hours
- **Total**: ~22 hours (approximately 3 development days)

## Next Steps
After approval of this plan:
1. Initialize Android project structure
2. Begin Phase 1 implementation
3. Iterate through phases sequentially
4. Request feedback at major milestones
