# Project Architecture  --  SoniFlac

> **FLAC Music Player + Internet Radio Discovery for Android**
> Open-source * Freemium * Built for audiophiles and car/Bluetooth use

---

## 1. Overview

SoniFlac is an Android app combining high-quality local FLAC playback with internet radio station discovery (via radio-browser.info), designed from the ground up for seamless Bluetooth/car integration (Tesla steering wheel controls, AVRCP).

### Tech Stack Summary

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 (Material You) |
| Min SDK | API 31 (Android 12) |
| Audio Engine | AndroidX Media3 (ExoPlayer) |
| Networking | Retrofit 2 + OkHttp + Kotlin Serialization |
| Database | Room |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Build | Gradle (Kotlin DSL) with version catalogs |
| CI/CD | GitHub Actions -> Fastlane -> Google Play |
| Testing | JUnit 5 + MockK + Turbine + Compose Test |
| License | GPL-3.0 (copyleft, protects community) |

---

## 2. Module Structure

Multi-module for clean boundaries, testability, and build performance.

```
soniflac/
|---- app/                          # Application entry point
|   |---- src/main/                 # Navigation, MainActivity, App-level DI
|   |---- src/gplay/                # Google Play flavor (billing impl)
|   |---- src/foss/                 # FOSS flavor (build-flag billing impl)
|
|---- core/
|   |---- model/                    # Shared data models (Track, Station, Playlist)
|   |---- database/                 # Room DB, DAOs, entities, migrations
|   |---- network/                  # Retrofit services, API clients
|   |---- player/                   # Media3 playback service, session, AVRCP
|   |---- common/                   # Extensions, utilities, constants
|   |---- testing/                  # Shared test utilities, fakes, fixtures
|
|---- feature/
|   |---- library/                  # Local FLAC library browsing UI
|   |---- radio/                    # Internet radio search/browse/favorites UI
|   |---- nowplaying/               # Now Playing screen, controls, album art
|   |---- streamstats/              # Stream Stats overlay (codec, bitrate, data usage)
|   |---- settings/                 # Settings, equalizer, premium unlock
|
|---- billing/                      # Premium unlock abstraction layer
|   |---- src/main/                 # BillingManager interface
|   |---- src/gplay/                # Google Play Billing Library impl
|   |---- src/foss/                 # BuildConfig flag impl (always-unlocked)
|
|---- build-logic/                  # Convention plugins (shared build config)
|   |---- convention/
|
|---- gradle/
|   |---- libs.versions.toml        # Version catalog
|
|---- .github/
|   |---- workflows/
|       |---- ci.yml                # PR checks: lint, test, build
|       |---- release.yml           # Tagged release -> signed APK -> Play Store
|       |---- nightly.yml           # Optional nightly instrumented tests
|
|---- fastlane/                     # Play Store metadata, screenshots, upload config
|   |---- Appfile
|   |---- Fastfile
|   |---- metadata/
|
|---- build.gradle.kts              # Root build file
|---- settings.gradle.kts           # Module includes
|---- ARCHITECTURE.md               # This file
|---- CONTRIBUTING.md               # Contribution guide
|---- LICENSE                       # GPL-3.0
|---- README.md
```

### Module Dependency Graph

```
feature:library --+
feature:radio ----|
feature:nowplaying|---> core:player ---> core:model
feature:streamstats|   core:database ---> core:model
feature:settings -+    core:network ----> core:model
                       billing
                       core:common (used by all)
                       core:testing (testImplementation only)
```

**Rule**: Feature modules never depend on each other. All shared state flows through `core:` modules.

---

## 3. Architecture Pattern  --  MVVM + Unidirectional Data Flow

```
+-------------+     +--------------+     +--------------+     +--------------+
|  Compose UI  |<----|  ViewModel   |<----|  Use Case /  |<----|  Repository  |
|  (feature/)  |---->|  (StateFlow) |---->|  Domain Logic |---->|  (core/)     |
|---------------+     |----------------+     |----------------+     |----------------+
    observe              emit                transform            data source
    user events          UI state             business rules       Room / Retrofit / Media3
```

