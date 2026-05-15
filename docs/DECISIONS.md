# Architecture Decision Records

Short ADRs as we go. Newest at the bottom.

---

## ADR-001 — Single activity + Compose navigation

**Context.** The PRD says "no nested menus, one screen one purpose." Compose +
navigation-compose gives that with the least ceremony. Multi-activity would
fragment lifecycle and complicate the audio capture handoff in Phase 1.

**Decision.** One `MainActivity`. `BoloNavHost` owns the five routes.

**Consequences.** Audio capture in Phase 1 will live in a foreground Service or
a process-scoped singleton; we don't get an activity-per-screen lifecycle to lean
on. Net positive — fewer surprises around lifecycle and the recording state.

---

## ADR-002 — Whisper palette, light-only

**Context.** Design handoff (chat1.md) finalized on the Whisper variant: warm
off-white `#F4F1E9`, sage `#6D9176`, breathing red `#C14F4F`. Dark mode was
never part of the explored design.

**Decision.** Ship light-only in Phase 0. `BoloTheme` accepts a `darkTheme`
param but ignores it. `themes.xml` and `themes-night.xml` both point at the
light theme so the system bars stay correct on dark-mode devices.

**Consequences.** Re-open in Phase 5 polish only if there is evidence
classroom-shared-phone users want it.

---

## ADR-003 — Fonts: system fallbacks for Phase 0

**Context.** Design specifies Geist for UI, Newsreader italic for the three
human moments (wordmark, read-aloud sentence, reflection prompt), Geist Mono
for data labels. Bundling 4 font files adds ~1.5 MB and risks shipping wrong
weights before we test type at small sizes on a low-DPI device.

**Decision.** Use `FontFamily.SansSerif` / `.Serif` / `.Monospace` in Phase 0,
behind aliases (`SansUI`, `SerifAccent`, `MonoData`) in `ui/theme/Type.kt`.
Phase 4 swaps the family values; call sites don't move.

**Consequences.** Phase 0 typography looks system-flavored, not on-brand-final.
That is acceptable for a clickable walkthrough. Reviewers will see the role
separation (sans / serif italic / mono) and judge layout — not final brand feel.

---

## ADR-004 — No-disk-write enforcement deferred to Phase 1

**Context.** Phase 0 has no audio code, so the invariant cannot be violated.
The check is meaningful only once `AudioCapture` exists.

**Decision.** Phase 0 ships the surrounding guards (`allowBackup=false`,
`INTERNET` removed via manifest merger, backup/data-extraction rules
exclude-all). Phase 1 adds (a) a debug-only `FileWatcher` over `filesDir` /
`cacheDir` / `noBackupFilesDir`, (b) a custom lint rule forbidding
`MediaRecorder`, `FileOutputStream`, and `RandomAccessFile` imports from
package `co.bolo.app.audio`, and (c) an instrumentation test that records for
5 minutes and asserts no audio-shaped files appeared.

**Consequences.** A reviewer of Phase 0 cannot yet point a static analyzer at
the code and say "proven." That's honest — there is nothing to prove yet.

---

## ADR-005 — Seed data lives in the DI module

**Context.** Phase 0 wants a runnable demo. We don't want to ship a seed in
Phase 1+ release builds, but we also don't want a separate "demo" flavor yet.

**Decision.** `Seed.kt` is idempotent (keyed by cohort id) and re-runs at app
start. Phase 1 will gate the seed call behind `BuildConfig.DEBUG`. Phase 4
deletes the seed call entirely and ships an empty-state Home screen.

**Consequences.** A Phase 0 reviewer always sees the same 6-student cohort
("NavGurukul · Batch 7") with 4 past sessions. Deterministic seed (`Random(42)`)
keeps stats stable across launches.
