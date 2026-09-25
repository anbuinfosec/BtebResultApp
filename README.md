# 🎓 BTEB Result & Academic Portal

<p align="center">
  <img src="art/logo.png" alt="BTEB Result App Logo" width="130" height="130" />
</p>

<p align="center">
  <strong>Fast, Modern & Complete Academic Portal for Bangladesh Technical Education Board (BTEB) Polytechnic Students.</strong>
</p>

<p align="center">
  <a href="https://github.com/anbuinfosec/BtebResultApp/releases">
    <img src="https://img.shields.io/github/downloads/anbuinfosec/BtebResultApp/total?style=for-the-badge&logo=github&color=4F46E5" alt="Total Downloads" />
  </a>
  <a href="https://github.com/anbuinfosec/BtebResultApp/releases/latest">
    <img src="https://img.shields.io/github/v/release/anbuinfosec/BtebResultApp?style=for-the-badge&color=059669" alt="Latest Version" />
  </a>
  <a href="https://github.com/anbuinfosec/BtebResultApp/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/anbuinfosec/BtebResultApp/android-build-release.yml?style=for-the-badge&logo=githubactions" alt="Build Status" />
  </a>
  <a href="https://github.com/anbuinfosec/BtebResultApp">
    <img src="https://img.shields.io/badge/Android-7.0%2B%20(API%2024%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android Version" />
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge" alt="License: MIT" />
  </a>
</p>

---

## 🚀 Key Highlights & Capabilities

- 🎯 **Individual Result & GPA Analysis**: Query 6-digit board rolls across all curriculums (*Diploma in Engineering, Textile, Agriculture, etc.*) with full semester breakdown (`Semester 1` to `Semester 8`).
- 👥 **Batch / Group Class Search**: Fetch an entire class roster or roll range (up to 100 students at once) with aggregated statistics (*Total, Passed, Referred, Pass %*).
- 🏛️ **Institute Results with Multi-Filter**: Query entire polytechnic results filtered dynamically by **Exam Date**, **Semester**, **Regulation**, **Status** (*Passed / Referred*), and **Grade / GPA range**.
- 📚 **Offline Booklists & Syllabus**: Complete regulation coverage for both **2022** and **2016** curriculums, with subject credits, theory & practical contact hours, and full mark distributions.
- 📅 **Smart Exam Routine Explorer**: Real-time examination schedule lookup with intelligent filtering that automatically highlights upcoming dates and suppresses concluded exams.
- 🧮 **BTEB Weighted CGPA Calculator**: Calculates official cumulative GPA using the board's regulation-weighted formula across all 8 semesters.
- 🏛️ **Polytechnic Directory & Leaderboard**: Comprehensive database of all 800+ polytechnic institutes in Bangladesh with performance metrics.
- 🔄 **In-App GitHub Update System**: Automatically checks GitHub Releases on startup, offers background download via Android's `DownloadManager` with notification progress, and one-tap install.
- 🔖 **Offline Bookmarks (Room Database)**: Save your roll number for instant, 1-tap cached access even without internet connectivity.
- ☕ **Support Developer & Bangla QR**: Integrated donation modal with web payment widget and standard Bangla QR code for all Bangladeshi bank and MFS apps (*bKash, Nagad, Rocket, Cellfin, Upay, etc.*).
- 🛡️ **Cryptographic Verification**: Automated SHA-256 integrity verification and GitHub Actions release signing workflow.

---

## 📥 Download Official APK

Get the latest signed APK directly from GitHub Releases:

👉 **[Download Latest APK Release](https://github.com/anbuinfosec/BtebResultApp/releases/latest)**

### Verification (SHA-256)
Every release includes a `SHA256SUMS.txt` artifact. To verify the integrity of your downloaded APK on Linux/macOS:
```bash
sha256sum -c SHA256SUMS.txt
```
Or on Windows PowerShell:
```powershell
Get-FileHash app-release.apk -Algorithm SHA256
```

---

## 🛠️ Architecture & Tech Stack

| Component | Technology |
|---|---|
| **Language** | Kotlin 2.2+ (100%) |
| **UI Framework** | Jetpack Compose with Material 3 Design |
| **Build Tooling** | Gradle 9.8.0 Wrapper + Android Gradle Plugin 9.1.1 |
| **Architecture** | MVVM (Model-View-ViewModel) + Clean Architecture |
| **Networking** | Retrofit 2.12 + OkHttp 4.12 + Moshi JSON Serialization |
| **Persistence** | Room 2.7 SQLite Database (Coroutines Flow & KSP) |
| **Asynchronous** | Kotlin Coroutines & `StateFlow` / `collectAsStateWithLifecycle` |
| **QR Code Engine** | ZXing Core 3.5.3 |
| **In-App Updates** | GitHub Releases API + Android `DownloadManager` |
| **CI/CD** | GitHub Actions Automated Build, Test, Sign & Release |
| **Code Shrinking** | ProGuard / R8 Obfuscation & Security Rules |

---

## ⚙️ Environment Configuration

The application uses the Secrets Gradle Plugin to inject configuration safely from a `.env` file:

```bash
# Clone repository
git clone https://github.com/anbuinfosec/BtebResultApp.git
cd BtebResultApp

# Copy example environment configuration
cp .env.example .env
```

Contents of `.env`:
```env
# Base API URL (Trailing slash required)
BASE_URL=https://bteb.anbuinfosec.dev/
```

---

## 🏗️ Building from Source

```bash
# Check dependencies and Gradle 9.8.0 wrapper
./gradlew tasks

# Run unit tests
./gradlew testDebugUnitTest

# Build debug APK (outputs to app/build/outputs/apk/debug/app-debug.apk)
./gradlew assembleDebug

# Build release APK & Google Play App Bundle
./gradlew assembleRelease bundleRelease
```

Detailed instructions for signing configurations and CI/CD secret management are available in **[SETUP.md](SETUP.md)**.

---

## 🤝 Contributing

Contributions, feature requests, and bug reports are welcome!
1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Developer & Contact

- **Developer**: Mohammad Alamin ([@anbuinfosec](https://github.com/anbuinfosec))
- **Organization**: AnbuSoft
- **Repository**: [github.com/anbuinfosec/BtebResultApp](https://github.com/anbuinfosec/BtebResultApp)
- **Email**: [anbuinfosec@gmail.com](mailto:anbuinfosec@gmail.com)
- **API Portal**: [https://bteb.anbuinfosec.dev](https://bteb.anbuinfosec.dev)
- **Support & Donations**: [https://donate.anbuinfosec.dev](https://donate.anbuinfosec.dev)
