package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudentResult
import com.example.ui.components.GlassCard
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseReferred
import com.example.viewmodel.BtebViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InstituteResultsScreen(
    instituteCode: String,
    instituteName: String,
    viewModel: BtebViewModel,
    onBack: () -> Unit,
    onNavigateToIndividual: (String, String) -> Unit
) {
    val instituteResultsState by viewModel.instituteResultsState.collectAsStateWithLifecycle()

    // Filter states
    var filterSemester by remember { mutableStateOf("") }
    var filterRegulation by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("") } // "PASSED", "REFERRED", ""
    var filterGrade by remember { mutableStateOf("") }  // "A+", "A", "A-", ...
    var filterPublishDate by remember { mutableStateOf("") }

    // Dropdown expanded states
    var semesterExpanded by remember { mutableStateOf(false) }
    var regulationExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var gradeExpanded by remember { mutableStateOf(false) }
    var dateExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(instituteCode) {
        viewModel.loadInstituteResults(instituteCode)
    }

    val allResults = when (val s = instituteResultsState) {
        is UiState.Success -> s.data
        else -> emptyList()
    }

    // Build filter option lists from data
    val semesters = remember(allResults) {
        allResults.flatMap { r -> r.semesterResults?.map { it.semesterNumberClean } ?: emptyList() }
            .distinct().sortedBy { it.toIntOrNull() ?: 0 }
    }
    val regulations = remember(allResults) {
        allResults.map { it.regulationString }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val publishDates = remember(allResults) {
        allResults.flatMap { r -> r.semesterResults?.flatMap { sem -> sem.examResults?.map { it.date?.substringBefore("T") ?: "" } ?: emptyList() } ?: emptyList() }
            .filter { it.isNotBlank() }.distinct().sortedDescending()
    }

    // Apply filters
    val filteredResults = remember(allResults, filterSemester, filterRegulation, filterStatus, filterGrade, filterPublishDate) {
        allResults.filter { student ->
            // Semester filter
            val matchesSemester = filterSemester.isEmpty() ||
                student.semesterResults?.any { it.semesterNumberClean == filterSemester } == true

            // Regulation filter
            val matchesRegulation = filterRegulation.isEmpty() || student.regulationString == filterRegulation

            // Status filter
            val matchesStatus = filterStatus.isEmpty() ||
                (filterStatus == "PASSED" && student.isPassed) ||
                (filterStatus == "REFERRED" && !student.isPassed)

            // Grade filter (GPA bucket)
            val matchesGrade = filterGrade.isEmpty() || run {
                val gpaStr = student.semesterResults?.firstOrNull()?.gpaString ?: "0.00"
                val gpa = gpaStr.toDoubleOrNull() ?: 0.0
                when (filterGrade) {
                    "A+" -> gpa >= 3.75
                    "A" -> gpa in 3.25..<3.75
                    "A-" -> gpa in 3.00..<3.25
                    "B+" -> gpa in 2.75..<3.00
                    "B" -> gpa in 2.50..<2.75
                    "C" -> gpa in 2.00..<2.50
                    "F" -> gpa < 2.00
                    else -> true
                }
            }

            // Publish date filter — match any semester result date
            val matchesDate = filterPublishDate.isEmpty() ||
                student.semesterResults?.any { sem ->
                    sem.examResults?.any { it.date?.startsWith(filterPublishDate) == true } == true
                } == true

            matchesSemester && matchesRegulation && matchesStatus && matchesGrade && matchesDate
        }
    }

    val hasActiveFilters = filterSemester.isNotEmpty() || filterRegulation.isNotEmpty() ||
        filterStatus.isNotEmpty() || filterGrade.isNotEmpty() || filterPublishDate.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = instituteName.ifBlank { "Institute Results" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (allResults.isNotEmpty()) {
                            Text(
                                text = "Code: $instituteCode • ${allResults.size} students",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadInstituteResults(instituteCode) }) {
                        Icon(Icons.Default.Refresh, "Reload")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ── Filter Card ──────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FilterList, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(6.dp))
                            Text("Filters", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        // Row 1: Publish Date + Semester
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Publish Date
                            ExposedDropdownMenuBox(
                                expanded = dateExpanded,
                                onExpandedChange = { dateExpanded = !dateExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = filterPublishDate.ifBlank { "Any Date" },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Publish Date", fontSize = 11.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateExpanded) },
                                    modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    singleLine = true
                                )
                                ExposedDropdownMenu(expanded = dateExpanded, onDismissRequest = { dateExpanded = false }) {
                                    DropdownMenuItem(text = { Text("Any Date") }, onClick = { filterPublishDate = ""; dateExpanded = false })
                                    publishDates.take(15).forEach { date ->
                                        DropdownMenuItem(text = { Text(date, fontSize = 13.sp) }, onClick = { filterPublishDate = date; dateExpanded = false })
                                    }
                                }
                            }

                            // Semester
                            ExposedDropdownMenuBox(
                                expanded = semesterExpanded,
                                onExpandedChange = { semesterExpanded = !semesterExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = if (filterSemester.isBlank()) "All Semesters" else "Semester $filterSemester",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Semester", fontSize = 11.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterExpanded) },
                                    modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    singleLine = true
                                )
                                ExposedDropdownMenu(expanded = semesterExpanded, onDismissRequest = { semesterExpanded = false }) {
                                    DropdownMenuItem(text = { Text("All Semesters") }, onClick = { filterSemester = ""; semesterExpanded = false })
                                    semesters.forEach { sem ->
                                        DropdownMenuItem(text = { Text("Semester $sem") }, onClick = { filterSemester = sem; semesterExpanded = false })
                                    }
                                }
                            }
                        }

                        // Row 2: Regulation + Status + Grade
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Regulation
                            ExposedDropdownMenuBox(
                                expanded = regulationExpanded,
                                onExpandedChange = { regulationExpanded = !regulationExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = filterRegulation.ifBlank { "All" },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Regulation", fontSize = 11.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regulationExpanded) },
                                    modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    singleLine = true
                                )
                                ExposedDropdownMenu(expanded = regulationExpanded, onDismissRequest = { regulationExpanded = false }) {
                                    DropdownMenuItem(text = { Text("All Regulations") }, onClick = { filterRegulation = ""; regulationExpanded = false })
                                    regulations.forEach { reg ->
                                        DropdownMenuItem(text = { Text(reg) }, onClick = { filterRegulation = reg; regulationExpanded = false })
                                    }
                                }
                            }

                            // Status
                            ExposedDropdownMenuBox(
                                expanded = statusExpanded,
                                onExpandedChange = { statusExpanded = !statusExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = filterStatus.ifBlank { "All Status" },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Status", fontSize = 11.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                                    modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    singleLine = true
                                )
                                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                                    DropdownMenuItem(text = { Text("All Status") }, onClick = { filterStatus = ""; statusExpanded = false })
                                    DropdownMenuItem(text = { Text("✓ Passed") }, onClick = { filterStatus = "PASSED"; statusExpanded = false })
                                    DropdownMenuItem(text = { Text("✗ Referred") }, onClick = { filterStatus = "REFERRED"; statusExpanded = false })
                                }
                            }

                            // Grade
                            ExposedDropdownMenuBox(
                                expanded = gradeExpanded,
                                onExpandedChange = { gradeExpanded = !gradeExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = filterGrade.ifBlank { "All Grades" },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Grade", fontSize = 11.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                                    modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    singleLine = true
                                )
                                ExposedDropdownMenu(expanded = gradeExpanded, onDismissRequest = { gradeExpanded = false }) {
                                    DropdownMenuItem(text = { Text("All Grades") }, onClick = { filterGrade = ""; gradeExpanded = false })
                                    listOf("A+", "A", "A-", "B+", "B", "C", "F").forEach { g ->
                                        DropdownMenuItem(text = { Text("Grade $g") }, onClick = { filterGrade = g; gradeExpanded = false })
                                    }
                                }
                            }
                        }

                        // Result count + Clear
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (hasActiveFilters)
                                    "Showing ${filteredResults.size} of ${allResults.size} students"
                                else
                                    "${allResults.size} total students",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (hasActiveFilters) {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable {
                                        filterSemester = ""; filterRegulation = ""; filterStatus = ""
                                        filterGrade = ""; filterPublishDate = ""
                                    }
                                ) {
                                    Row(
                                        Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Clear, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onErrorContainer)
                                        Text("Clear Filters", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Loading / Error States ────────────────────────────────────────
            when (val state = instituteResultsState) {
                is UiState.Loading -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Loading institute results…", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Warning, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.error)
                                Text(state.message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onErrorContainer)
                                Button(onClick = { viewModel.loadInstituteResults(instituteCode) }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
                is UiState.Idle -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.School, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f))
                            Text("Loading…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                is UiState.Success -> { /* Results shown below */ }
            }

            // ── Result List ───────────────────────────────────────────────────
            if (filteredResults.isEmpty() && hasActiveFilters && instituteResultsState is UiState.Success) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FilterList, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f))
                        Text("No students match the selected filters.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            items(filteredResults, key = { it.id ?: it.rollString }) { student ->
                InstituteStudentCard(
                    student = student,
                    onClick = { onNavigateToIndividual(student.rollString, student.exam ?: "DIPLOMA IN ENGINEERING") }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun InstituteStudentCard(
    student: StudentResult,
    onClick: () -> Unit
) {
    val latestGpa = student.semesterResults?.firstOrNull()?.gpaString ?: "N/A"
    val latestSemester = student.semesterResults?.firstOrNull()?.semesterDisplayName ?: "—"
    val latestDate = student.semesterResults?.firstOrNull()?.examResults?.firstOrNull()?.date
        ?.substringBefore("T") ?: "—"
    val isPassed = student.isPassed
    val statusColor = if (isPassed) EmeraldSuccess else RoseReferred
    val statusLabel = if (isPassed) "PASSED" else "REFERRED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // GPA circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = latestGpa,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = statusColor,
                        fontSize = if (latestGpa.length > 4) 10.sp else 13.sp
                    )
                    Text("GPA", style = MaterialTheme.typography.labelSmall, color = statusColor.copy(0.7f), fontSize = 8.sp)
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Roll: ${student.rollString}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = student.exam ?: "DIPLOMA IN ENGINEERING",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Reg: ${student.regulationString}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("•", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(latestSemester, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("•", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(latestDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Status badge
            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        if (isPassed) Icons.Default.CheckCircle else Icons.Default.Warning,
                        null,
                        Modifier.size(12.dp),
                        tint = statusColor
                    )
                    Text(statusLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = statusColor)
                }
            }
        }
    }
}
