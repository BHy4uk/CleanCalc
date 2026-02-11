# CleanCalc PRD (Native Android)

## Problem Statement
Deliver a production-ready, fully native Android calculator (Kotlin + Jetpack Compose) that is Play Store compliant, offline, and stable for internal testing.

## Architecture
- **Native Android**: Kotlin + Jetpack Compose
- **State Management**: ViewModel + SavedStateHandle
- **No Backend**: Offline-only, no permissions

## Implemented Features
- Basic arithmetic: addition, subtraction, multiplication, division
- Decimal input with validation
- Operator precedence
- Clear (C) and delete (⌫)
- Separate input and result display
- Division by zero error handling
- Light theme with optional dark mode
- Zero-permission AndroidManifest
- Play Store config: applicationId `com.neche.cleanCalc`, versionCode 1, versionName 1.0.0

## Backlog
### P0
- None

### P1
- Add optional haptic feedback toggle
- Add copy-to-clipboard for result

### P2
- Add history tape
- Add scientific mode toggle
