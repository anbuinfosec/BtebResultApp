package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentResultResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "data") val data: List<StudentResult>? = emptyList(),
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class StudentResult(
    @Json(name = "_id") val id: String? = null,
    @Json(name = "exam") val exam: String? = null,
    @Json(name = "roll") val rawRoll: Any? = null,
    @Json(name = "regulation") val rawRegulation: Any? = null,
    @Json(name = "curriculumCode") val curriculumCode: String? = null,
    @Json(name = "institute") val institute: InstituteData? = null,
    @Json(name = "semester_results") val semesterResults: List<SemesterResult>? = emptyList(),
    @Json(name = "current_reffereds") val currentReffereds: List<ReferredSubject>? = emptyList(),
    @Json(name = "latest_result") val latestResult: String? = null,
    @Json(name = "lastUpdated") val lastUpdated: String? = null
) {
    val rollString: String
        get() = when (rawRoll) {
            is Double -> rawRoll.toLong().toString()
            is Float -> rawRoll.toLong().toString()
            null -> ""
            else -> rawRoll.toString()
        }

    val regulationString: String
        get() = when (rawRegulation) {
            is Double -> rawRegulation.toLong().toString()
            is Float -> rawRegulation.toLong().toString()
            null -> ""
            else -> rawRegulation.toString()
        }

    // Collect all active referred subjects from current_reffereds AND semester_results
    val allReferredSubjects: List<ReferredSubject>
        get() {
            val list = mutableListOf<ReferredSubject>()
            currentReffereds?.let { list.addAll(it) }

            semesterResults?.forEach { sem ->
                sem.examResults?.forEach { examRes ->
                    val refferedList = examRes.parsedReferreds
                    refferedList.forEach { ref ->
                        if (!ref.passed && list.none { it.subjectCodeString == ref.subjectCodeString }) {
                            list.add(ref)
                        }
                    }
                }
            }
            return list
        }

    val isPassed: Boolean
        get() {
            // Check if latest semester had any unpassed referreds
            val latestSem = semesterResults?.firstOrNull()
            val latestReferreds = latestSem?.examResults?.flatMap { it.parsedReferreds }?.filter { !it.passed }
            return (currentReffereds.isNullOrEmpty() && (latestReferreds.isNullOrEmpty()))
        }
}

@JsonClass(generateAdapter = true)
data class InstituteData(
    @Json(name = "code") val rawCode: Any? = null,
    @Json(name = "institute_code") val altCode: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "institute_name") val altName: String? = null,
    @Json(name = "district") val district: String? = null,
    @Json(name = "studentCount") val studentCount: Int? = null,
    @Json(name = "count") val altCount: Int? = null,
    @Json(name = "passedCount") val passedCount: Int? = null,
    @Json(name = "failedCount") val failedCount: Int? = null,
    @Json(name = "passPercentage") val rawPassPercentage: Any? = null
) {
    val displayName: String
        get() = name ?: altName ?: "Polytechnic Institute"

    val codeString: String
        get() {
            val c = rawCode ?: altCode
            return when (c) {
                is Double -> c.toLong().toString()
                is Float -> c.toLong().toString()
                null -> ""
                else -> c.toString()
            }
        }

    val totalStudentsCount: Int
        get() = studentCount ?: altCount ?: 0

    val passPercentageFormatted: String
        get() = when (rawPassPercentage) {
            is Number -> String.format("%.1f%%", rawPassPercentage.toDouble())
            is String -> "$rawPassPercentage%"
            else -> ""
        }
}

