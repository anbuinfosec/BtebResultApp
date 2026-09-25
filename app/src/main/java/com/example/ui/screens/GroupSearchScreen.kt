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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AutoResizeText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GroupResultResponse
import com.example.data.model.StudentResult
import com.example.ui.components.CustomTextField
import com.example.ui.components.ErrorStateView
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.PulsingLogoLoader
import com.example.ui.components.ShimmerBox
import com.example.ui.components.StatusChip
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseReferred
import com.example.ui.theme.SkySecondary
import com.example.viewmodel.BtebViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSearchScreen(
    viewModel: BtebViewModel,
    onNavigateToIndividual: (roll: String, exam: String) -> Unit,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val groupResultState by viewModel.groupResultState.collectAsStateWithLifecycle()
    val curriculumsState by viewModel.curriculumsState.collectAsStateWithLifecycle()

    var rollInput by remember { mutableStateOf("") }
    var selectedCurriculum by remember { mutableStateOf("DIPLOMA IN ENGINEERING") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val curriculumOptions = when (curriculumsState) {
        is UiState.Success -> (curriculumsState as UiState.Success<List<String>>).data
        else -> listOf("DIPLOMA IN ENGINEERING", "DIPLOMA IN TEXTILE ENGINEERING", "DIPLOMA IN AGRICULTURE")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Group / Batch Search",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    if (onOpenDrawer != null) {
                        IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("group_drawer_button")) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    } else {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("group_back_button")) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
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
                .testTag("group_search_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Form Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Curriculum Dropdown
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCurriculum,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Curriculum") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
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
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            curriculumOptions.forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text(curr) },
                                    onClick = {
                                        selectedCurriculum = curr
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    CustomTextField(
                        value = rollInput,
                        onValueChange = { rollInput = it },
                        label = "Roll Numbers / Range",
                        placeholder = "Enter roll comma or using start-end",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onImeAction = {
                            if (rollInput.isNotBlank()) {
                                viewModel.searchGroupResult(rollInput, selectedCurriculum)
                            }
                        },
                        testTag = "group_rolls_input"
                    )

                    PrimaryButton(
                        text = "Fetch Batch Results",
                        onClick = { viewModel.searchGroupResult(rollInput, selectedCurriculum) },
                        enabled = rollInput.isNotBlank(),
                        testTag = "fetch_group_button"
                    )
                }
            }

            // Results Section
            when (val state = groupResultState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            PulsingLogoLoader(
                                title = "Processing Batch Results...",
                                subtitle = "Scanning records for specified rolls..."
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorStateView(
                        message = state.message,
                        onRetry = { viewModel.searchGroupResult(rollInput, selectedCurriculum) },
                        alternativeActionText = "Edit Rolls",
                        onAlternativeAction = { /* User can change input above */ },
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                is UiState.Success -> {
                    val groupRes = state.data
                    val results = groupRes.results ?: emptyList()

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Summary Bar
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 16.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                SummaryMetric("Total", "${groupRes.total ?: 0}", MaterialTheme.colorScheme.primary)
                                SummaryMetric("Found", "${groupRes.found ?: 0}", EmeraldSuccess)
                                SummaryMetric("Not Found", "${groupRes.notFound ?: 0}", RoseReferred)
                            }
                        }

                        // Student Results List
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(results) { student ->
                                GroupStudentCard(
                                    student = student,
                                    onClick = { onNavigateToIndividual(student.rollString, student.exam ?: selectedCurriculum) }
                                )
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Enter a roll range (e.g. 728470-728480) and press Fetch Batch Results.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMetric(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AutoResizeText(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = color),
            maxLines = 1,
            minFontSize = 14.sp
        )
        AutoResizeText(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            minFontSize = 9.sp
        )
    }
}

@Composable
private fun GroupStudentCard(student: StudentResult, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                AutoResizeText(
                    text = "Roll: ${student.rollString}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    minFontSize = 13.sp
                )
                Text(
                    text = student.institute?.name ?: "Polytechnic Institute",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            StatusChip(isPassed = student.isPassed)
        }
    }
}