- **UI Layer**: Compose screens observe `StateFlow<UiState>` from ViewModels
- **Domain Layer**: Optional use case classes for complex business logic
- **Data Layer**: Repositories abstract data sources (Room, Retrofit, MediaStore)
- **Player**: `PlayerService` is a foreground `MediaSessionService`  --  survives app backgrounding

### State Pattern Example

```kotlin
// Every screen follows this pattern
data class RadioSearchUiState(
    val query: String = "",
    val stations: List<Station> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface RadioSearchEvent {
    data class Search(val query: String) : RadioSearchEvent
    data class ToggleFavorite(val station: Station) : RadioSearchEvent
    data class Play(val station: Station) : RadioSearchEvent
}
```

---

## 4. Key Components  --  Deep Dive

### 4.1 Audio Playback  --  `core:player`

Built on **AndroidX Media3** (successor to ExoPlayer).

```
core/player/
|---- PlaybackService.kt            # MediaSessionService (foreground service)
|---- PlayerManager.kt              # Wraps ExoPlayer, exposes Flow-based state
|---- MediaSessionCallback.kt       # Handles AVRCP commands (skip/pause/play)
|---- AudioFocusManager.kt          # Audio focus handling (pause for calls, etc.)
|---- QueueManager.kt               # Manages playback queue (local + radio)
|---- StreamMetrics.kt              # Collects real-time codec/bitrate/throughput data
|---- BandwidthTracker.kt           # Wraps DataSource to count bytes transferred
|---- notification/
    |---- PlaybackNotification.kt   # Media notification with controls
```

**Why Media3?**
- Native FLAC decoding (no external codecs needed)
- Built-in `MediaSession` with AVRCP support out of the box
- `MediaSessionService` gives proper foreground service lifecycle
- Tesla/car head units see standard media controls automatically

**AVRCP / Bluetooth Integration:**
Media3's `MediaSession` automatically exposes:
- Play / Pause (Tesla left scroll wheel press)
- Next / Previous (Tesla left scroll wheel left/right)
- Seek (if supported by head unit)
- Metadata (track title, artist, album art -> displayed on Tesla screen)

No custom Bluetooth code needed  --  Media3 handles the AVRCP profile.

### 4.2 Internet Radio  --  `core:network` + `feature:radio`

**API**: radio-browser.info (free, no API key required)

```
core/network/
|---- RadioBrowserApi.kt            # Retrofit interface
|   - searchStations(name, tag, country, language, ...)
|   - getTopStations(limit)
|   - getStationsByTag(tag)
|   - getStationsByCountry(country)
|   - getTags()
|   - getCountries()
|   - clickStation(stationUuid)   # Report listen (community stats)
|---- RadioBrowserClient.kt         # Server rotation (DNS-based load balancing)
|---- dto/                          # API response DTOs
|---- interceptors/
    |---- UserAgentInterceptor.kt   # Required: identify your app to the API
```

**Server Selection**: radio-browser.info uses DNS round-robin. On startup, resolve `all.api.radio-browser.info` and pick a random server. Cache for session.

**Station Model:**
```kotlin
data class Station(
    val stationUuid: String,
    val name: String,
    val url: String,              // Stream URL -> feed directly to Media3
    val urlResolved: String,
    val codec: String,            // MP3, AAC, FLAC, OGG, etc.
    val bitrate: Int,
    val country: String,
    val language: String,
    val tags: List<String>,       // Genre tags
    val favicon: String?,         // Station logo URL
    val votes: Int,
    val clickCount: Int,
    val isFavorite: Boolean = false  // Local enrichment
)
```

### 4.3 Local Library  --  `feature:library`

