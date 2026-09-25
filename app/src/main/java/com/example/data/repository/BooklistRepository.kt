package com.example.data.repository

import android.content.Context
import com.example.data.api.RetrofitInstance
import com.example.data.model.SubjectBooklistItem
import com.example.data.model.TechnologyBooklist
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class BooklistRepository(private val context: Context) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, TechnologyBooklist::class.java)
    private val adapter = moshi.adapter<List<TechnologyBooklist>>(listType)

    private val _subjectMap = mutableMapOf<String, String>()
    val subjectMap: Map<String, String> get() = _subjectMap

    private var _booklists2022: List<TechnologyBooklist> = emptyList()
    val booklists2022: List<TechnologyBooklist> get() = _booklists2022

    private var _booklists2016: List<TechnologyBooklist> = emptyList()
    val booklists2016: List<TechnologyBooklist> get() = _booklists2016

    suspend fun loadBooklists() = withContext(Dispatchers.IO) {
        try {
            // Load 2022 booklist
            context.assets.open("booklists_2022.json").use { stream ->
                val reader = InputStreamReader(stream)
                val list = adapter.fromJson(reader.readText())
                if (list != null) {
                    _booklists2022 = list
                    indexSubjects(list)
                }
            }
        } catch (_: Exception) {}

        try {
            // Load 2016 booklist
            context.assets.open("booklists_2016.json").use { stream ->
                val reader = InputStreamReader(stream)
                val list = adapter.fromJson(reader.readText())
                if (list != null) {
                    _booklists2016 = list
                    indexSubjects(list)
                }
            }
        } catch (_: Exception) {}
    }

    private fun indexSubjects(techs: List<TechnologyBooklist>) {
        techs.forEach { tech ->
            tech.semesters?.forEach { sem ->
                sem.subjects?.forEach { sub ->
                    val code = sub.codeString
                    val name = sub.cleanName
                    if (code.isNotBlank() && name.isNotBlank()) {
                        // Prefer 2022 or existing non-empty
                        if (!_subjectMap.containsKey(code)) {
                            _subjectMap[code] = name
                        }
                    }
                }
            }
        }
    }

    fun getSubjectName(code: String?): String {
        if (code.isNullOrBlank()) return ""
        val cleanCode = code.trim()
        return _subjectMap[cleanCode] ?: ""
    }

    fun formatSubject(code: String?): String {
        if (code.isNullOrBlank()) return ""
        val name = getSubjectName(code)
        return if (name.isNotBlank()) {
            "$code - $name"
        } else {
            code
        }
    }
}