@JsonClass(generateAdapter = true)
data class SemesterResult(
    @Json(name = "semester") val rawSemester: Any? = null,
    @Json(name = "gpa") val directGpa: Any? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "exam_results") val examResults: List<ExamResultDetail>? = emptyList(),
    @Json(name = "passed_subjects") val passedSubjects: List<String>? = emptyList(),
    @Json(name = "failed_subjects") val failedSubjects: List<String>? = emptyList()
) {
    val semesterNumberClean: String
        get() = when (rawSemester) {
            is Number -> rawSemester.toInt().toString()
            is String -> {
                val s = rawSemester.trim()
                if (s.contains(".")) {
                    s.substringBefore(".").trim()
                } else {
                    s.replace("th", "", ignoreCase = true)
                        .replace("st", "", ignoreCase = true)
                        .replace("nd", "", ignoreCase = true)
                        .replace("rd", "", ignoreCase = true)
                        .trim()
                }
            }
            null -> "1"
            else -> rawSemester.toString().substringBefore(".")
        }

    val semesterDisplayName: String
        get() = "Semester $semesterNumberClean"

    val semesterString: String
        get() = semesterNumberClean

    val gpaString: String
        get() {
            val examGpa = examResults?.firstOrNull()?.rawGpa
            return when {
                examGpa != null -> when (examGpa) {
                    is Number -> String.format("%.2f", examGpa.toDouble())
                    else -> examGpa.toString()
                }
                directGpa != null -> when (directGpa) {
                    is Number -> String.format("%.2f", directGpa.toDouble())
                    else -> directGpa.toString()
                }
                hasReferreds -> "Referred"
                else -> "N/A"
            }
        }

    val hasReferreds: Boolean
        get() {
            val refs = examResults?.flatMap { it.parsedReferreds }
            return !refs.isNullOrEmpty() || !failedSubjects.isNullOrEmpty()
        }

    val referredList: List<ReferredSubject>
        get() = examResults?.flatMap { it.parsedReferreds } ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class ExamResultDetail(
    @Json(name = "date") val date: String? = null,
    @Json(name = "gpa") val rawGpa: Any? = null,
    @Json(name = "instituteCode") val rawInstituteCode: Any? = null,
    @Json(name = "roll") val rawRoll: Any? = null,
    @Json(name = "reffereds") val rawReffereds: Any? = null
) {
    @Suppress("UNCHECKED_CAST")
    val parsedReferreds: List<ReferredSubject>
        get() {
            if (rawReffereds is List<*>) {
                val result = mutableListOf<ReferredSubject>()
                for (item in rawReffereds) {
                    if (item is Map<*, *>) {
                        val map = item as Map<String, Any?>
                        result.add(
                            ReferredSubject(
                                rawSubjectCode = map["subject_code"],
                                subjectName = map["subject_name"] as? String,
                                failedType = (map["reffered_type"] ?: map["failed_type"]) as? String,
                                subjectSemester = (map["subject_semester"] as? Number)?.toInt(),
                                passed = (map["passed"] as? Boolean) ?: false
                            )
                        )
                    }
                }
                return result
            }
            return emptyList()
        }
}

@JsonClass(generateAdapter = true)
data class ReferredSubject(
    @Json(name = "subject_code") val rawSubjectCode: Any? = null,
    @Json(name = "subject_name") val subjectName: String? = null,
    @Json(name = "failed_type") val failedType: String? = null,
    @Json(name = "subject_semester") val subjectSemester: Int? = null,
    @Json(name = "passed") val passed: Boolean = false
) {
    val subjectCodeString: String
        get() = when (rawSubjectCode) {
            is Double -> rawSubjectCode.toLong().toString()
            is Float -> rawSubjectCode.toLong().toString()
            null -> ""
            else -> rawSubjectCode.toString()
        }
}

@JsonClass(generateAdapter = true)
data class GroupSearchRequest(
    @Json(name = "rolls") val rolls: String,
    @Json(name = "exam") val exam: String? = "Any"
)

@JsonClass(generateAdapter = true)
data class GroupResultResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "total") val total: Int? = 0,
    @Json(name = "found") val found: Int? = 0,
    @Json(name = "notFound") val notFound: Int? = 0,
    @Json(name = "results") val results: List<StudentResult>? = emptyList(),
    @Json(name = "missing") val missing: List<Any>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class InstituteListResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "data") val data: List<InstituteData>? = emptyList(),
    @Json(name = "count") val count: Int? = 0
)

@JsonClass(generateAdapter = true)
data class InstituteResultsResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "data") val data: List<StudentResult>? = emptyList(),
    @Json(name = "count") val count: Int? = 0,
    @Json(name = "total") val total: Int? = 0,
    @Json(name = "page") val page: Int? = 1,
    @Json(name = "totalPages") val totalPages: Int? = 1,
    @Json(name = "institute") val institute: InstituteData? = null
)

