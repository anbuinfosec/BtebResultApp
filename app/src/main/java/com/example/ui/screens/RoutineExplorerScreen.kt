package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RoutineItem
import com.example.ui.components.CustomTextField
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseReferred
import com.example.viewmodel.BtebViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineExplorerScreen(
    viewModel: BtebViewModel,
    onOpenDrawer: () -> Unit
) {
    val deptsState by viewModel.routineDepartmentsState.collectAsStateWithLifecycle()
    val searchState by viewModel.routineSearchState.collectAsStateWithLifecycle()

    var selectedDepartment by remember { mutableStateOf("Civil") }
    var selectedSemester by remember { mutableStateOf("4") }
    var subjectCodeSearch by remember { mutableStateOf("") }
    var hideEndedExams by remember { mutableStateOf(true) }

    var deptExpanded by remember { mutableStateOf(false) }
    var semExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadRoutineDepartments()
        viewModel.searchRoutine(selectedDepartment, selectedSemester)
    }

    val (departments, semesters) = when (deptsState) {
        is UiState.Success -> (deptsState as UiState.Success<Pair<List<String>, List<String>>>).data
        else -> Pair(
            listOf("Civil", "Computer Science &", "Electrical", "Mechanical", "Electronics"),
            listOf("1", "2", "3", "4", "5", "6", "7", "8")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Exam Routine Explorer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("routine_drawer_button")) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("routine_explorer_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Filter Controls Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dept Dropdown
                        ExposedDropdownMenuBox(
                            expanded = deptExpanded,
                            onExpandedChange = { deptExpanded = !deptExpanded },
                            modifier = Modifier.weight(1.3f)
                        ) {
                            OutlinedTextField(
                                value = selectedDepartment,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Department") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true)
                            )

                            ExposedDropdownMenu(
                                expanded = deptExpanded,
                                onDismissRequest = { deptExpanded = false }
                            ) {
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept) },
                                        onClick = {
                                            selectedDepartment = dept
                                            deptExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Semester Dropdown
                        ExposedDropdownMenuBox(
                            expanded = semExpanded,
                            onExpandedChange = { semExpanded = !semExpanded },
                            modifier = Modifier.weight(0.7f)
                        ) {
                            OutlinedTextField(
                                value = "Sem $selectedSemester",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Sem") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semExpanded) },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true)
                            )

                            ExposedDropdownMenu(
                                expanded = semExpanded,
                                onDismissRequest = { semExpanded = false }
                            ) {
                                semesters.forEach { sem ->
                                    DropdownMenuItem(
                                        text = { Text("Semester $sem") },
                                        onClick = {
                                            selectedSemester = sem
                                            semExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PrimaryButton(
                            text = "View Schedule",
                            onClick = { viewModel.searchRoutine(selectedDepartment, selectedSemester) },
                            icon = Icons.Default.CalendarMonth,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        FilterChip(
                            selected = hideEndedExams,
                            onClick = { hideEndedExams = !hideEndedExams },
                            label = { Text("Upcoming") },
                            leadingIcon = if (hideEndedExams) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }

                    // Subject Code Search
                    CustomTextField(
                        value = subjectCodeSearch,
                        onValueChange = {
                            subjectCodeSearch = it
                            if (it.isBlank()) {
                                viewModel.searchRoutine(selectedDepartment, selectedSemester)
                            }
                        },
                        label = "Search by Subject Code",
                        placeholder = "e.g. 26442 or 26811",
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (subjectCodeSearch.isNotBlank()) {
                                IconButton(onClick = { viewModel.searchRoutineBySubjectCode(subjectCodeSearch) }) {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Code")
                                }
                            }
                        },
                        onImeAction = {
                            if (subjectCodeSearch.isNotBlank()) {
                                viewModel.searchRoutineBySubjectCode(subjectCodeSearch)
                            }
                        }
                    )
                }
            }

            // Results Timeline View
            when (val state = searchState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is UiState.Error -> {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = RoseReferred)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = state.message, color = RoseReferred)
                        }
                    }
                }

                is UiState.Success -> {
                    val rawItems = state.data
                    val filteredItems = if (hideEndedExams) rawItems.filter { !it.isEnded } else rawItems

                    if (filteredItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (rawItems.isNotEmpty() && hideEndedExams) {
                                    "All ${rawItems.size} exams for this semester have already concluded. Toggle 'Upcoming' to view archive."
                                } else {
                                    "No routine schedules found for $selectedDepartment Semester $selectedSemester."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredItems) { item ->
                                RoutineScheduleCard(item = item, viewModel = viewModel)
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun RoutineScheduleCard(
    item: RoutineItem,
    viewModel: BtebViewModel
) {
    val resolvedName = viewModel.getSubjectName(item.subjectCodeString).ifBlank { item.subjectName ?: "Subject" }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Code: ${item.subjectCodeString}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Surface(
                    color = if (item.isEnded) MaterialTheme.colorScheme.surfaceVariant else EmeraldSuccess.copy(alpha = 0.2f),
                    contentColor = if (item.isEnded) MaterialTheme.colorScheme.onSurfaceVariant else EmeraldSuccess,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (item.isEnded) "${item.formattedDate} (Ended)" else item.formattedDate,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "$resolvedName",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Time: ${item.time ?: "TBA"}${if (!item.day.isNullOrBlank()) " (${item.day})" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!item.semesterString.isNullOrBlank()) {
                    Text(
                        text = "Semester ${item.semesterString}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
