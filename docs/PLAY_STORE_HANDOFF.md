# Google Play Store Deployment Handoff Package

**App Name:** Bolo  
**Package Name:** `co.bolo.app`  
**Version Code:** `10`  
**Version Name:** `0.1.9`  
**Date:** August 27, 2026  

---

## 1. App Bundle File Location
The compiled, production-signed Android App Bundle file is ready at:
`app/build/outputs/bundle/release/app-release.aab`

---

## 2. Release Signing Information
The release App Bundle has been signed with a production keystore (`bolo-release.jks`) using the settings defined in `keystore.properties` (gitignored for security).

---

## 3. Play Store Listing Information

### App Basic Details
* **App Name:** Bolo — Group English Practice
* **Short Description:** Track English speaking practice for shared-phone learning groups.
* **Full Description:** Bolo is designed for group-based English practice. A facilitator places one phone in the middle of a 5–10 student group. The app tracks speaking time, measures English usage percentage, and displays individual progress dashboards without sending audio off the device.
* **Category:** Education
* **Content Rating:** Everyone / 13+

### Release Notes (What's New in v0.1.9)
```text
- Updated release build (v0.1.9 / versionCode 10).
- Fully compliant with Google Play Store policies.
- Integrated official Google Play In-App Updates SDK for smooth background updates.
- Enhanced cohort management and session tracking stability.
```

---

## 4. Mandatory Play Console Survey Answers

### Privacy Policy
* **URL:** Link to `PRIVACY_POLICY.md` (or host the document online).

### Data Safety Questionnaire
* **Does your app collect or share user data?** No.
* **Audio Data:** Bolo processes microphone audio **100% locally on-device**. No voice recordings or audio files leave the phone or are stored on external servers.

### Financial / Government Declarations
* **Financial Features:** No
* **Government App:** No
