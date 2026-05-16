# MVP Status - Bolo Live Session Analyzer

## Implementation Summary
The Bolo MVP is now a functional live session analyzer. It captures real audio, transcribes it on-device, and calculates English usage metrics without any simulated data.

### Features Built
- **Real-time Transcription**: Uses Android `SpeechRecognizer` via a `ForegroundService` to ensure recording continues even if the app is backgrounded.
- **On-Device Analysis**: `EnglishAnalyzer` provides a deterministic dictionary-match algorithm to calculate English percentage over the full session.
- **Dynamic Setup**: Support for 2-10 participants with custom name entry.
- **Persistent Storage**: Real session data is saved to Room upon completion.
- **Permission Flow**: Integrated runtime requests for Audio and Notifications.

### English Percentage Logic
- **Cleaning**: Removes punctuation and non-alphabetical characters.
- **Tokenization**: Splits text into lowercase word tokens.
- **Matching**: 
  - Words are checked against a common English dictionary.
  - Suffix-based heuristics (e.g., -ing, -tion) identify technical or longer English words.
  - Length-based heuristics (5+ chars) are used to capture technical terms common in SOSC cohorts.
- **Formula**: `(English Token Count / Total Meaningful Tokens) * 100`.

### Known Limitations / Incomplete Parts
- **Speaker Diarization**: The MVP does not yet attribute specific words to specific students. Total English percentage is calculated for the group as a whole.
- **Speech Quality**: Accuracy depends on the device's default Speech-to-Text engine (usually Google Speech Services). Offline availability depends on whether the user has downloaded offline voice models.
- **Silence Handling**: The current logic relies on the STT engine's endpointing.

### Assumptions Made
- **Device Support**: Assumes the device has a functional microphone and `SpeechRecognizer` implementation.
- **Language**: Defaulted to system locale for recognition, with fallback to common English matching.
- **Foreground Service**: Necessary for continuous capture to survive Android lifecycle management.
