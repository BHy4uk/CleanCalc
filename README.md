# CleanCalc (Native Android)

## Open in Android Studio
1. Launch Android Studio (latest stable).
2. Click **Open** and select `/app/android`.
3. Let Gradle sync complete.

## Generate Keystore
```bash
keytool -genkey -v -keystore cleancalc.keystore -alias cleancalc   -keyalg RSA -keysize 2048 -validity 10000
```
Move `cleancalc.keystore` to:
```
/app/android/app/cleancalc.keystore
```

## Build Release AAB
1. Add signing properties to `/app/android/gradle.properties`:
```
RELEASE_STORE_FILE=cleancalc.keystore
RELEASE_KEY_ALIAS=cleancalc
RELEASE_STORE_PASSWORD=YOUR_STORE_PASSWORD
RELEASE_KEY_PASSWORD=YOUR_KEY_PASSWORD
```
2. Reference the signing config in `/app/android/app/build.gradle.kts`:
```
android {
  signingConfigs {
    create("release") {
      storeFile = file(RELEASE_STORE_FILE)
      storePassword = RELEASE_STORE_PASSWORD
      keyAlias = RELEASE_KEY_ALIAS
      keyPassword = RELEASE_KEY_PASSWORD
    }
  }
  buildTypes {
    release {
      signingConfig = signingConfigs.getByName("release")
    }
  }
}
```
3. Build the bundle:
```bash
cd /app/android
./gradlew bundleRelease
```
AAB output:
```
/app/android/app/build/outputs/bundle/release/app-release.aab
```
