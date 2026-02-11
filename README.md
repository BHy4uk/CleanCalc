# CleanCalc

CleanCalc is a production-ready calculator built with Expo (React Native) that supports basic arithmetic, decimals, and reliable state persistence.

## Requirements
- **Android Studio**: latest stable version
- **Android SDK**: 34 or higher
- **Java**: 17+
- **Node.js**: 18+
- **Yarn**: 1.22+
- **Expo SDK**: 54 (already configured in `/frontend`)

## Project Structure
```
/app
  /backend          FastAPI (not required for calculator)
  /frontend         Expo app (CleanCalc)
  README.md
```

## Run on Emulator
1. Open Android Studio → **Device Manager** → create or start a virtual device.
2. In a terminal:
   ```bash
   cd /app/frontend
   yarn install
   npx expo start
   ```
3. Press **a** in the Expo CLI to launch the app on the emulator.

## Generate Native Android Project (Android Studio)
This creates the native `android/` folder so it can be opened in Android Studio.
```bash
cd /app/frontend
npx expo prebuild -p android
```
Then open `/app/frontend/android` in Android Studio and let Gradle sync.

## Build Debug APK
```bash
cd /app/frontend/android
./gradlew assembleDebug
```
APK output:
```
android/app/build/outputs/apk/debug/app-debug.apk
```

## Release Signing (Required for Play Store)
### 1) Generate Keystore
```bash
keytool -genkey -v -keystore cleancalc.keystore -alias cleancalc \
  -keyalg RSA -keysize 2048 -validity 10000
```
Move `cleancalc.keystore` to:
```
/app/frontend/android/app/cleancalc.keystore
```

### 2) Add signing credentials
Edit `/app/frontend/android/gradle.properties`:
```
RELEASE_STORE_FILE=cleancalc.keystore
RELEASE_KEY_ALIAS=cleancalc
RELEASE_STORE_PASSWORD=YOUR_STORE_PASSWORD
RELEASE_KEY_PASSWORD=YOUR_KEY_PASSWORD
```

### 3) Connect signing config
Edit `/app/frontend/android/app/build.gradle`:
```
android {
  signingConfigs {
    release {
      if (project.hasProperty('RELEASE_STORE_FILE')) {
        storeFile file(RELEASE_STORE_FILE)
        storePassword RELEASE_STORE_PASSWORD
        keyAlias RELEASE_KEY_ALIAS
        keyPassword RELEASE_KEY_PASSWORD
      }
    }
  }

  buildTypes {
    release {
      signingConfig signingConfigs.release
      minifyEnabled false
      shrinkResources false
    }
  }
}
```

## Build Release AAB (Google Play)
```bash
cd /app/frontend/android
./gradlew bundleRelease
```
AAB output:
```
android/app/build/outputs/bundle/release/app-release.aab
```

## Build Release APK (Optional)
```bash
cd /app/frontend/android
./gradlew assembleRelease
```

## Versioning
Update these in `/app/frontend/app.json` **before each release**:
- `expo.version` → **versionName** (e.g., 1.0.1)
- `android.versionCode` → increment by 1 every release

## App Icon
Replace these files with your real artwork (keep the same sizes/paths):
- `/app/frontend/assets/images/icon.png`
- `/app/frontend/assets/images/adaptive-icon.png`
- `/app/frontend/assets/images/splash-icon.png`

## Google Play Console Upload Guide
1. Create a Google Play Developer account.
2. In **Play Console**, click **Create app**:
   - App name: **CleanCalc**
   - Default language: your choice
   - App type: **App**
3. Complete the **App Content** section:
   - Data safety (calculator requires no data collection)
   - Ads: **No**
   - Target audience: your choice
4. Go to **Release → Testing → Internal testing**.
5. Create a new release and upload the AAB:
   - `app-release.aab` from the build step above
6. Add testers (email list or Google Groups).
7. Submit for internal testing and wait for processing.
8. Install on device using the internal testing opt-in link from Play Console.

## Notes for Play Store Compliance
- No permissions required (calculator uses none).
- Debug logs removed.
- Release builds are non-debuggable by default.
- ApplicationId: `com.neche.cleanCalc`
- VersionName/VersionCode: `1.0.0 / 1`