```
feature/library/
|---- scanner/
|   |---- MediaScanner.kt          # Queries MediaStore for audio files
|---- metadata/
|   |---- FlacMetadataReader.kt    # Extract FLAC tags (uses Media3 MetadataRetriever)
|---- ui/
|   |---- LibraryScreen.kt         # Browse by artist/album/folder
|   |---- AlbumScreen.kt
|   |---- ArtistScreen.kt
|---- viewmodel/
    |---- LibraryViewModel.kt
```

Uses `MediaStore.Audio` API (API 31+ scoped storage compliant) to discover local files. Media3 handles FLAC, ALAC, WAV, OGG, MP3, AAC natively.

### 4.4 Database  --  `core:database`

```kotlin
@Database(
    entities = [
        FavoriteStationEntity::class,
        RecentStationEntity::class,
        PlaybackHistoryEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        EqualizerPresetEntity::class,
        DataUsageEntity::class
    ],
    version = 1
)
abstract class SoniFlacDatabase : RoomDatabase() {
    abstract fun favoriteStationDao(): FavoriteStationDao
    abstract fun recentStationDao(): RecentStationDao
    abstract fun playbackHistoryDao(): PlaybackHistoryDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun equalizerPresetDao(): EqualizerPresetDao
    abstract fun dataUsageDao(): DataUsageDao
}
```

### 4.5 Billing  --  `billing/`

**Interface (shared):**
```kotlin
interface BillingManager {
    val isPremium: StateFlow<Boolean>
    suspend fun queryPremiumStatus()
    suspend fun launchPurchaseFlow(activity: Activity)  // No-op in FOSS
}
```

**Google Play flavor** (`src/gplay/`):
- Uses Google Play Billing Library 7.x
- One-time in-app purchase (non-consumable)
- Verifies purchase on each app start

**FOSS flavor** (`src/foss/`):
- `isPremium` always returns `true` (all features unlocked)
- `launchPurchaseFlow` is a no-op or shows "Built from source" message
- Alternatively: controlled by a BuildConfig flag if you ever want a FOSS-but-limited build

**Premium-gated features (MVP):**
- Unlimited saved radio station favorites (free: 5)
- Equalizer
- Sleep timer
- Custom themes beyond default Material You

### 4.6 Stream Stats  --  `core:player` + `feature:streamstats`

A "Stats for Nerds" overlay accessible from the Now Playing screen, providing real-time technical details about the current audio stream and cumulative data usage tracking with configurable limits.

```
core/player/
|---- StreamMetrics.kt              # Collects real-time stream data from Media3
|   - Reads Player.currentFormat -> codec, sampleRate, bitDepth, channels
|   - Reads Player.audioFormat -> actual decoded format info
|   - Polls bandwidth estimator for current throughput
|   - Emits StreamMetricsState via StateFlow (updated every 1s)
|---- BandwidthTracker.kt           # Wrapping DataSource that counts bytes
    - Intercepts read() calls on ProgressiveMediaSource / HlsMediaSource
    - Tracks session bytes, session duration
    - Reports to DataUsageRepository

core/database/
|---- entity/
|   |---- DataUsageEntity.kt        # Daily data usage records
|---- dao/
    |---- DataUsageDao.kt           # Query cumulative usage by day/week/month

feature/streamstats/
|---- ui/
|   |---- StreamStatsOverlay.kt     # Semi-transparent overlay on Now Playing
|---- viewmodel/
|   |---- StreamStatsViewModel.kt   # Combines StreamMetrics + DataUsageRepository
|---- model/
    |---- StreamStatsUiState.kt     # UI state for the overlay
```

**What the overlay displays:**

For local files:
| Field | Source | Example |
|-------|--------|---------|
| Codec | `Format.codecs` | FLAC |
| Sample Rate | `Format.sampleRate` | 44,100 Hz |
| Bit Depth | `Format.pcmEncoding` | 16-bit |
| Channels | `Format.channelCount` | Stereo |
| Bitrate | `Format.averageBitrate` | 923 kbps |
| File Size | `MediaItem` metadata | 34.2 MB |

