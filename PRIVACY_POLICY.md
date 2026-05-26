# Bolo — Privacy Policy

_Last updated: 26 May 2026_

Bolo is an Android application built by Samyarth (a NavGurukul
initiative) for use in classroom sessions. This policy explains exactly
what data Bolo touches and where it goes.

## What Bolo does

During a session, Bolo listens via the device microphone, sends the
audio to Google's speech recognition service for transcription, and
analyses the resulting text to estimate how much English vs. other
languages was spoken. The transcript text and per-student English share
are stored on the device.

## What data Bolo handles

| Data | Where it lives | Who can see it |
|------|----------------|----------------|
| **Microphone audio** | Streamed to Google's speech service. **Bolo itself does not store the raw audio.** | Google Speech Services (per Google's privacy policy). |
| **Transcript text** | Local Room database on the device only. | Only the user of the device. |
| **Per-student English share + speech time** | Local Room database on the device only. | Only the user of the device. |
| **Cohort name, student names, session topics** | Entered by the facilitator. Local Room database only. | Only the user of the device. |
| **Voice fingerprints** | Not collected. Bolo does not enrol voices or perform biometric speaker identification. | — |

## What Bolo does **not** do

- Bolo does not record or save audio files.
- Bolo does not send any data to Samyarth, NavGurukul, or any server
  controlled by the developers.
- Bolo does not show ads, run analytics, or use third-party trackers.
- Bolo does not require an account or login.

## Third-party processor

Audio is sent to **Google Speech Services** (part of the Android
operating system) for real-time transcription. Google's handling of
that audio is governed by:

- [Google Privacy Policy](https://policies.google.com/privacy)
- [Speech Services data handling](https://support.google.com/websearch/answer/6030020)

The transcript text Google returns is kept by Bolo on the device; the
underlying audio is handled by Google according to their own retention
policies and the user's Google account settings.

## Network use

Bolo requires an internet connection to transcribe speech. No data is
sent over the network by Bolo itself — only the system speech
recogniser uses the network, and the connection is encrypted (HTTPS) by
the operating system.

## Permissions Bolo requests

- **Microphone** — to capture speech during sessions.
- **Internet, Network state** — used by the system speech recogniser.
- **Foreground service, Foreground service microphone, Post
  notifications** — to keep recognition running with the screen off and
  to show the persistent "Bolo Session Active" notification while a
  session is live.
- **Install packages** — Bolo can install in-app updates fetched from
  GitHub Releases when a newer build is published. The user always sees
  the system installer prompt.

## How to delete your data

All Bolo data is stored on the device. To delete it:

1. Open Bolo.
2. Go to **Settings → Privacy → Clear all data & restart setup**.
3. Confirm.

This wipes every cohort, student, session, transcript, and the consent
flag from the device. Uninstalling Bolo has the same effect.

## Children

Bolo is intended for use by adult facilitators with adult learners
(18+). Bolo is not directed to children under 13 and does not knowingly
collect data from them.

## Contact

For questions about this policy, contact
**souvikdeb2612@gmail.com**.

## Changes

If we ever change what data Bolo handles, this page will be updated
and a new "Last updated" date posted above. Significant changes will
also be flagged in the app on next launch.
