# StudyFlow — Student Productivity & Planner App

StudyFlow is a modern native Android application built with Kotlin and Jetpack Compose. It features a personalized student onboarding experience, subject management, Pomodoro focus sessions, daily scheduling, recurring reminders, and weekly analytics.

---

## Local Build & Test

### Run Unit Tests
```bash
./gradlew test
```

### Build Debug APK
```bash
./gradlew assembleDebug
```
Output location: `app/build/outputs/apk/debug/app-debug.apk`

---

## Automated GitHub Actions Build

StudyFlow uses GitHub Actions to automatically run tests and build an unsigned Debug APK artifact on every push to `main` or via manual trigger.

- **Workflow File**: `.github/workflows/android-apk.yml`
- **Output Artifact Name**: `StudyFlow-debug-apk`
- **GitHub Secrets Required**: None