For streams (radio / Jellyfin), adds:
| Field | Source | Example |
|-------|--------|---------|
| Network Throughput | `BandwidthTracker` | 128 kbps |
| Buffer Health | `Player.bufferedPosition - currentPosition` | 12.4s |
| Session Data Used | `BandwidthTracker.sessionBytes` | 47.3 MB |
| Today's Total | `DataUsageDao.getTodayUsage()` | 312 MB |
| Monthly Total | `DataUsageDao.getMonthUsage()` | 4.2 GB |

**Data Usage Tracking:**

```kotlin
@Entity(tableName = "data_usage")
data class DataUsageEntity(
    @PrimaryKey
    val date: LocalDate,            // One row per day
    val bytesStreamed: Long,         // Cumulative bytes for the day
    val streamingDurationMs: Long    // Total streaming time
)

// State exposed to the overlay
data class StreamStatsUiState(
    // Current track info
    val codec: String = "",
    val sampleRate: Int = 0,
    val bitDepth: Int = 0,
    val channels: Int = 0,
    val bitrate: Int = 0,
    val fileSize: Long? = null,
    // Network (streams only)
    val isStreaming: Boolean = false,
    val networkThroughputKbps: Int = 0,
    val bufferHealthSeconds: Float = 0f,
    val sessionBytesUsed: Long = 0,
    // Data usage
    val todayBytesUsed: Long = 0,
    val monthBytesUsed: Long = 0,
    val monthlyLimitBytes: Long? = null  // null = no limit set
)
```

**Data Limit & Warning System:**

Users can set a monthly data limit in Settings (e.g., 2 GB). The app tracks cumulative streaming usage per day in Room, and:
- Displays a progress bar in Stream Stats showing usage vs. limit
- Shows a warning notification at 80% of limit
- Shows an alert dialog at 100% (does not hard-stop  --  user can dismiss and continue)
- Resets monthly counter on the 1st of each month (or user-configurable billing cycle date)

**Implementation notes:**
- `BandwidthTracker` wraps Media3's `DataSource` via `DataSource.Factory` decoration  --  no custom HTTP stack needed
- `StreamMetrics` uses a 1-second polling interval via `tickerFlow` to read player state
- All data usage writes are batched (not per-byte)  --  `BandwidthTracker` flushes to Room every 30 seconds and on session end
- Local file playback does NOT count toward data usage (only network-sourced audio)
- `TrafficStats.getUidRxBytes()` serves as a cross-check but is not the primary source (it includes non-audio traffic like album art fetches)

---

## 5. Build Variants

```kotlin
// app/build.gradle.kts
android {
    flavorDimensions += "distribution"
    productFlavors {
        create("gplay") {
            dimension = "distribution"
            buildConfigField("boolean", "IS_FOSS", "false")
        }
        create("foss") {
            dimension = "distribution"
            buildConfigField("boolean", "IS_FOSS", "true")
        }
    }
}
```

This produces four build variants:
- `fossDebug`  --  for local dev and F-Droid
- `fossRelease`  --  F-Droid release
- `gplayDebug`  --  dev with Play Billing
- `gplayRelease`  --  Google Play Store release

---

## 6. Testing Strategy

### 6.1 Test Pyramid

```
         /  UI Tests  \          ~10%   Compose UI tests, navigation
        /--------------\
       / Integration    \        ~20%   Repository + DB, API + mocks
      /------------------\
     /    Unit Tests      \      ~70%   ViewModels, use cases, mappers
    /----------------------\
```

### 6.2 Testing Libraries

| Library | Purpose |
|---------|---------|
| JUnit 5 | Test framework |
| MockK | Kotlin-native mocking |
| Turbine | Testing Kotlin Flows (StateFlow assertions) |
| Compose UI Test | Compose screen testing |
| Robolectric | Android framework tests without emulator |
| OkHttp MockWebServer | API integration tests |
| Room In-Memory DB | Database tests |
| Hilt Testing | DI-aware tests |

