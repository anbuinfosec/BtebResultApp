package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.SubjectDetailDialog
import com.example.ui.components.ErrorStateView
import com.example.ui.components.PulsingLogoLoader
import com.example.ui.components.ResultCardSkeleton
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ReferredSubject
import com.example.data.model.RoutineItem
import com.example.data.model.SemesterResult
import com.example.data.model.StudentResult
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.StatusChip
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseReferred
import com.example.ui.theme.SkySecondary
import com.example.viewmodel.BtebViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IndividualResultScreen(
    roll: String,
    exam: String,
    viewModel: BtebViewModel,
    onBack: () -> Unit
) {
    val resultState by viewModel.individualResultState.collectAsStateWithLifecycle()
    val referredRoutineState by viewModel.referredRoutineState.collectAsStateWithLifecycle()
    val isBookmarked by viewModel.isBookmarked(roll).collectAsStateWithLifecycle(initialValue = false)
    val context = LocalContext.current

    var selectedSubjectForDialog by remember { mutableStateOf<Triple<String, String, String?>?>(null) }

    LaunchedEffect(roll, exam) {
        viewModel.searchIndividualResult(roll, exam)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Result: Roll $roll",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (resultState is UiState.Success) {
                        val studentResult = (resultState as UiState.Success<StudentResult>).data
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("BTEB Result", "BTEB Result for Roll ${studentResult.rollString} (${studentResult.exam}): Status = ${if (studentResult.isPassed) "PASSED" else "REFERRED"}, Institute: ${studentResult.institute?.name}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Result details copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Result",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.toggleBookmark(studentResult, isBookmarked)
                                Toast.makeText(
                                    context,
                                    if (!isBookmarked) "Saved Roll $roll to Bookmarks" else "Removed Roll $roll from Bookmarks",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.testTag("bookmark_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("individual_result_screen")
        ) {
            when (val state = resultState) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PulsingLogoLoader(
                            title = "Retrieving Board Results...",
                            subtitle = "Fetching official grades for Roll $roll ($exam)"
                        )
                        ResultCardSkeleton()
                    }
                }

                is UiState.Error -> {
                    ErrorStateView(
                        message = state.message,
                        onRetry = { viewModel.searchIndividualResult(roll, exam) },
                        onAlternativeAction = onBack,
                        alternativeActionText = "Back to Search",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is UiState.Success -> {
                    val result = state.data
                    val referreds = result.allReferredSubjects

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Student Profile Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 24.dp,
                            testTag = "student_profile_card"
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = result.exam ?: exam,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Roll: ${result.rollString}",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        )
                                        Text(
                                            text = "Regulation: ${result.regulationString}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    StatusChip(isPassed = result.isPassed)
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // Institute Info
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = SkySecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = result.institute?.name ?: "Polytechnic Institute",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Code: ${result.institute?.codeString ?: "N/A"}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (!result.institute?.district.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = result.institute?.district!!,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Referred Alert Card with Resolved Subject Names
                        if (referreds.isNotEmpty()) {
                            ReferredAlertCard(
                                referreds = referreds,
                                viewModel = viewModel,
                                onSubjectClick = { code, name, stat ->
                                    selectedSubjectForDialog = Triple(code, name, stat)
                                }
                            )
                        }

                        // Routine from Roll: Don't show routine if exam has ended!
                        if (referreds.isNotEmpty()) {
                            RoutineFromRollCard(routineState = referredRoutineState, viewModel = viewModel)
                        }

                        // Semester Breakdown
                        Text(
                            text = "Semester Wise Results",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        if (result.semesterResults.isNullOrEmpty()) {
                            Text(
                                text = "No semester breakdown recorded.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            result.semesterResults.forEach { semRes ->
                                SemesterAccordionCard(
                                    semesterResult = semRes,
                                    viewModel = viewModel,
                                    onSubjectClick = { code, name, stat ->
                                        selectedSubjectForDialog = Triple(code, name, stat)
                                    }
                                )
                            }
                        }
                    }
                }

                else -> {}
            }

            selectedSubjectForDialog?.let { (code, name, stat) ->
                SubjectDetailDialog(
                    subjectCode = code,
                    subjectName = name,
                    status = stat,
                    onDismiss = { selectedSubjectForDialog = null }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReferredAlertCard(
    referreds: List<ReferredSubject>,
    viewModel: BtebViewModel,
    onSubjectClick: (code: String, name: String, status: String?) -> Unit
) {
    Surface(
        color = RoseReferred.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoseReferred.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = RoseReferred,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Current Referred Subjects (${referreds.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RoseReferred
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                referreds.forEach { ref ->
                    val resolvedName = viewModel.getSubjectName(ref.subjectCodeString)
                    val displayName = when {
                        resolvedName.isNotBlank() -> resolvedName
                        !ref.subjectName.isNullOrBlank() -> ref.subjectName
                        else -> ""
                    }

                    Surface(
                        onClick = { onSubjectClick(ref.subjectCodeString, displayName, "REFERRED [${ref.failedType ?: "Theory"}]") },
                        color = RoseReferred,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (displayName.isNotBlank()) "${ref.subjectCodeString} - $displayName" else ref.subjectCodeString,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                if (ref.subjectSemester != null) {
                                    Text(
                                        text = "${ref.subjectSemester}th Semester",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.85f))
                                    )
                                }
                            }
                            if (!ref.failedType.isNullOrBlank()) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = ref.failedType,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineFromRollCard(
    routineState: UiState<List<RoutineItem>>,
    viewModel: BtebViewModel
) {
    when (routineState) {
        is UiState.Success -> {
            // User mandate: "on after exam end don't show routine"
            val upcomingItems = routineState.data.filter { !it.isEnded }
            if (upcomingItems.isEmpty()) {
                // If all exams have ended, do not display routine
                return
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upcoming Makeup Exam Schedule",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        )
                    }

                    upcomingItems.forEach { item ->
                        RoutineItemRow(item = item, viewModel = viewModel)
                    }
                }
            }
        }

        is UiState.Loading -> {
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = EmeraldSuccess
                    )
                    Text(
                        text = "Checking upcoming exam routine for referred subjects...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        else -> {}
    }
}

@Composable
private fun RoutineItemRow(
    item: RoutineItem,
    viewModel: BtebViewModel
) {
    val resolvedName = viewModel.getSubjectName(item.subjectCodeString).ifBlank { item.subjectName ?: "Subject" }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.subjectCodeString} - $resolvedName",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "Time: ${item.time ?: "TBD"}${if (!item.day.isNullOrBlank()) " (${item.day})" else ""}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = EmeraldSuccess.copy(alpha = 0.2f),
                contentColor = EmeraldSuccess,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = item.formattedDate,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SemesterAccordionCard(
    semesterResult: SemesterResult,
    viewModel: BtebViewModel,
    onSubjectClick: (code: String, name: String, status: String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isPassed = !semesterResult.hasReferreds

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        onClick = { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = semesterResult.semesterDisplayName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isPassed) "GPA: ${semesterResult.gpaString}" else "Status: REFERRED",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isPassed) MaterialTheme.colorScheme.primary else RoseReferred
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(isPassed = isPassed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Passed Subjects
                    if (!semesterResult.passedSubjects.isNullOrEmpty()) {
                        Text(
                            text = "Passed Subjects (Tap for details):",
                            style = MaterialTheme.typography.labelLarge.copy(color = EmeraldSuccess)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            semesterResult.passedSubjects.forEach { sub ->
                                val name = viewModel.getSubjectName(sub)
                                Surface(
                                    onClick = { onSubjectClick(sub, name, "PASSED") },
                                    color = EmeraldSuccess.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (name.isNotBlank()) "$sub - $name" else sub,
                                        style = MaterialTheme.typography.labelSmall.copy(color = EmeraldSuccess),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Referred Subjects
                    if (semesterResult.hasReferreds) {
                        Text(
                            text = "Referred Subjects (Tap for details):",
                            style = MaterialTheme.typography.labelLarge.copy(color = RoseReferred)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            semesterResult.referredList.forEach { ref ->
                                val name = viewModel.getSubjectName(ref.subjectCodeString)
                                Surface(
                                    onClick = { onSubjectClick(ref.subjectCodeString, name, "REFERRED [${ref.failedType ?: "Theory"}]") },
                                    color = RoseReferred.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${ref.subjectCodeString}${if (name.isNotBlank()) " - $name" else ""} [${ref.failedType ?: "Referred"}]",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = RoseReferred,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
