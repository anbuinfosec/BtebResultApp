package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubDeveloperProfile(
    @Json(name = "login") val login: String = "anbuinfosec",
    @Json(name = "name") val name: String? = "Mohammad Alamin",
    @Json(name = "avatar_url") val avatarUrl: String? = "https://avatars.githubusercontent.com/u/135030867?v=4",
    @Json(name = "bio") val bio: String? = "Whispering in code's quiet hum, I craft light from shadows, building bridges where logic and dreams unite—worlds born line by line.",
    @Json(name = "html_url") val htmlUrl: String? = "https://github.com/anbuinfosec",
    @Json(name = "public_repos") val publicRepos: Int? = 111,
    @Json(name = "followers") val followers: Int? = 188,
    @Json(name = "following") val following: Int? = 1,
    @Json(name = "location") val location: String? = "Bangladesh",
    @Json(name = "blog") val blog: String? = "https://anbuinfosec.dev",
    @Json(name = "company") val company: String? = "@AnbuSoft",
    @Json(name = "twitter_username") val twitterUsername: String? = "anbuinfosec",
    @Json(name = "hireable") val hireable: Boolean? = true,
    @Json(name = "created_at") val createdAt: String? = "2023-05-30T12:59:50Z"
)