@JsonClass(generateAdapter = true)
data class RoutineDepartmentResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "departments") val departments: List<String>? = emptyList(),
    @Json(name = "semesters") val semesters: List<Any>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class RoutineResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "routine") val routineList: List<RoutineItem>? = null,
    @Json(name = "data") val dataList: List<RoutineItem>? = null
) {
    val items: List<RoutineItem>
        get() = routineList ?: dataList ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class RoutineItem(
    @Json(name = "_id") val id: String? = null,
    @Json(name = "subject_code") val rawSubjectCode: Any? = null,
    @Json(name = "subject_name") val subjectName: String? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "day") val day: String? = null,
    @Json(name = "time") val time: String? = null,
    @Json(name = "semester") val rawSemester: Any? = null,
    @Json(name = "curriculum") val curriculum: String? = null,
    @Json(name = "departments") val departments: List<String>? = emptyList(),
    @Json(name = "shifts") val shifts: List<String>? = emptyList()
) {
    val subjectCodeString: String
        get() = when (rawSubjectCode) {
            is Double -> rawSubjectCode.toLong().toString()
            is Float -> rawSubjectCode.toLong().toString()
            null -> ""
            else -> rawSubjectCode.toString()
        }

    val semesterString: String
        get() = rawSemester?.toString() ?: ""

    val isEnded: Boolean
        get() {
            if (date.isNullOrBlank()) return false
            val clean = date.trim().substringBefore("T")
            if (clean.startsWith("1970")) return true
            try {
                val today = java.time.LocalDate.now()
                val examDate: java.time.LocalDate? = when {
                    clean.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> java.time.LocalDate.parse(clean)
                    clean.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) -> {
                        val p = clean.split("-")
                        java.time.LocalDate.of(p[2].toInt(), p[1].toInt(), p[0].toInt())
                    }
                    else -> null
                }
                if (examDate != null) {
                    return examDate.isBefore(today)
                }
            } catch (_: Exception) {}
            return false
        }

    val isUpcoming: Boolean
        get() = !isEnded

    val formattedDate: String
        get() {
            if (date.isNullOrBlank()) return "Date TBA"
            return if (date.contains("T")) {
                date.substringBefore("T")
            } else {
                date
            }
        }
}

@JsonClass(generateAdapter = true)
data class CurriculumsResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "data") val data: List<String>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class PublicStatsResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "data") val data: PublicStats? = null
)

@JsonClass(generateAdapter = true)
data class PublicStats(
    // Live API fields
    @Json(name = "studentCount") val studentCount: Long? = null,
    @Json(name = "instituteCount") val instituteCount: Long? = null,
    @Json(name = "passCount") val passCount: Long? = null,
    @Json(name = "refCount") val refCount: Long? = null,
    @Json(name = "passPercentage") val rawPassPercentage: Any? = null,
    @Json(name = "refPercentage") val rawRefPercentage: Any? = null,
    @Json(name = "lastResultPublished") val lastResultPublished: String? = null,

    // Fallback/Legacy keys
    @Json(name = "totalStudents") val totalStudents: Long? = null,
    @Json(name = "totalInstitutes") val totalInstitutes: Long? = null,
    @Json(name = "passedStudents") val passedStudents: Long? = null,
    @Json(name = "referredStudents") val referredStudents: Long? = null
) {
    val displayTotalStudents: Long
        get() = studentCount ?: totalStudents ?: 523888L

    val displayTotalInstitutes: Long
        get() = instituteCount ?: totalInstitutes ?: 803L

    val displayPassedStudents: Long
        get() = passCount ?: passedStudents ?: 346134L

    val displayReferredStudents: Long
        get() = refCount ?: referredStudents ?: 310764L

    val passPercentageFormatted: String
        get() = when (rawPassPercentage) {
            is Number -> String.format("%.1f%%", rawPassPercentage.toDouble())
            is String -> "$rawPassPercentage%"
            else -> "66.1%"
        }
}

@JsonClass(generateAdapter = true)
data class ContactRequest(
    @Json(name = "name") val name: String,
    @Json(name = "subject") val subject: String,
    @Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class ContactResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "message") val message: String? = null
)
