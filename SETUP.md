# 🛠️ Development & Deployment Setup Guide

This guide walks you through setting up the development environment, compiling the project locally, configuring release signing keys, and running the automated GitHub Actions CI/CD workflow.

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed on your machine:

1. **Java Development Kit (JDK)**: JDK 17 or higher (Temurin, OpenJDK, or Android Studio bundled JDK).
   ```bash
   java -version
   ```
2. **Android Studio**: Android Studio Iguana, Jellyfish, Koala, Ladybug (or newer) with Android SDK 34 / 36 installed.
3. **Git**: Version 2.30+ installed.

---

## 🚀 1. Clone the Repository

```bash
git clone https://github.com/anbuinfosec/BtebResultApp.git
cd BtebResultApp
```

---

## ⚙️ 2. Environment Configuration

Copy the sample `.env.example` file to `.env`:

```bash
cp .env.example .env
```

Contents of `.env`:
```env
# Base API URL
BASE_URL=https://bteb.anbuinfosec.dev/
```

> 🔒 **Security Notice**: `.env` is ignored by `.gitignore` and should never be committed into source control.

---

## 🏗️ 3. Build & Test Commands

Open a terminal inside the project root:

### Check Project & Dependencies
```bash
./gradlew tasks
```

### Run Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Build Debug APK
Outputs to `app/build/outputs/apk/debug/app-debug.apk`:
```bash
./gradlew assembleDebug
```

### Build Release APK & App Bundle
Outputs to `app/build/outputs/apk/release/` and `app/build/outputs/bundle/release/`:
```bash
./gradlew assembleRelease bundleRelease
```

---

## 🔑 3. Generating a Production Signing Key

To generate a custom release keystore for signing your APK:

```bash
keytool -genkey -v -keystore my-upload-key.jks \
  -alias upload \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

When prompted, choose strong passwords for the keystore and the key alias.

### Convert Keystore to Base64 (for GitHub Secrets)
On Linux / macOS:
```bash
base64 -w 0 my-upload-key.jks > keystore_base64.txt
```
On Windows PowerShell:
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("my-upload-key.jks")) | Out-File -Encoding ASCII keystore_base64.txt
```

---

## 🔐 4. GitHub Actions CI/CD & Secrets Setup

The repository includes an automated workflow at `.github/workflows/android-build-release.yml` that automatically:
1. Runs JVM unit tests.
2. Builds Debug & Release APKs, and Google Play Bundle (AAB).
3. Signs release binaries using your private key.
4. Generates SHA-256 security checksums (`SHA256SUMS.txt`).
5. Publishes a new GitHub Release when a git tag is pushed.

### Required GitHub Secrets

Go to your repository on GitHub:
**Settings** ➔ **Secrets and variables** ➔ **Actions** ➔ **New repository secret**

Add the following secrets:

| Secret Name | Description | Example Value |
|---|---|---|
| `SIGNING_KEY` | Base64-encoded string of your `.jks` file | Content of `keystore_base64.txt` |
| `STORE_PASSWORD` | Password of your keystore | `your_keystore_password` |
| `KEY_ALIAS` | Alias name you specified during key generation | `upload` |
| `KEY_PASSWORD` | Password for the key alias | `your_alias_password` |

> ℹ️ **Note**: If `SIGNING_KEY` is not configured, the CI workflow will automatically generate a temporary signing key to ensure builds never fail unexpectedly.

---

## 🏷️ 5. Publishing a New Release

To trigger an automated release build with APK and bundle attached:

```bash
# 1. Update version in app/build.gradle.kts (versionCode & versionName)
# 2. Commit your changes
git commit -am "chore: release v1.0.1"

# 3. Create a Git tag starting with 'v'
git tag v1.0.1

# 4. Push tag to GitHub
git push origin v1.0.1
```

GitHub Actions will automatically run the build, sign the binaries, compute SHA-256 checksums, and publish the release to:
`https://github.com/anbuinfosec/BtebResultApp/releases`

---

## 🛡️ 6. ProGuard & Security Hardening

Release builds use R8 / ProGuard optimization configured in `app/proguard-rules.pro`:
- Debug log statements (`Log.d`, `Log.v`, `Log.i`) are automatically stripped.
- Internal packages and class names are obfuscated (`com.example.bteb.obf`).
- API models and Room entities are strictly preserved to maintain runtime stability.
- Keystores are securely deleted from CI runner disks immediately after signing.

---

## 💡 Support & Issues

For help or reporting issues, open a GitHub Issue at:
[https://github.com/anbuinfosec/BtebResultApp/issues](https://github.com/anbuinfosec/BtebResultApp/issues)
or email [anbuinfosec@gmail.com](mailto:anbuinfosec@gmail.com).
