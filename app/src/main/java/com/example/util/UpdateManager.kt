package com.example.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.BuildConfig
import com.example.data.model.GithubReleaseResponse
import com.example.data.model.UpdateInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class UpdateManager {

    companion object {
        val CURRENT_VERSION: String = BuildConfig.VERSION_NAME
        const val GITHUB_REPO_URL = "https://github.com/anbuinfosec/BtebResultApp"
        private const val GITHUB_LATEST_RELEASE_URL = "https://api.github.com/repos/anbuinfosec/BtebResultApp/releases/latest"
        const val GITHUB_RELEASES_WEB_URL = "https://github.com/anbuinfosec/BtebResultApp/releases"
    }

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(GithubReleaseResponse::class.java)

    suspend fun checkForUpdates(): Result<UpdateInfo> = withContext(Dispatchers.IO) {
        try {
            val url = URL(GITHUB_LATEST_RELEASE_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "BTEB-Android-App")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                connectTimeout = 8000
                readTimeout = 8000
            }

            if (connection.responseCode in 200..299) {
                val json = connection.inputStream.bufferedReader().use { it.readText() }
                val release = adapter.fromJson(json)

                if (release != null) {
                    val rawTag = release.tagName?.removePrefix("v")?.trim() ?: CURRENT_VERSION
                    val hasUpdate = isVersionGreater(rawTag, CURRENT_VERSION)
                    val apkAsset = release.assets?.firstOrNull { it.name?.endsWith(".apk", ignoreCase = true) == true }
                    val downloadUrl = apkAsset?.downloadUrl ?: release.htmlUrl ?: GITHUB_RELEASES_WEB_URL

                    val info = UpdateInfo(
                        latestVersion = rawTag,
                        currentVersion = CURRENT_VERSION,
                        releaseTitle = release.name ?: "Release v$rawTag",
                        releaseNotes = release.body ?: "Bug fixes, performance improvements, and enhanced curriculum data.",
                        downloadUrl = downloadUrl,
                        isUpdateAvailable = hasUpdate
                    )
                    return@withContext Result.success(info)
                }
            }

            // Fallback: If repo is private or release not published yet
            val fallback = UpdateInfo(
                latestVersion = CURRENT_VERSION,
                currentVersion = CURRENT_VERSION,
                releaseTitle = "Latest Version",
                releaseNotes = "You are already using the latest official version.",
                downloadUrl = GITHUB_RELEASES_WEB_URL,
                isUpdateAvailable = false
            )
            Result.success(fallback)
        } catch (e: Exception) {
            val fallback = UpdateInfo(
                latestVersion = CURRENT_VERSION,
                currentVersion = CURRENT_VERSION,
                releaseTitle = "Latest Version",
                releaseNotes = "You are already using the latest official version.",
                downloadUrl = GITHUB_RELEASES_WEB_URL,
                isUpdateAvailable = false
            )
            Result.success(fallback)
        }
    }

    fun startApkDownload(context: Context, updateInfo: UpdateInfo): Boolean {
        return try {
            val downloadUrl = updateInfo.downloadUrl
            if (downloadUrl.endsWith(".apk", ignoreCase = true)) {
                val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                    setTitle("BTEB Result App v${updateInfo.latestVersion}")
                    setDescription("Downloading update from GitHub Releases...")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "bteb-result-v${updateInfo.latestVersion}.apk"
                    )
                    setMimeType("application/vnd.android.package-archive")
                }
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                dm?.enqueue(request)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun isVersionGreater(remote: String, local: String): Boolean {
        return try {
            val rParts = remote.split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            val lParts = local.split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            val length = maxOf(rParts.size, lParts.size)

            for (i in 0 until length) {
                val r = rParts.getOrElse(i) { 0 }
                val l = lParts.getOrElse(i) { 0 }
                if (r > l) return true
                if (r < l) return false
            }
            false
        } catch (_: Exception) {
            false
        }
    }
}
