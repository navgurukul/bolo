# Engineering Audit: Project Bolo (Phase 0)

**Date:** October 26, 2023 (Audit of current repository state)  
**Status:** EARLY PROTOTYPE / STUB STATE  
**Auditor:** AI Technical Auditor  

---

## 1. Project Overview
- **Purpose:** Bolo is a group-based English speaking practice application designed for a "single shared phone" use case. It aims to track speaking time and English language usage percentages for multiple students in a live session.
- **Main Modules/Features:** Cohort management, Student enrollment, Live Session tracking (simulated), Session Summaries, and Student Progress Dashboards.
- **Current Maturity:** **Phase 0 (Skeleton).** The app is a functional "walkthrough" using seeded mock data. The core value proposition—real-time audio analysis—is currently entirely simulated.

---

## 2. Tech Stack Audit

### Languages
- **Kotlin:** 100% (JVM Target 17, Kotlin 2.1.0).
- **Java/Rust/C++:** None detected. No JNI/Native dependencies currently present.

### Frameworks/Libraries
- **UI:** Jetpack Compose (Material 3).
- **DI:** Hilt (2.55).
- **Async:** Coroutines + Flow (StateFlow for UI).
- **Navigation:** Navigation Compose.
- **Persistence:** Room (2.6.1).
- **Security:** `androidx.security.crypto` (Dependency included, but no implementation of encrypted embeddings yet).
- **Media/AI:** **NONE.** Missing `AudioRecord` implementation, Whisper, Vosk, or TFLite integrations.

### Build System
- **Gradle:** Kotlin DSL with Version Catalogs (`libs.versions.toml`).
- **Structure:** Single module (`:app`).
- **Target SDK:** 35.

### Architecture Pattern
- **MVVM:** Clear separation between Views (Compose), ViewModels (Hilt), and Repositories.
- **Clean Architecture Lite:** Uses Repository pattern. Use cases/Interactors are not present (business logic resides in ViewModels/Repos).
- **Data Flow:** Unidirectional data flow (UDF) using `StateFlow` and `collectAsStateWithLifecycle`.

---

## 3. AI / NLP / ML Audit

### Existing AI/NLP Implementations
- **Status:** **Placeholder only.**
- **Evidence:** `SessionViewModel.kt` contains a `tick()` function that uses `Random.nextFloat()` to simulate speaker probability and English share percentages.
- **Models:** No model files (.tflite, .onnx, etc.) are present in the project.

### Speech Recognition / Language Detection
- **Implementation Status:** **Non-functional.**
- **Details:** There is no code interfacing with microphone buffers or performing STT/VAD.
- **Hinglish/Language Logic:** Only exists as a mock probability variable (`englishShareRolling`).

### Audio Processing
- **Implementation Status:** **Mocked.**
- **Code Reference:** `EnrollmentViewModel` and `SessionViewModel` use `delay()` timers to simulate "listening."
- **Threading:** Currently using `viewModelScope`. No dedicated high-priority audio threads or Foreground Services for microphone handling.

---

## 4. Repositories & Data Layer Audit
- **Room Database:** Fully functional. Entities for `Cohort`, `Student`, `Session`, and `SpeakerStat` are well-defined.
- **Persistence:** Real data is saved to Room at the end of a session (`SessionRepo.upsertSession`).
- **Fake Data:** `Seed.kt` populates the database on first launch with believable but synthetic history.
- **Production Readiness:** The schema is solid, but `voiceEmbedding` in the `Student` table is currently a nullable `ByteArray` with no encryption logic yet.

---

## 5. Session Architecture Audit
- **Session Creation:** Functional (Topic selection -> Participant setup).
- **Student Grouping:** Supported via `Cohort` and `Student` entities.
- **Real-time Communication:** **N/A.** The app is designed for local processing on one device. No network/socket layer exists.
- **Multi-user Support:** Handled via local speaker identification (planned for Phase 2). Currently, speaker switching is randomized.

---