### 6.3 What Gets Tested

**Unit tests (`test/`)**  --  run on JVM, fast:
- Every ViewModel: state transitions, event handling, error cases
- Every Repository: data mapping, caching logic, error handling
- API DTOs: serialization/deserialization with sample JSON fixtures
- Use cases: business logic (search filtering, queue management)
- Player state machine: play/pause/skip/queue transitions
- Billing manager: premium gating logic
- StreamMetrics: format parsing, bitrate calculation, state emission
- BandwidthTracker: byte counting, flush batching, session lifecycle
- DataUsageRepository: daily aggregation, monthly rollup, limit checks

**Integration tests (`test/`)**  --  run on JVM with Robolectric:
- Room DAOs: CRUD operations, migrations, queries
- DataUsageDao: daily/monthly aggregation queries, billing cycle rollover
- Retrofit + MockWebServer: API contract tests with fixture JSON
- Repository + real Room DB: full data flow

**UI tests (`androidTest/`)**  --  run on emulator/device:
- Screen rendering: each screen renders correctly for each state
- Navigation: screen transitions work
- Player controls: play/pause/skip update UI correctly
- Radio search: type -> results -> play flow
- Stream Stats overlay: renders correct fields for local vs. stream sources

### 6.4 Test Conventions

```kotlin
// Naming: functionName_condition_expectedResult
@Test
fun searchStations_withRockTag_returnsFilteredStations() { ... }

@Test
fun toggleFavorite_whenNotPremiumAndAtLimit_showsUpgradePrompt() { ... }

// Every ViewModel test follows this structure:
@Test
fun onSearch_emitsLoadingThenResults() = runTest {
    val viewModel = RadioSearchViewModel(fakeRepository)
    viewModel.uiState.test {   // Turbine
        awaitItem() shouldBe RadioSearchUiState()  // initial
        viewModel.onEvent(RadioSearchEvent.Search("jazz"))
        awaitItem().isLoading shouldBe true
        awaitItem().stations shouldHaveSize 10
    }
}
```

### 6.5 `core:testing`  --  Shared Test Infrastructure

```
core/testing/
|---- fakes/
|   |---- FakeRadioBrowserApi.kt      # Returns fixture data
|   |---- FakePlayerManager.kt        # Simulates playback state
|   |---- FakeBillingManager.kt       # Configurable premium state
|   |---- FakeStreamMetrics.kt        # Emits configurable codec/bitrate states
|   |---- FakeBandwidthTracker.kt     # Simulates byte counting
|   |---- FakeStationRepository.kt
|---- fixtures/
|   |---- StationFixtures.kt          # Pre-built Station objects
|   |---- TrackFixtures.kt
|   |---- json/                       # Raw API response JSON files
|       |---- stations_search.json
|       |---- stations_by_tag.json
|       |---- countries.json
|---- rules/
    |---- MainDispatcherRule.kt       # Replaces Dispatchers.Main in tests
```

---

## 7. CI/CD Pipeline

### 7.1 PR Checks  --  `.github/workflows/ci.yml`

Triggered on: every PR to `main`, every push to `main`

```yaml
Steps:
  1. Checkout code
  2. Setup JDK 17
  3. Gradle cache restore
  4. Run ktlint (code style)
  5. Run detekt (static analysis)
  6. Run unit tests (all modules, gplay + foss variants)
  7. Build debug APKs (gplay + foss)
  8. Upload test reports as artifacts
  9. Comment test summary on PR
```

**Branch protection rules:**
- Require passing CI before merge
- Require at least 1 review
- No direct pushes to `main`

### 7.2 Release  --  `.github/workflows/release.yml`

Triggered on: push of tag matching `v*` (e.g., `v1.0.0`)

