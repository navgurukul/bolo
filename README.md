# Bolo

*Speak more English, together.*

One shared Android phone in the middle of a 5–10 person group. The facilitator
taps Start, picks a topic, and the phone listens. On Stop it shows how many
minutes of English actually happened — by speaking time, per student, with a
positive "most English today" callout. Each student has their own dashboard
with a trend across sessions.

No audio leaves the phone. Ever.

## Status — Phase 0

Project skeleton + clickable walkthrough with seed data. No real audio yet.

What works on an emulator today:

- **Home** — cohort + students + recent sessions, big black "Start a session" button.
- **Enrollment** — guided 10-second flow (placeholder, no real recording).
- **Session** — pick a topic pill or type your own, breathing-red-dot live screen
  with a fake timer, English-share ring (turns amber when the simulation drifts).
- **Summary** — group %, per-student bars, "most English today" callout, italic
  reflection prompt. Saves a real `Session` + `SpeakerStat` rows to Room.
- **Dashboard** — line chart of a student's English % across sessions, topic
  filter, "Forget my voice" action.

## Run it

Requires Android Studio with the AGP 8.7+ / Kotlin 2.1 toolchain.

1. Open the project root in Android Studio.
2. Let Gradle sync.
3. Run `app` on an Android 10+ emulator (Pixel 4 or 5 image works well).

A fresh DB is seeded on first launch with one cohort ("NavGurukul · Batch 7"),
six students, and four past sessions (`data/seed/Seed.kt`).

## Repo layout

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the module-by-module
explanation.

```
app/                 Android app (single activity, Compose, MVVM, Hilt, Room)
design/              The design handoff bundle (HTML + JSX prototypes)
docs/
├── ARCHITECTURE.md  Module map and why each one earns its place
├── PRIVACY.md       The "no audio to disk" guarantee in plain English
└── DECISIONS.md     ADRs as we go
```

## Phase plan

| Phase | What lands | Status |
|---|---|---|
| 0 | Skeleton + clickable walkthrough with mock data | **done** |
| 1 | Real `AudioRecord` capture, VAD, RAM-only buffer + invariant test | next |
| 2 | Voice enrollment + speaker identification | |
| 3 | Binary English / not-English classifier | |
| 4 | Real data into summary + dashboard, topic tagging, optional live cues | |
| 5 | Polish, settings, Firestore summary-only sync | |
