package com.example.data.repository

import com.example.data.api.BtebApiService
import com.example.data.local.BookmarkDao
import com.example.data.local.BookmarkEntity
import com.example.data.model.ContactRequest
import com.example.data.model.GroupSearchRequest
import kotlinx.coroutines.flow.Flow

class BtebRepository(
    private val apiService: BtebApiService,
    private val bookmarkDao: BookmarkDao
) {
    // API
    suspend fun getIndividualResult(roll: String, exam: String?) =
        apiService.getIndividualResult(roll, exam)

    suspend fun getGroupResult(request: GroupSearchRequest) =
        apiService.getGroupResult(request)

    suspend fun getInstitutes(search: String?) =
        apiService.getInstitutes(search)

    suspend fun getInstituteResults(code: String, page: Int, limit: Int) =
        apiService.getInstituteResults(code, page, limit)

    suspend fun getRoutineDepartments() =
        apiService.getRoutineDepartments()

    suspend fun searchRoutine(department: String, semester: String) =
        apiService.searchRoutine(department, semester)

    suspend fun getRoutineBySubjects(subjects: String) =
        apiService.getRoutineBySubjects(subjects)

    suspend fun getCurriculums() =
        apiService.getCurriculums()

    suspend fun getPublicStats() =
        apiService.getPublicStats()

    suspend fun submitContactForm(request: ContactRequest) =
        apiService.submitContactForm(request)

    // Room DB
    val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun isBookmarked(roll: String): Flow<Boolean> = bookmarkDao.isBookmarked(roll)

    suspend fun toggleBookmark(bookmark: BookmarkEntity, currentlyBookmarked: Boolean) {
        if (currentlyBookmarked) {
            bookmarkDao.deleteBookmark(bookmark.roll)
        } else {
            bookmarkDao.insertBookmark(bookmark)
        }
    }
}
