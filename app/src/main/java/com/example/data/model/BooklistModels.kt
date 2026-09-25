package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TechnologyBooklist(
    @Json(name = "name") val name: String,
    @Json(name = "semesters") val semesters: List<SemesterBooklist>? = emptyList()
) {
    val cleanName: String
        get() {
            return name
                .replace("– Course Structure & Booklist", "")
                .replace("- Course Structure & Booklist", "")
                .trim()
        }
}

@JsonClass(generateAdapter = true)
data class SemesterBooklist(
    @Json(name = "name") val name: String,
    @Json(name = "summary") val summary: String? = null,
    @Json(name = "subjects") val subjects: List<SubjectBooklistItem>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class SubjectBooklistItem(
    @Json(name = "code") val rawCode: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "credit") val rawCredit: Any? = null,
    @Json(name = "theory") val rawTheory: Any? = null,
    @Json(name = "pract") val rawPract: Any? = null,
    @Json(name = "marks") val rawMarks: Any? = null
) {
    val codeString: String
        get() = rawCode?.toString()?.trim() ?: ""

    val cleanName: String
        get() = name?.trim() ?: ""

    val creditString: String
        get() = rawCredit?.toString()?.trim() ?: ""

    val theoryString: String
        get() = rawTheory?.toString()?.trim() ?: ""

    val practicalString: String
        get() = rawPract?.toString()?.trim() ?: ""

    val marksString: String
        get() = rawMarks?.toString()?.trim() ?: ""
}
