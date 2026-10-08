# DKP

DKP is an Android app for recording and analyzing runs of the original Donkey Kong Arcade game.

## Features

- Enter score, bonus, and deaths for each level manually by selecting levels 4 through 21.
- Automatically calculate pace, current average, needed average based on goal, and progress values.
- Use a battery saver mode that dims the screen while playing.
- View the pace history in an interactive chart and select levels directly from the chart.
- Save, review, edit, delete, and sort runs by date, name, score, pace, or level.
- Compare personal bests in the PB Improvement chart.

## Data and Network Usage

The app only accesses data you enter, such as run details and app settings. It does not access other personal data on your device, such as contacts, photos, or files. Run data and settings are stored locally on the device, and the app does not transmit data over the network. The app code makes no network requests and does not request internet access. Depending on device settings, Android may still back up app data. 

## Requirements

- Android Studio with the Android SDK for API 37
- Android 8.0 (API 26) or later to run the app
- The project includes Gradle Wrapper scripts for building

## Build Locally

Build a debug version on Windows:

```powershell
.\gradlew.bat assembleDebug
```

On macOS or Linux, use `./gradlew assembleDebug`.

The APK is created at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Build a release version on Windows:

```powershell
.\gradlew.bat assembleRelease
```

Without release signing configured, Gradle creates an unsigned release APK. It must be signed before installation or distribution. In Android Studio, use **Build → Generate Signed Bundle / APK…**.

## Technology

- Kotlin
- Jetpack Compose and Material 3
- Gradle Wrapper
- `applicationId`: `com.darkfaker.dkp`

## License

This project is licensed under the [Apache License 2.0](LICENSE).
