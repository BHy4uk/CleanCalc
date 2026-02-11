# CleanCalc PRD

## Problem Statement
Build a production-ready, Play Store-compliant Android calculator app with a modern UI, robust arithmetic (including decimals and operator precedence), error handling, and state persistence.

## Architecture
- **Frontend**: Expo (React Native, TypeScript), Expo Router (single screen)
- **State Persistence**: AsyncStorage
- **Backend**: FastAPI + MongoDB scaffold (not used by calculator)

## Implemented Features
- Basic arithmetic: addition, subtraction, multiplication, division
- Decimal input with validation
- Operator precedence (e.g., 5 + 5 × 2 = 20)
- Clear (C) and delete last digit (⌫)
- Separate input and result display
- Division-by-zero handling with friendly error
- State persistence across reload/minimize
- Light theme with optional dark mode
- Play Store readiness (app id, versioning, no permissions, release guidance)

## Backlog
### P0
- None

### P1
- Add optional haptic feedback toggle
- Add copy-to-clipboard for results

### P2
- Add history tape (optional)
- Add scientific mode toggle