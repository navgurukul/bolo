# Lexical Analysis Pipeline V2

This document describes the improved on-device English classification and transcript analysis pipeline.

## 1. Classification Pipeline
The pipeline is deterministic and follows a layered approach for every token:

1.  **Noise/Empty**: Tokens containing no letters or numbers are ignored.
2.  **Numbers**: Numeric tokens are counted as English.
3.  **Fillers**: Common fillers (um, uh, hmm, aaa) are counted as "Filler" and excluded from the "Meaningful Tokens" denominator.
4.  **Allowed Short Words**: Words like "I", "a", "is", "to" are explicitly allowed as English.
5.  **Dictionary Match**: Exact match against a curated English word list (~2000+ words).
6.  **Stem Match**: Lightweight stemming (stripping -ing, -ed, -s, -ly, -tion) to match root words in the dictionary.
7.  **Proper Noun Heuristic**: Capitalized words not found in the dictionary are marked as Proper Nouns (counted as meaningful but NOT English).
8.  **Unknown**: Fallback for words that do not match any of the above.

## 2. Score Calculation
The English Usage score is calculated using the following formula:

**Score = (English Words / Meaningful Tokens) × 100**

*   **English Words**: Tokens matching Dictionary, Stem, Number, or Allowed Short rules.
*   **Meaningful Tokens**: Total tokens minus Fillers and Noise.
*   **Rounding**: Results are rounded to the nearest integer for display.

## 3. Dictionary Source
The dictionary is stored in `assets/dictionaries/english_words.txt`. It contains common English words, educational vocabulary, and basic technical terms. 

## 4. Debugging & Transparency
The **Transcript Analysis (Debug)** section in the Session Summary provides:
*   **Classification Reason**: Why "practicing" was counted (e.g., "Stem match").
*   **Unknown Word Tracking**: A list of words that were classified as non-English, helping identify gaps in the dictionary.
*   **Calculation Summary**: Shows the raw counts used in the formula to verify mathematical correctness.

## 5. Performance
*   **Lookup**: O(1) using a `HashSet`.
*   **Memory**: Dictionary is loaded once at app startup.
*   **Threading**: Analysis is performed on-device without blocking the UI thread.
