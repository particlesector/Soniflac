# SoniFlac

[![CI](https://github.com/particlesector/Soniflac/actions/workflows/ci.yml/badge.svg)](https://github.com/particlesector/Soniflac/actions/workflows/ci.yml)
[![License: GPL-3.0](https://img.shields.io/badge/License-GPL%203.0-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-12%2B-green.svg)](https://developer.android.com/about/versions/12)

**FLAC music player + internet radio for Android**

Open-source, freemium, built for audiophiles and car/Bluetooth use.

[soniflac.com](https://soniflac.com/)

---

## Features

### Local Playback
- Browse and play **FLAC, MP3, OGG, AAC, WAV, and ALAC** files from your device
- Browse by artist, album, or folder
- Full metadata extraction via Media3

### Internet Radio
- Search and stream thousands of stations via [radio-browser.info](https://www.radio-browser.info/)
- Filter by name, genre, country, or language
- Save favorites (5 free, unlimited with premium)
- Station vote counts and popularity data

### Bluetooth & Car Integration
- Full **AVRCP** support for play/pause/skip from steering wheel controls
- Tested with Tesla — track title, artist, and album art display on car screen
- Works with any AVRCP-compatible head unit or Bluetooth device

### Stream Stats
A "stats for nerds" overlay on the Now Playing screen showing real-time technical details:

| Local Files | Streams |
|-------------|---------|
| Codec (FLAC, MP3, etc.) | Everything from local, plus: |
| Sample rate | Network throughput |
| Bit depth | Buffer health |
| Channel count | Session data used |
| Bitrate | Today's / monthly total |

### Data Usage Tracking
- Monitor daily and monthly streaming data consumption
- Configurable monthly limit with warnings at 80% and alert at 100%
- Per-session and cumulative tracking stored locally
- Only network-sourced audio counts (local playback excluded)

### Material You
- Dynamic theming with Material 3
- Follows system color palette

### Premium (Freemium Model)
Core features are free. Premium unlocks:
- Unlimited saved radio station favorites
- Equalizer with presets
- Sleep timer
- Custom themes beyond Material You defaults

**FOSS builds have all premium features unlocked by default.**

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 |
| Min SDK | API 31 (Android 12) |
| Audio | AndroidX Media3 (ExoPlayer) |
| Networking | Retrofit 2 + OkHttp + Kotlin Serialization |
| Database | Room |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Image Loading | Coil 3 |
| Build | Gradle (Kotlin DSL) with version catalogs |
| CI/CD | GitHub Actions + Fastlane |
| Testing | JUnit 5 + MockK + Turbine + Compose Test |

---

## Project Structure

```
soniflac/
├── app/                  # App entry point, navigation, flavors (gplay / foss)
├── core/
│   ├── model/            # Shared data models (Track, Station, Playlist)
│   ├── database/         # Room DB, DAOs, entities, migrations
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

See [ARCHITECTURE.md](ARCHITECTURE.md) for the full design document, including module dependency graph, data models, and implementation details.

---

## Build Variants

| Variant | Description |
|---------|-------------|
| `gplayDebug` | Development with Google Play Billing |
| `gplayRelease` | Google Play Store release |
| `fossDebug` | Development, all features unlocked |
| `fossRelease` | F-Droid / sideload release |

The `IS_FOSS` BuildConfig flag controls premium feature gating per flavor.

---

## Building from Source

### Requirements

- Android Studio Ladybug or newer
- JDK 17
- Android SDK 35

### Build

```bash
git clone https://github.com/particlesector/Soniflac.git
cd Soniflac

# FOSS debug build (all features unlocked, recommended for development)
./gradlew assembleFossDebug

# Google Play debug build (requires Play Billing setup)
./gradlew assembleGplayDebug
```

### Run Checks Locally

```bash
# Lint and static analysis
./gradlew ktlintCheck detekt

# Unit tests
./gradlew testFossDebugUnitTest
./gradlew testGplayDebugUnitTest

# All checks (mirrors CI)
./gradlew ktlintCheck detekt testFossDebugUnitTest testGplayDebugUnitTest assembleFossDebug assembleGplayDebug
```

---

## CI/CD

### Pull Request Checks

Every PR to `main` runs:
1. **Lint** — ktlint + detekt static analysis
2. **Unit Tests** — both `gplay` and `foss` flavors
3. **Build** — debug APKs for both flavors

All checks must pass before merge.

### Release Pipeline

Triggered by pushing a tag matching `v*` (e.g., `v1.0.0`):
1. Runs full test suite
2. Builds signed release APK + AAB (`gplayRelease`)
3. Builds signed release APK (`fossRelease`)
4. Uploads to Google Play (internal track) via Fastlane
5. Creates GitHub Release with changelog and APK attachments

---

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) before submitting a PR.

Key points:
- This project uses [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `test:`, `ci:`, `refactor:`, `chore:`)
- PRs must include tests for new functionality
- All CI checks must pass
- Feature modules never depend on each other — route shared state through `core/`

---

## License

SoniFlac is licensed under the [GNU General Public License v3.0](LICENSE).

By contributing, you agree that your contributions will be licensed under the same license.