```yaml
Steps:
  1. Checkout code
  2. Setup JDK 17
  3. Decrypt signing keystore (stored as GitHub Secret)
  4. Run full test suite
  5. Build signed release APK + AAB (gplayRelease)
  6. Build signed release APK (fossRelease)
  7. Upload gplayRelease AAB to Google Play (internal track) via Fastlane
  8. Create GitHub Release with:
     - Changelog (auto-generated from commits)
     - gplay APK attachment
     - foss APK attachment
  9. Notify (optional: Discord/Matrix webhook)
```

### 7.3 Signing & Secrets

GitHub repository secrets needed:
```
KEYSTORE_BASE64          # Base64-encoded release keystore
KEYSTORE_PASSWORD        # Keystore password
KEY_ALIAS                # Key alias
KEY_PASSWORD             # Key password
PLAY_STORE_JSON_KEY      # Google Play service account JSON
```

The keystore is base64-encoded and decoded during CI:
```bash
echo "$KEYSTORE_BASE64" | base64 --decode > release.keystore
```

### 7.4 Fastlane Configuration

```ruby
# fastlane/Fastfile
default_platform(:android)

platform :android do
  desc "Deploy to Google Play internal track"
  lane :deploy_internal do
    upload_to_play_store(
      track: 'internal',
      aab: '../app/build/outputs/bundle/gplayRelease/app-gplay-release.aab',
      json_key: '../play-store-key.json',
      skip_upload_metadata: false,
      skip_upload_images: true
    )
  end

  desc "Promote internal to production"
  lane :promote_production do
    upload_to_play_store(
      track: 'internal',
      track_promote_to: 'production',
      json_key: '../play-store-key.json'
    )
  end
end
```

---

## 8. Navigation  --  Compose Navigation

```
Bottom Nav Bar:
+--------------------------------+
| Library  |  Radio   | Settings |
|----------------------------------+

Library Tab:                    Radio Tab:
|---- LibraryScreen               |---- RadioSearchScreen
|   |---- -> ArtistScreen          |   |---- Search by name/genre/country
|   |   |---- -> AlbumScreen       |   |---- -> StationDetailScreen
|   |---- -> AlbumScreen           |---- RadioFavoritesScreen
|   |---- -> FolderBrowserScreen   |---- RadioHistoryScreen

Overlay (persistent):
|---- NowPlayingBar (bottom, expandable to full NowPlayingScreen)
    |---- StreamStatsOverlay (toggle from Now Playing menu / long-press)
```

---

## 9. Premium Feature Gating

```kotlin
// Usage in any ViewModel or UI
@HiltViewModel
class RadioFavoritesViewModel @Inject constructor(
    private val billingManager: BillingManager,
    private val repository: StationRepository
) : ViewModel() {

    fun toggleFavorite(station: Station) {
        viewModelScope.launch {
            val currentFavorites = repository.getFavoriteCount()
            val isPremium = billingManager.isPremium.value

            if (!isPremium && currentFavorites >= FREE_FAVORITE_LIMIT) {
                _uiState.update { it.copy(showUpgradePrompt = true) }
                return@launch
            }
            repository.toggleFavorite(station)
        }
    }

    companion object {
        const val FREE_FAVORITE_LIMIT = 5
    }
}
```

---

## 10. MVP Feature Scope

### Phase 1  --  MVP (target: 6-8 weeks)
- [ ] Local FLAC/MP3/OGG file browsing and playback
- [ ] Internet radio search via radio-browser.info (by name, genre, country)
- [ ] Play radio streams
- [ ] Save favorite stations (5 free, unlimited premium)
- [ ] Now Playing screen with controls
- [ ] Media notification with playback controls
- [ ] Bluetooth AVRCP support (play/pause/skip from car controls)
- [ ] Material You theming
- [ ] Premium unlock (Play Billing + FOSS flag)
- [ ] Stream Stats overlay (codec, sample rate, bit depth, bitrate, throughput, buffer health)
- [ ] Data usage tracking (session, daily, monthly) with configurable monthly limit and warnings
- [ ] Full unit test coverage on ViewModels and repositories
- [ ] CI pipeline (PR checks + release automation)

