package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SubjectBooklistItem
import com.example.data.model.TechnologyBooklist
import com.example.ui.components.CustomTextField
import com.example.ui.components.GlassCard
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.SkySecondary
import com.example.viewmodel.BtebViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooklistScreen(
    viewModel: BtebViewModel,
    onOpenDrawer: () -> Unit
) {
    val technologies by viewModel.booklistTechnologies.collectAsStateWithLifecycle()
    val selectedRegulation by viewModel.selectedRegulation.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTechIndex by remember { mutableIntStateOf(0) }
    var selectedSemesterName by remember { mutableStateOf("All") }
    var techDropdownExpanded by remember { mutableStateOf(false) }

    val filteredTechnologies = remember(technologies, searchQuery) {
        if (searchQuery.isBlank()) technologies
        else technologies.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.semesters?.any { sem ->
                sem.subjects?.any { sub ->
                    sub.codeString.contains(searchQuery, ignoreCase = true) ||
                    sub.cleanName.contains(searchQuery, ignoreCase = true)
                } == true
            } == true
        }
    }

    val currentTechnology = filteredTechnologies.getOrNull(selectedTechIndex.coerceAtMost((filteredTechnologies.size - 1).coerceAtLeast(0)))

    // Extract subjects to display
    val displaySubjects: List<Pair<String, SubjectBooklistItem>> = remember(currentTechnology, selectedSemesterName, searchQuery) {
        val list = mutableListOf<Pair<String, SubjectBooklistItem>>()
        if (searchQuery.isNotBlank() && searchQuery.all { it.isDigit() }) {
            // If searching by subject code, search across all technologies
            technologies.forEach { tech ->
                tech.semesters?.forEach { sem ->
                    sem.subjects?.forEach { sub ->
                        if (sub.codeString.contains(searchQuery, ignoreCase = true) && list.none { it.second.codeString == sub.codeString }) {
                            list.add(Pair("${tech.cleanName} • ${sem.name}", sub))
                        }
                    }
                }
            }
        } else {
            currentTechnology?.semesters?.forEach { sem ->
                if (selectedSemesterName == "All" || sem.name.startsWith(selectedSemesterName, ignoreCase = true)) {
                    sem.subjects?.forEach { sub ->
                        if (searchQuery.isBlank() || sub.cleanName.contains(searchQuery, ignoreCase = true) || sub.codeString.contains(searchQuery, ignoreCase = true)) {
                            list.add(Pair(sem.name, sub))
                        }
                    }
                }
            }
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booklists & Syllabus",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("booklist_drawer_button")) {
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
                .testTag("booklist_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Regulation Tabs
            TabRow(
                selectedTabIndex = if (selectedRegulation == "2022") 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = selectedRegulation == "2022",
                    onClick = {
                        viewModel.selectRegulation("2022")
                        selectedTechIndex = 0
                    },
                    text = { Text("2022 Regulation", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedRegulation == "2016",
                    onClick = {
                        viewModel.selectRegulation("2016")
                        selectedTechIndex = 0
                    },
                    text = { Text("2016 Regulation", fontWeight = FontWeight.Bold) }
                )
            }

            // Search Filter Box
            CustomTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    selectedTechIndex = 0
                },
                label = "Search Technology or Subject Code/Name",
                placeholder = "e.g. Computer, 26811, Electronics...",
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                testTag = "booklist_search_input"
            )

            // Technology Dropdown Selector
            if (filteredTechnologies.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = techDropdownExpanded,
                    onExpandedChange = { techDropdownExpanded = !techDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = currentTechnology?.cleanName ?: "Select Technology",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Technology / Department (${filteredTechnologies.size})") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = techDropdownExpanded) },
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
                        expanded = techDropdownExpanded,
                        onDismissRequest = { techDropdownExpanded = false }
                    ) {
                        filteredTechnologies.forEachIndexed { index, tech ->
                            DropdownMenuItem(
                                text = { Text(tech.cleanName, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    selectedTechIndex = index
                                    techDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Semester Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val sems = listOf("All", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th")
                items(sems) { sem ->
                    FilterChip(
                        selected = selectedSemesterName == sem,
                        onClick = { selectedSemesterName = sem },
                        label = { Text(if (sem == "All") "All Semesters" else "$sem Sem") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Subjects List
            if (displaySubjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No subjects found for this selection.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    items(displaySubjects) { (semesterName, subject) ->
                        BooklistSubjectCard(semesterName = semesterName, subject = subject)
                    }
                }
            }
        }
    }
}

@Composable
private fun BooklistSubjectCard(
    semesterName: String,
    subject: SubjectBooklistItem
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
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
                        text = "Code: ${subject.codeString}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = SkySecondary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = semesterName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = SkySecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = subject.cleanName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Details row: Credits, Theory, Practical, Marks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (subject.creditString.isNotBlank()) {
                    Text(
                        text = "Credit: ${subject.creditString}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (subject.theoryString.isNotBlank() || subject.practicalString.isNotBlank()) {
                    Text(
                        text = "T: ${subject.theoryString.ifBlank { "0" }}  P: ${subject.practicalString.ifBlank { "0" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (subject.marksString.isNotBlank()) {
                    Text(
                        text = "Marks: ${subject.marksString}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = EmeraldSuccess
                    )
                }
            }
        }
    }
}
