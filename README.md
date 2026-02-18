# SoniFlac

**FLAC music player + internet radio for Android**

Open-source, freemium, built for audiophiles and car/Bluetooth use.

[soniflac.com](https://soniflac.com/)

## Features

- **Local FLAC playback** — browse and play FLAC, MP3, OGG, AAC, WAV, and ALAC files from your device
- **Internet radio** — search and stream thousands of stations via [radio-browser.info](https://www.radio-browser.info/) (by name, genre, or country)
- **Bluetooth & car integration** — full AVRCP support for play/pause/skip from steering wheel controls (tested with Tesla)
- **Stream Stats** — "stats for nerds" overlay showing codec, sample rate, bit depth, bitrate, buffer health, and network throughput
- **Data usage tracking** — monitor daily and monthly streaming data with configurable limits and warnings
- **Material You** — dynamic theming with Material 3
- **Freemium model** — core features are free; premium unlocks unlimited favorites, equalizer, sleep timer, and custom themes
- **FOSS-friendly** — Google Play and FOSS build variants; FOSS builds have all features unlocked

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 |
| Min SDK | API 31 (Android 12) |
| Audio | AndroidX Media3 (ExoPlayer) |
| Networking | Retrofit 2 + OkHttp |
| Database | Room |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Build | Gradle (Kotlin DSL) with version catalogs |
| CI/CD | GitHub Actions + Fastlane |
| Testing | JUnit 5 + MockK + Turbine + Compose Test |

## Project Structure

```
soniflac/
├── app/                  # App entry point, navigation, flavors (gplay / foss)
├── core/
│   ├── model/            # Shared data models
│   ├── database/         # Room DB, DAOs, entities
│   ├── network/          # Retrofit services, radio-browser.info client
│   ├── player/           # Media3 playback service, AVRCP, stream metrics
│   ├── common/           # Extensions and utilities
│   └── testing/          # Shared test fakes and fixtures
├── feature/
│   ├── library/          # Local file browsing UI
│   ├── radio/            # Internet radio search/browse UI
│   ├── nowplaying/       # Now Playing screen and controls
│   ├── streamstats/      # Stream Stats overlay
│   └── settings/         # Settings and premium unlock
├── billing/              # Premium billing abstraction (Play Billing / FOSS)
└── build-logic/          # Convention plugins
```

Feature modules never depend on each other — all shared state flows through `core/` modules.

## Build Variants

| Variant | Description |
|---------|-------------|
| `gplayDebug` | Development with Play Billing |
| `gplayRelease` | Google Play Store release |
| `fossDebug` | Development, all features unlocked |
| `fossRelease` | F-Droid / sideload release |

## Building from Source

**Requirements:** Android Studio Ladybug+, JDK 17, Android SDK 35

```bash
git clone https://github.com/particlesector/Soniflac.git
cd Soniflac

# FOSS debug build (all features unlocked)
./gradlew assembleFossDebug

# Google Play debug build
./gradlew assembleGplayDebug
```

## Contributing

Contributions are welcome. Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

This project uses [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `test:`, `ci:`).

## License

SoniFlac is licensed under the [GNU General Public License v3.0](LICENSE).