# Touch Sensor Input Details

## Hardware

The touchpad chip is `iqs7211e`, operating as a **pure key device** (`event2: iqs7211e_keys`).
It never generates raw `MotionEvent` (coordinates) — only `KeyEvent`.

Confirmed via:
```bash
adb shell getevent -l
# add device 2: /dev/input/event2
#   name: "iqs7211e_keys"
```

The volume down/up buttons and the right arm button are separate physical keys routed through `gpio-keys`:
```bash
# add device 4: /dev/input/event1
#   name: "gpio-keys"
```

## Input Mapping

| Physical gesture / button | Source | Kernel event | Linux keycode | Android keycode |
|---|---|---|---|---|
| 1-finger swipe forward | touchpad (`iqs7211e_keys`) | `KEY_RIGHT` | 106 | `KEYCODE_DPAD_RIGHT` (22) |
| 1-finger swipe backward | touchpad (`iqs7211e_keys`) | `KEY_LEFT` | 105 | `KEYCODE_DPAD_LEFT` (21) |
| Swipe up | touchpad | nothing | — | — (disabled at firmware level) |
| Swipe down | touchpad | nothing | — | — (disabled at firmware level) |
| Single tap | touchpad (`iqs7211e_keys`) | `KEY_ENTER` | 28 | `KEYCODE_ENTER` (66) |
| Double tap | touchpad (`iqs7211e_keys`) | `KEY_BACK` | 158 | `KEYCODE_BACK` (4) |
| Triple tap | touchpad (`iqs7211e_keys`) | — | — | `KEYCODE_HENKAN` (214) |
| Long press | touchpad (`iqs7211e_keys`) | — | — | `KEYCODE_MENU` (82) — firmware-detected; see note below |
| 2-finger swipe forward | touchpad (`iqs7211e_keys`) | `KEY_VOLUMEDOWN` | — | `KEYCODE_VOLUME_DOWN` (25) |
| 2-finger swipe backward | touchpad (`iqs7211e_keys`) | `KEY_VOLUMEUP` | — | `KEYCODE_VOLUME_UP` (24) |
| Right arm button | `gpio-keys` | `KEY_VOLUMEDOWN` | — | KeyCode 124 |

Key layout mapping source: `/system/usr/keylayout/Generic.kl`
(No device-specific `.kl` file exists for `iqs7211e_keys`)

## Key Facts

- **Vertical swipes are disabled at firmware level** — no `KEY_UP` or `KEY_DOWN` is ever emitted
- **Double tap is detected at firmware level** — the driver emits `KEY_BACK` directly, not two `KEY_ENTER` events
- **`RecyclerView` scrolling works via built-in D-pad navigation** — it responds natively to `KEYCODE_DPAD_LEFT`/`KEYCODE_DPAD_RIGHT` without any custom `onKeyDown` override
- **`PagerSnapHelper` in the current launcher code is effectively unused** — scrolling is driven by D-pad key events, not fling/touch gestures

## Distinguishing KEY_ENTER Press and Release

A single tap emits both a DOWN and UP event at the kernel level:

```
/dev/input/event2: EV_KEY       KEY_ENTER            DOWN
/dev/input/event2: EV_SYN       SYN_REPORT           00000000
/dev/input/event2: EV_KEY       KEY_ENTER            UP
```

These map to Android's `onKeyDown` and `onKeyUp` respectively. You can distinguish them using these callbacks in your `Activity` or `View`:

```kotlin
override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    if (keyCode == KeyEvent.KEYCODE_ENTER) {
        // Finger touched (tap began)
        return true
    }
    return super.onKeyDown(keyCode, event)
}

override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    if (keyCode == KeyEvent.KEYCODE_ENTER) {
        // Finger lifted (tap ended) — typically the right place to trigger actions
        return true
    }
    return super.onKeyUp(keyCode, event)
}
```

- `onKeyDown` fires when the DOWN event arrives (touch begins)
- `onKeyUp` fires when the UP event arrives (touch ends)
- For a simple tap action, triggering on `onKeyUp` is conventional (consistent with button click behavior)

## Long Press

**Long press cannot be detected via `KEYCODE_ENTER` hold duration.**

Measured via logcat: the touch sensor always emits DOWN+UP approximately **48ms apart**,
regardless of how long the user physically holds the sensor. The UP event fires immediately
after DOWN at the firmware level — there is no way to distinguish a short tap from a long hold
using `onKeyDown`/`onKeyUp` timing.

```
# Every "hold" looks identical to a tap in the logs:
D Input: ENTER DOWN at 1772377969724
D Input: ENTER UP held=48ms   ← always ~48ms, even after holding for seconds
```

**However**, the touchpad firmware detects long press independently and emits `KEYCODE_MENU` (82)
directly — confirmed by the device SDK documentation (长按事件: KEYCODE_MENU = 82).
This is a firmware-level gesture, not derived from KEY_ENTER hold duration.

**Caveat**: The OS-level documentation lists long press as 未登録 (unregistered) and states it
launches the system app launcher. It is unclear whether `KEYCODE_MENU` reaches foreground apps
reliably or is intercepted by the OS. **Test on device to confirm.**
