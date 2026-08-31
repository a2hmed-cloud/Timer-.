# StudyFlow — Student Productivity & Planner App

StudyFlow is a modern native Android application built with Kotlin and Jetpack Compose. It features a personalized student onboarding experience, subject management, Pomodoro focus sessions, daily scheduling, recurring reminders, and weekly analytics.

---

## Download

Get the latest version of StudyFlow directly from **GitHub Releases**:
- Go to [GitHub Releases](../../releases)
- Download `app-release.apk`
- Install on your Android device (Android 7.0+ / API 24+)

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

### Build Release APK
```bash
./gradlew assembleRelease
```
Output location: `app/build/outputs/apk/release/app-release.apk`

---

## Automated GitHub Release Workflow

StudyFlow uses GitHub Actions to automatically build, sign, and publish a Release APK whenever a version tag is pushed or triggered manually.

### One-Time Setup: Keystore & GitHub Secrets

To sign your release APKs securely, follow these steps **once**:

1. **Generate Keystore Locally**:
   ```bash
   keytool -genkey -v -keystore release.keystore -alias upload -keyalg RSA -keysize 2048 -validity 10000
   ```
   *(Keep your passwords and `release.keystore` file safe. Do NOT commit it to Git!)*

2. **Encode Keystore to Base64**:
   - **Linux / macOS**:
     ```bash
     base64 -w 0 release.keystore > keystore_base64.txt
     ```
   - **Windows PowerShell**:
     ```powershell
     [Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Out-File -Encoding ascii keystore_base64.txt
     ```

3. **Add Secrets to GitHub Repository**:
   Go to your repository on GitHub: **Settings > Secrets and variables > Actions > New repository secret** and add:

   | Secret Name | Description |
   |---|---|
   | `KEYSTORE_BASE64` | The entire content of `keystore_base64.txt` |
   | `KEYSTORE_PASSWORD` | Password created for the keystore |
   | `KEY_ALIAS` | Key alias (e.g., `upload`) |
   | `KEY_PASSWORD` | Password for the key alias |

---

## Publishing a Release

### Automated Release via Git Tag

When you are ready to publish a new release:

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions will automatically:
1. Checkout the project and set up Java 17 & Gradle.
2. Run unit tests (`./gradlew test`).
3. Decode your keystore from `KEYSTORE_BASE64`.
4. Build the signed Release APK (`./gradlew assembleRelease`).
5. Verify the generated APK file size and integrity.
6. Create a new GitHub Release with release notes and attach `app-release.apk`.

### Manual Trigger

You can also trigger a release manually at any time:
1. Go to **Actions** tab in your GitHub repository.
2. Select **Android Release** workflow.
3. Click **Run workflow**, specify the desired version name (e.g. `1.0.0`), and launch.
