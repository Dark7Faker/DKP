# DKP

DKP is an Android app for analyzing runs of the original Donkey Kong Arcade game by manually entering level data.

## Features

- Enter score, bonus, and deaths for each level manually by selecting levels 4 through 21.
- Automatically calculate pace, current average, needed average based on goal, and progress values.
- Use a battery saver mode that dims the screen while playing.
- View the pace history in an interactive chart and select levels directly from the chart.
- Save, review, edit, delete, and sort runs by date, name, score, pace, or level.
- Compare personal bests in the PB Improvement chart.

## How to Use

See the [How to use guide](https://darkfaker.notion.site/dkp-how-to-use) for instructions on using the app.

## Data and Network Usage

The app only accesses data you enter, such as run details and app settings. It also uses the locale and time zone to format dates. The App does not access other personal data on your device, such as contacts, photos, or files. Run data and settings are stored locally on the device, and the app does not transmit data over the network. The app code makes no network requests and does not request internet access. Depending on device settings, Android may still back up app data. 

## Independence from Donkey Kong

DKP is an independent companion app and is not affiliated with, endorsed, or sponsored by Nintendo. It does not connect to the Donkey Kong game or read, import, or collect data from it. Players must enter all scores and run statistics manually; the app only calculates and displays information provided by the player.

The app is not intended to copy or reproduce any Donkey Kong game features. Its purpose is to provide players with an independent mathematical framework for tracking and analyzing their play, based solely on their own manual entries.

## Install

1. On your Android phone, open the [latest GitHub release](https://github.com/Superflo187/DKPace/releases/latest) and download the APK asset.
2. Open the downloaded APK and follow Android's installation prompts. Android may ask you to allow your browser or file manager to install apps from that source.
3. Google Play Protect may show an unfamiliar-app warning because DKP is installed directly from a release APK by an unknown developer rather than through Google Play. This warning alone does not necessarily mean that Play Protect has identified the app as harmful. If Play Protect offers to scan the APK, you can choose to scan it and review the result before proceeding.

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
