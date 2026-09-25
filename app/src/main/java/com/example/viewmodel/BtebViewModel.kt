package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.RetrofitInstance
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.model.ContactRequest
import com.example.data.model.ContactResponse
import com.example.data.model.GroupResultResponse
import com.example.data.model.GroupSearchRequest
import com.example.data.model.InstituteData
import com.example.data.model.PublicStats
import com.example.data.model.RoutineItem
import com.example.data.model.StudentResult
import com.example.data.model.TechnologyBooklist
import com.example.data.model.UpdateInfo
import com.example.data.repository.BooklistRepository
import com.example.data.repository.BtebRepository
import com.example.util.UpdateManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class BtebViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val apiService = RetrofitInstance.api
    private val repository = BtebRepository(apiService, database.bookmarkDao())
    private val booklistRepository = BooklistRepository(application)
    private val updateManager = UpdateManager()

    // Results
    private val _individualResultState = MutableStateFlow<UiState<StudentResult>>(UiState.Idle)
    val individualResultState: StateFlow<UiState<StudentResult>> = _individualResultState.asStateFlow()

    private val _groupResultState = MutableStateFlow<UiState<GroupResultResponse>>(UiState.Idle)
    val groupResultState: StateFlow<UiState<GroupResultResponse>> = _groupResultState.asStateFlow()

    // Institutes
    private val _institutesState = MutableStateFlow<UiState<List<InstituteData>>>(UiState.Idle)
    val institutesState: StateFlow<UiState<List<InstituteData>>> = _institutesState.asStateFlow()

    private val _instituteResultsState = MutableStateFlow<UiState<List<StudentResult>>>(UiState.Idle)
    val instituteResultsState: StateFlow<UiState<List<StudentResult>>> = _instituteResultsState.asStateFlow()

    // Routine
    private val _routineDepartmentsState = MutableStateFlow<UiState<Pair<List<String>, List<String>>>>(UiState.Idle)
    val routineDepartmentsState: StateFlow<UiState<Pair<List<String>, List<String>>>> = _routineDepartmentsState.asStateFlow()

    private val _routineSearchState = MutableStateFlow<UiState<List<RoutineItem>>>(UiState.Idle)
    val routineSearchState: StateFlow<UiState<List<RoutineItem>>> = _routineSearchState.asStateFlow()

    private val _referredRoutineState = MutableStateFlow<UiState<List<RoutineItem>>>(UiState.Idle)
    val referredRoutineState: StateFlow<UiState<List<RoutineItem>>> = _referredRoutineState.asStateFlow()

    // Curriculums & Public Stats
    private val _curriculumsState = MutableStateFlow<UiState<List<String>>>(UiState.Idle)
    val curriculumsState: StateFlow<UiState<List<String>>> = _curriculumsState.asStateFlow()

    private val _statsState = MutableStateFlow<UiState<PublicStats>>(UiState.Idle)
    val statsState: StateFlow<UiState<PublicStats>> = _statsState.asStateFlow()

    // Contact
    private val _contactState = MutableStateFlow<UiState<ContactResponse>>(UiState.Idle)
    val contactState: StateFlow<UiState<ContactResponse>> = _contactState.asStateFlow()

    // Bookmarks
    val savedBookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Booklists
    private val _booklistTechnologies = MutableStateFlow<List<TechnologyBooklist>>(emptyList())
    val booklistTechnologies: StateFlow<List<TechnologyBooklist>> = _booklistTechnologies.asStateFlow()

    private val _selectedRegulation = MutableStateFlow("2022")
    val selectedRegulation: StateFlow<String> = _selectedRegulation.asStateFlow()

    // Updates
    private val _updateInfoState = MutableStateFlow<UpdateInfo?>(null)
    val updateInfoState: StateFlow<UpdateInfo?> = _updateInfoState.asStateFlow()

    init {
        viewModelScope.launch {
            booklistRepository.loadBooklists()
            _booklistTechnologies.value = booklistRepository.booklists2022
        }
        loadCurriculumsAndStats()
        checkForUpdates()
    }

    fun isBookmarked(roll: String): Flow<Boolean> = repository.isBookmarked(roll)

    fun toggleBookmark(result: StudentResult, currentlyBookmarked: Boolean) {
        viewModelScope.launch {
            val entity = BookmarkEntity(
                roll = result.rollString,
                curriculum = result.exam ?: "DIPLOMA IN ENGINEERING",
                instituteName = result.institute?.name ?: result.institute?.displayName ?: "Polytechnic Institute",
                regulation = result.regulationString,
                status = if (result.isPassed) "PASSED" else "REFERRED"
            )
            repository.toggleBookmark(entity, currentlyBookmarked)
        }
    }

    fun searchIndividualResult(roll: String, exam: String?) {
        if (roll.isBlank()) return
        viewModelScope.launch {
            _individualResultState.value = UiState.Loading
            _referredRoutineState.value = UiState.Idle
            try {
                val response = repository.getIndividualResult(roll.trim(), exam)
                val student = response.data?.firstOrNull()
                if (student != null) {
                    _individualResultState.value = UiState.Success(student)
                    // Fetch routine schedules for referred subjects if any
                    val referreds = student.allReferredSubjects
                    if (referreds.isNotEmpty()) {
                        try {
                            val codes = referreds.mapNotNull { it.subjectCodeString.ifBlank { null } }.joinToString(",")
                            if (codes.isNotBlank()) {
                                val routineResp = repository.getRoutineBySubjects(codes)
                                _referredRoutineState.value = UiState.Success(routineResp.items)
                            }
                        } catch (_: Exception) {}
                    }
                } else {
                    _individualResultState.value = UiState.Error(response.message ?: "No records found for Roll $roll.")
                }
            } catch (e: Exception) {
                _individualResultState.value = UiState.Error(e.localizedMessage ?: "Failed to connect to BTEB servers.")
            }
        }
    }

    fun searchGroupResult(rolls: String, exam: String?) {
        if (rolls.isBlank()) return
        viewModelScope.launch {
            _groupResultState.value = UiState.Loading
            try {
                val req = GroupSearchRequest(rolls = rolls.trim(), exam = exam)
                val resp = repository.getGroupResult(req)
                _groupResultState.value = UiState.Success(resp)
            } catch (e: Exception) {
                _groupResultState.value = UiState.Error(e.localizedMessage ?: "Failed to fetch group results.")
            }
        }
    }

    fun loadInstituteResults(code: String) {
        viewModelScope.launch {
            _instituteResultsState.value = UiState.Loading
            try {
                val resp = repository.getInstituteResults(code, page = 1, limit = 10000)
                _instituteResultsState.value = UiState.Success(resp.data ?: emptyList())
            } catch (e: Exception) {
                _instituteResultsState.value = UiState.Error(e.localizedMessage ?: "Failed to load institute results.")
            }
        }
    }

    fun searchInstitutes(query: String) {
        viewModelScope.launch {
            _institutesState.value = UiState.Loading
            try {
                val q = query.trim().ifBlank { null }
                val resp = repository.getInstitutes(q)
                _institutesState.value = UiState.Success(resp.data ?: emptyList())
            } catch (e: Exception) {
                _institutesState.value = UiState.Error(e.localizedMessage ?: "Failed to search institutes.")
            }
        }
    }

    fun loadRoutineDepartments() {
        viewModelScope.launch {
            _routineDepartmentsState.value = UiState.Loading
            try {
                val resp = repository.getRoutineDepartments()
                val depts = resp.departments ?: emptyList()
                val sems = resp.semesters?.map { it.toString() } ?: emptyList()
                _routineDepartmentsState.value = UiState.Success(Pair(depts, sems))
            } catch (e: Exception) {
                _routineDepartmentsState.value = UiState.Error(e.localizedMessage ?: "Failed to load departments.")
            }
        }
    }

    fun searchRoutine(department: String, semester: String) {
        viewModelScope.launch {
            _routineSearchState.value = UiState.Loading
            try {
                val resp = repository.searchRoutine(department.trim(), semester.trim())
                _routineSearchState.value = UiState.Success(resp.items)
            } catch (e: Exception) {
                _routineSearchState.value = UiState.Error(e.localizedMessage ?: "Failed to load routine.")
            }
        }
    }

    fun searchRoutineBySubjectCode(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            _routineSearchState.value = UiState.Loading
            try {
                val resp = repository.getRoutineBySubjects(code.trim())
                _routineSearchState.value = UiState.Success(resp.items)
            } catch (e: Exception) {
                _routineSearchState.value = UiState.Error(e.localizedMessage ?: "Failed to search subject routine.")
            }
        }
    }

    fun getSubjectName(code: String?): String {
        return booklistRepository.getSubjectName(code)
    }

    fun selectRegulation(regulation: String) {
        _selectedRegulation.value = regulation
        _booklistTechnologies.value = if (regulation == "2016") {
            booklistRepository.booklists2016
        } else {
            booklistRepository.booklists2022
        }
    }

    fun loadCurriculumsAndStats() {
        viewModelScope.launch {
            // Load curriculums
            try {
                val curResp = repository.getCurriculums()
                val list = curResp.data ?: emptyList()
                if (list.isNotEmpty()) {
                    _curriculumsState.value = UiState.Success(list)
                }
            } catch (_: Exception) {}

            // Load public stats
            _statsState.value = UiState.Loading
            try {
                val statsResp = repository.getPublicStats()
                val data = statsResp.data ?: PublicStats()
                _statsState.value = UiState.Success(data)
            } catch (e: Exception) {
                _statsState.value = UiState.Error(e.localizedMessage ?: "Failed to load board statistics.")
            }
        }
    }

    fun submitContact(name: String, subject: String, message: String) {
        if (name.isBlank() || subject.isBlank() || message.isBlank()) return
        viewModelScope.launch {
            _contactState.value = UiState.Loading
            try {
                val resp = repository.submitContactForm(ContactRequest(name, subject, message))
                _contactState.value = UiState.Success(resp)
            } catch (e: Exception) {
                _contactState.value = UiState.Error(e.localizedMessage ?: "Failed to send message.")
            }
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            val result = updateManager.checkForUpdates()
            result.onSuccess {
                _updateInfoState.value = it
            }
        }
    }
}
