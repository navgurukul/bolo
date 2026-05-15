# Bolo — Architecture (Phase 0)

One Android app, one screen at a time, one shared phone. Single activity, Compose
navigation, Hilt-injected ViewModels, Room for the only thing we persist
(aggregates and — from Phase 2 — encrypted voice embeddings).

## Module map

```
app/src/main/java/co/bolo/app/
├── BoloApp.kt             // @HiltAndroidApp entry
├── MainActivity.kt        // single activity, edge-to-edge, hosts BoloNavHost
│
├── ui/
│   ├── theme/             // Whisper palette + Geist / Newsreader / Mono roles
│   ├── nav/               // BoloNavHost, Routes
│   ├── home/              // Home: cohort + students + recent sessions
│   ├── enrollment/        // 10-sec mocked enrollment flow
│   ├── session/           // topic pick → fake-timer live screen
│   ├── summary/           // group %, per-student bars, top-speaker callout, reflection
│   ├── dashboard/         // per-student trend, topic filter, forget-voice
│   └── components/        // BreathingRedDot, EnglishRing, TrendChart, StudentBar,
│                          //   TopicPill, Wordmark, Hairline
│
├── audio/                 // (Phase 1+) AudioCapture, Vad, LanguageClassifier,
│                          //   SpeakerIdentifier, SessionEngine — RAM only
│
├── data/
│   ├── model/             // Cohort, Student, Session, SpeakerStat — aggregates only
│   ├── db/                // BoloDatabase + DAOs
│   ├── repo/              // CohortRepo, SessionRepo
│   └── seed/              // Seed.kt — Phase 0 demo data
│
├── di/                    // AppModule provides DB + DAOs
└── util/                  // Format (percent, minutes, relativeDay, clockMs)
```

## Why these modules

- `ui/` is split by feature, not by widget type. Each subfolder owns its screen,
  its ViewModel, and any feature-specific composables. Shared visuals (red dot,
  ring, trend chart) live in `ui/components/`.
- `audio/` is empty in Phase 0. It is the only package allowed to touch
  `android.media.AudioRecord`. Phase 1 will populate it; an enforcement lint rule
  is on the Phase 1 checklist.
- `data/` is thin. The model is four entities and two repos. We add fields when
  a screen earns them, never speculatively.
- `di/` has one module today (DB + DAOs). Audio components will land in their own
  module in Phase 1 so they can be swapped per build variant for tests.

## State flow

```
Compose Screen  ──▶  HiltViewModel  ──▶  Repo  ──▶  Room DAO
       ▲                  │
       └──── StateFlow ◀──┘
```

ViewModels expose a single `StateFlow<UiState>` per screen. Screens consume it
with `collectAsStateWithLifecycle()`. No callbacks back into ViewModels except
through explicit method calls (`vm.start()`, `vm.selectTopic(t)`).

## Navigation

`androidx.navigation.compose` with five string routes. Args are entity ids only.
ViewModels resolve full objects via `SavedStateHandle` + repos — never serialize
domain models into nav args.

## The non-negotiables, enforced where they bite

- `android:allowBackup="false"` + empty `backup_rules.xml` + empty
  `data_extraction_rules.xml`. Voice embeddings never escape via autobackup.
- `INTERNET` permission is `tools:node="remove"` until Phase 5 sync. Phase 0
  literally cannot make a network call.
- `BreathingRedDot` is a tiny composable; Phase 1 will wire it to the audio
  thread so it is structurally impossible to listen without showing the dot.

## What we deferred (and why)

- **Dark theme.** Whisper palette is light-only by design. Revisit in Phase 5.
- **Bundled fonts.** Phase 0 falls back to system Sans/Serif/Mono so the role
  separation (UI / accent / data) is testable without shipping font binaries.
  Phase 4 swaps the `FontFamily` values in `theme/Type.kt`; call sites stay stable.
- **SQLCipher / encrypted embeddings.** Embeddings don't exist yet. The column is
  `ByteArray?`. Phase 2 wraps the bytes with AES via `androidx.security.crypto`
  using a Keystore-backed master key.
- **Real audio.** Phase 1 owns `AudioCapture`, `Vad`, and the no-disk-write
  invariant test.