## 6. App Flow Audit
- **Functional Flows:**
    - Home -> Setup -> Session Simulation -> Summary.
    - Home -> Student Dashboard (with trend charts).
- **Mock Flows:**
    - **Enrollment:** 10-second "listening" screen is a UI-only timer.
- **Placeholder Screens:**
    - `EnrollmentScreen`: No actual biometric data captured.
    - `SessionScreen`: Metrics are synthetic.

---

## 7. Background Processing Audit
- **WorkManager:** Not used.
- **Foreground Services:** **MISSING.** This is a critical risk. For a session-based app that records audio, a Foreground Service is required to prevent the OS from killing the process when the screen turns off or the app is minimized.
- **Coroutine Scopes:** Currently bound to ViewModels. Audio processing will need a more robust lifecycle (Service or Application scope).

---

## 8. Performance & Resource Usage Audit
- **Battery/Memory:** No current risks as there is no heavy processing.
- **AI Risks:** Once TFLite/Whisper is added, RAM usage will spike.
- **ANR Risks:** Current architecture uses `Dispatchers.IO` for DB, which is correct.

---

## 9. Missing Critical Components
1. **Audio Pipeline:** `AudioRecord` integration and raw PCM buffer management.
2. **VAD (Voice Activity Detection):** Needed to trigger classification only when someone is speaking.
3. **Speaker Identification (SID):** The logic to map audio segments to specific enrolled students.
4. **Language Classifier:** A binary or multi-class model to distinguish English from other languages.
5. **Foreground Service:** To host the audio engine.
6. **Encrypted Persistence:** `androidx.security.crypto` integration for biometric embeddings.
7. **Permission Handling:** Runtime `RECORD_AUDIO` request logic (declared in manifest but not requested in UI).

---

## 10. Suggested Architecture Improvements
- **Audio Engine Module:** Move all audio/AI logic to a dedicated module or a Foreground Service with a clear interface (e.g., `Flow<SessionUpdate>`).
- **Offline-First:** Current architecture is offline-first, which is excellent.
- **State Management:** Consider a `SessionManager` singleton or service to hold session state, rather than keeping it entirely in a `ViewModel`, to survive configuration changes and backgrounding.
- **Worker for Sync:** If Phase 5 (Firestore sync) is implemented, use `WorkManager` for reliable background uploads.

---

## 11. Folder Structure Analysis
- **`ui/`**: Feature-based, well-organized.
- **`data/`**: Standard repository pattern.
- **`audio/`**: **MISSING.** Should be created to house the Phase 1+ engine.
- **`assets/`**: **MISSING.** Will be needed for ML model files.

---

## 12. Functional vs Prototype Matrix

| Feature | Status | Evidence | Notes |
|---|---|---|---|
| **Database/Persistence** | Functional | `BoloDatabase`, DAOs, Repos | Room is fully wired. |
| **Navigation** | Functional | `BoloNavHost` | All prototype screens are reachable. |
| **Session Tracking** | Prototype | `SessionViewModel.tick()` | Uses `Random` for metrics. |
| **Enrollment** | Prototype | `EnrollmentViewModel` | UI timer only; no audio capture. |
| **Dashboard/Charts** | Partial | `DashboardViewModel` | Renders real data from Room/Seed. |
| **Audio Capture** | **Broken/Missing** | `AndroidManifest` | Permission exists; no implementation. |
| **AI Inference** | **Placeholder** | Docs only | No models or inference code present. |

---

## 13. Engineering Summary
The project is a high-quality **UX Prototype**. The architectural foundation (DI, Navigation, Persistence) is "production-grade" for an early stage, but the core engine of the app (Audio/AI) is entirely non-existent.

**Technical Debt Level:** Low (due to clean structure), but **Implementation Gap** is very high.

**Immediate Next Steps:**
1. Implement `AudioRecord` capture in a dedicated `AudioEngine`.
2. Integrate a lightweight VAD (e.g., Silero or WebRTC VAD).
3. Transition Session State from `ViewModel` to a `Service`-backed component.
