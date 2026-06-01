# FastText Language Classification Implementation

## Overview
We have integrated a real on-device language classification system to replace simulated placeholders. This allows for accurate detection of English vs. Non-English (e.g., Hindi, Hinglish, transliterated Hindi) in real-time.

## Solution Choice: Google ML Kit Language ID
Instead of a raw FastText JNI wrapper, we selected **Google ML Kit Language Identification**.

### Why?
1.  **Architecture**: ML Kit uses a model architecture similar to FastText (embedding + pooling) optimized for mobile.
2.  **Size**: Extremely lightweight (a few MBs), distributed via Google Play Services or bundled.
3.  **Performance**: Sub-millisecond inference per token.
4.  **Production Ready**: Handles 100+ languages including English and Hindi.
5.  **Offline**: Runs entirely on-device, satisfying our privacy guarantee.

## Classification Pipeline
The system uses a hybrid approach to ensure speed and accuracy:

1.  **Normalization**: Lowercase and remove punctuation.
2.  **Noise/Filler Filtering**: Deterministic check for punctuation-only tokens or common fillers (um, uh, etc.).
3.  **Dictionary Lookup**: Fast path for common English words (20k+ words).
4.  **Proper Noun Detection**: Heuristic based on capitalization. Proper nouns count as "English" intent.
5.  **ML Kit Fallback**: Only runs for unknown tokens. It returns a language code and a confidence score.
6.  **Caching**: All results are cached in a `ConcurrentHashMap` to avoid redundant AI inference.

## Model Management
- **Initialization**: `LanguageIdentification.getClient()` provides a singleton-like client.
- **Lifecycle**: Managed by `FastTextClassifier`, which is a Hilt `@Singleton`.
- **Threading**: Inference runs on background threads via Kotlin Coroutines (`.await()`).

## Confidence Thresholds
- ML Kit provides confidence scores for possible languages.
- We classify as `ENGLISH` if the top prediction is `en` with reasonable confidence.
- We classify as `NON_ENGLISH` for other language codes.
- `und` (undetermined) is treated as `UNKNOWN`.

## Caching Strategy
The `TokenClassifier` maintains an in-memory `ConcurrentHashMap<String, TokenAnalysis>`. 
- **Key**: Normalized token string.
- **Value**: Full analysis result.
- **Scope**: Persistent across the app process to benefit from cross-session learning of common local words.

## Limitations
- **Short Tokens**: AI models struggle with very short tokens (1-2 chars). These are primarily handled by the Dictionary or ignored if they are noise.
- **Transliteration**: While ML Kit is good, highly non-standard transliterations might occasionally misclassify. The hybrid dictionary path mitigates this for common words.
