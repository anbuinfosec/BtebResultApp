package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubReleaseResponse(
    @param:Json(name = "tag_name") val tagName: String? = null,
    @param:Json(name = "name") val name: String? = null,
    @param:Json(name = "body") val body: String? = null,
    @param:Json(name = "html_url") val htmlUrl: String? = null,
    @param:Json(name = "published_at") val publishedAt: String? = null,
    @param:Json(name = "assets") val assets: List<GithubAsset>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class GithubAsset(
    @param:Json(name = "name") val name: String? = null,
    @param:Json(name = "size") val size: Long? = 0L,
    @param:Json(name = "browser_download_url") val downloadUrl: String? = null
)

data class UpdateInfo(
    val latestVersion: String,
    val currentVersion: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val isUpdateAvailable: Boolean
)
