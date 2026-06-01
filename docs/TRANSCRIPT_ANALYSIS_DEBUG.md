# Transcript Analysis & Debugging

This document explains how Bolo captures, cleans, and analyzes speech to calculate the English usage percentage.

## 1. Capture Process
- **Mechanism**: Uses Android's `SpeechRecognizer` API.
- **Chunking**: The recognizer emits "chunks" of text whenever it detects a pause or completion of a phrase.
- **Persistence**: Every chunk is saved to the `transcript_chunks` table with its raw text, sequence number, and calculated metrics.

## 2. Cleaning Pipeline
The `EnglishAnalyzer.analyzeChunk` method performs the following:
- **Case Normalization**: Converts all text to lowercase.
- **Noise Removal**: Removes all non-alphabetic characters (numbers, punctuation, symbols).
- **Trimming**: Removes leading/trailing whitespace.

## 3. Tokenization
- **Splitting**: The raw text is split into tokens using whitespace.
- **Token Classification**:
    - **Meaningful Tokens**: Words that are neither "fillers" nor "ignored".
    - **Filler Tokens**: Common fillers like "um", "uh", "ah", "like", "you know".
    - **Ignored Tokens**: Single characters or very short noise that doesn't count as language usage.

## 4. English Classification
We use a lightweight, multi-step heuristic:
1. **Dictionary Match**: Check against a static set of ~200 most common English words.
2. **Suffix Match**: Technical or formal English words often end in "ing", "tion", "able", "ment", "ness", "ity", "ized", "ally".
3. **Length Heuristic**: Words with 5 or more characters that aren't in our "filler" list are treated as English (optimized for technical/academic vocabulary used by SOSC students).

## 5. Calculation Formula
The final score is calculated at the session level to ensure accuracy:

```
English Percentage = (Total English Words / Total Meaningful Tokens) × 100
```

- **English Words**: Sum of all tokens classified as English across all chunks.
- **Meaningful Tokens**: Sum of all tokens that aren't fillers or single-character noise.

## 6. Data Persistence
- **Session Table**: Stores the final summary (total English ms, total speech ms, full raw transcript).
- **Transcript Chunks Table**: Stores granular data for every speech event:
    - Raw & Cleaned text
    - English word count
    - Meaningful token count
    - Filler count
    - Sequence order

## 7. Known Limitations & Edge Cases
- **False Positives**: Long non-English words (5+ chars) might be flagged as English if they don't match common filler patterns.
- **False Negatives**: Short English words not in the top-200 dictionary (e.g., "map", "box") might be missed unless they have a common suffix.
- **Punctuation**: The `SpeechRecognizer` may or may not return punctuation depending on the device engine; our cleaner removes it anyway for consistency.
- **Multi-word Fillers**: "You know" is currently handled as individual tokens ("you", "know") which are both in the English dictionary, potentially inflating the score slightly if not explicitly filtered as a phrase.

## 8. Debugging Tips
If a session's percentage looks wrong:
1. Open the **Session Summary**.
2. Expand **Transcript Analysis (Debug)**.
3. Compare the **Raw Text** vs **Cleaned Text**.
4. Check the **Token breakdown** to see if specific local language words are being misclassified as English.
5. Review the **Classification Samples** at the bottom of the analysis section.