### Phase 2  --  Post-MVP
- [ ] Jellyfin / Subsonic server connectivity
- [ ] Equalizer with presets
- [ ] Sleep timer
- [ ] Radio station history / recently played
- [ ] Offline caching for radio favorites
- [ ] Android Auto support
- [ ] Playlist management
- [ ] Album art fetching for local files
- [ ] Station recommendations based on listening history

### Phase 3  --  Community-Driven
- [ ] Navidrome integration
- [ ] Plex integration
- [ ] Chromecast / DLNA output
- [ ] Lyrics display
- [ ] Gapless playback fine-tuning
- [ ] Wear OS companion
- [ ] Widget for home screen

---

## 11. Key Dependencies  --  Version Catalog

```toml
# gradle/libs.versions.toml

[versions]
kotlin = "2.1.0"
agp = "8.7.3"
compose-bom = "2025.01.01"
media3 = "1.5.1"
hilt = "2.53.1"
room = "2.6.1"
retrofit = "2.11.0"
okhttp = "4.12.0"
kotlinx-serialization = "1.7.3"
coroutines = "1.9.0"
junit5 = "5.11.4"
mockk = "1.13.14"
turbine = "1.2.0"

[libraries]
# Compose
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-navigation = { group = "androidx.navigation", name = "navigation-compose", version = "2.8.5" }
compose-ui-test = { group = "androidx.compose.ui", name = "ui-test-junit4" }

# Media3
media3-exoplayer = { group = "androidx.media3", name = "media3-exoplayer", version.ref = "media3" }
media3-session = { group = "androidx.media3", name = "media3-session", version.ref = "media3" }
media3-ui = { group = "androidx.media3", name = "media3-ui", version.ref = "media3" }

# Networking
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-mockwebserver = { group = "com.squareup.okhttp3", name = "mockwebserver", version.ref = "okhttp" }

# Database
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }

# DI
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }
hilt-testing = { group = "com.google.dagger", name = "hilt-android-testing", version.ref = "hilt" }

# Testing
junit5 = { group = "org.junit.jupiter", name = "junit-jupiter", version.ref = "junit5" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

# Billing (gplay only)
billing = { group = "com.android.billingclient", name = "billing-ktx", version = "7.1.1" }

# Image loading
coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version = "3.0.4" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "2.1.0-1.0.29" }
```

---

## 12. Repository Setup Checklist

```
GitHub Repository Setup:
|---- [x] Create repo: github.com/particlesector/Soniflac
|---- [x] Add LICENSE (GPL-3.0)
|---- [x] Add .gitignore (Android template)
|---- [x] Add ARCHITECTURE.md (this document)
|---- [x] Add CONTRIBUTING.md
|---- [x] Add README.md
|---- [ ] Configure branch protection on main
|---- [x] Add GitHub Actions workflows
|---- [x] Add issue templates (bug report, feature request)
|---- [x] Add PR template
|
|---- Development Environment:
    |---- [ ] Android Studio Ladybug or newer
    |---- [ ] JDK 17
    |---- [ ] Android SDK 35
```

---

## 13. Coding Standards

- **Formatting**: ktlint with default rules, enforced in CI
- **Static analysis**: detekt with default + compose rules
- **Naming**: standard Kotlin conventions, no Hungarian notation
- **Commits**: Conventional Commits (`feat:`, `fix:`, `docs:`, `test:`, `ci:`)
  - Enables automatic changelog generation
- **PRs**: must include tests for new functionality
- **Documentation**: KDoc on all public interfaces and complex functions
- **Error handling**: sealed Result types, no silent catch blocks

---

*This document is the source of truth for project architecture decisions. Update it via PR when architecture evolves.*
