package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CustomTextField
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.UpdateDialog
import com.example.ui.theme.AutoResizeText
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoDark
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.SkySecondary
import com.example.ui.theme.rememberScreenInfo
import com.example.viewmodel.BtebViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: BtebViewModel,
    onNavigateToResult: (roll: String, exam: String) -> Unit,
    onNavigateToGroupSearch: () -> Unit,
    onNavigateToBooklists: () -> Unit,
    onNavigateToRoutine: () -> Unit,
    onNavigateToInstitutes: () -> Unit,
    onNavigateToCgpa: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToContact: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var rollInput by remember { mutableStateOf("") }
    var selectedCurriculum by remember { mutableStateOf("DIPLOMA IN ENGINEERING") }
    var curriculumDropdownExpanded by remember { mutableStateOf(false) }

    val bookmarks by viewModel.savedBookmarks.collectAsStateWithLifecycle()
    val statsState by viewModel.statsState.collectAsStateWithLifecycle()
    val curriculumsState by viewModel.curriculumsState.collectAsStateWithLifecycle()
    val updateInfo by viewModel.updateInfoState.collectAsStateWithLifecycle()

    var showUpdateDialog by remember { mutableStateOf(false) }
    var updatePrompted by remember { mutableStateOf(false) }

    LaunchedEffect(updateInfo) {
        if (updateInfo?.isUpdateAvailable == true && !updatePrompted) {
            updatePrompted = true
            showUpdateDialog = true
        }
    }

    val curriculumOptions = when (curriculumsState) {
        is UiState.Success -> (curriculumsState as UiState.Success<List<String>>).data
        else -> listOf(
            "DIPLOMA IN ENGINEERING",
            "DIPLOMA IN TEXTILE ENGINEERING",
            "DIPLOMA IN AGRICULTURE",
            "DIPLOMA IN FORESTRY",
            "DIPLOMA IN FISHERIES",
            "DIPLOMA IN LIVESTOCK"
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadCurriculumsAndStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bteb_logo),
                            contentDescription = "BTEB Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = "BTEB Portal",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("home_drawer_button")) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    if (updateInfo?.isUpdateAvailable == true) {
                        IconButton(onClick = { showUpdateDialog = true }) {
                            BadgedBox(
                                badge = { Badge { Text("1") } }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = "Update Available",
                                    tint = EmeraldSuccess
                                )
                            }
                        }
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "About App")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("home_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Header Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    IndigoLight.copy(alpha = 0.25f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "OFFICIAL BTEB Result By",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "@AnbuSoft",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Text(
                            text = "Find Exam Results & CGPA",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Instant individual GPA, referred lists, full class batch rosters & regulation syllabus.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Quick Search Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Individual Result Lookup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    // Curriculum Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = curriculumDropdownExpanded,
                        onExpandedChange = { curriculumDropdownExpanded = !curriculumDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCurriculum,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Curriculum / Program") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = curriculumDropdownExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, true)
                        )
                        ExposedDropdownMenu(
                            expanded = curriculumDropdownExpanded,
                            onDismissRequest = { curriculumDropdownExpanded = false }
                        ) {
                            curriculumOptions.forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text(curr, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = {
                                        selectedCurriculum = curr
                                        curriculumDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 6-digit Roll Input
                    CustomTextField(
                        value = rollInput,
                        onValueChange = {
                            if (it.length <= 8) {
                                rollInput = it.filter { char -> char.isDigit() }
                            }
                        },
                        label = "Board Roll Number",
                        placeholder = "e.g. 100328",
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Search,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        onImeAction = {
                            if (rollInput.isNotBlank()) {
                                keyboardController?.hide()
                                onNavigateToResult(rollInput.trim(), selectedCurriculum)
                            } else {
                                Toast.makeText(context, "Please enter a valid Roll Number", Toast.LENGTH_SHORT).show()
                            }
                        },
                        testTag = "home_roll_input"
                    )

                    PrimaryButton(
                        text = "Check Result",
                        onClick = {
                            if (rollInput.isNotBlank()) {
                                keyboardController?.hide()
                                onNavigateToResult(rollInput.trim(), selectedCurriculum)
                            } else {
                                Toast.makeText(context, "Please enter a valid Roll Number", Toast.LENGTH_SHORT).show()
                            }
                        },
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        modifier = Modifier.testTag("home_search_button")
                    )

                    // Quick Bookmarks chips
                    if (bookmarks.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Saved Bookmarks",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                bookmarks.take(5).forEach { bookmark ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable {
                                            onNavigateToResult(bookmark.roll, bookmark.curriculum)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Bookmark,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = bookmark.roll,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Services Grid
            Text(
                text = "Academic Services",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Group Search",
                    subtitle = "Roster & Class pass rate",
                    icon = Icons.Default.Group,
                    iconColor = SkySecondary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToGroupSearch
                )
                QuickActionCard(
                    title = "Booklists",
                    subtitle = "2022 & 2016 Syllabus",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    iconColor = IndigoLight,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBooklists
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Exam Routine",
                    subtitle = "Live schedule & filter",
                    icon = Icons.Default.CalendarMonth,
                    iconColor = EmeraldSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToRoutine
                )
                QuickActionCard(
                    title = "Polytechnics",
                    subtitle = "800+ Institute Directory",
                    icon = Icons.Default.School,
                    iconColor = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToInstitutes
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "CGPA Calc",
                    subtitle = "Weighted 8-Sem GPA",
                    icon = Icons.Default.Calculate,
                    iconColor = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCgpa
                )
                QuickActionCard(
                    title = "Statistics",
                    subtitle = "Board pass/ref metrics",
                    icon = Icons.Default.BarChart,
                    iconColor = Color(0xFFEC4899),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToStats
                )
            }

            // Board Statistics Preview
            if (statsState is UiState.Success) {
                val stats = (statsState as UiState.Success).data
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BTEB Board Statistics",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Live Data",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox(
                                label = "Students",
                                value = String.format("%,d", stats.displayTotalStudents),
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                label = "Institutes",
                                value = String.format("%,d", stats.displayTotalInstitutes),
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                label = "Pass Rate",
                                value = stats.passPercentageFormatted,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showUpdateDialog && updateInfo != null) {
        UpdateDialog(
            updateInfo = updateInfo!!,
            onDismiss = { showUpdateDialog = false }
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val screenInfo = rememberScreenInfo()
    val padding = if (screenInfo.isCompact) 12.dp else 14.dp
    val iconBoxSize = if (screenInfo.isCompact) 38.dp else 42.dp
    val iconSize = if (screenInfo.isCompact) 20.dp else 22.dp

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(iconBoxSize)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(iconSize))
            }

            AutoResizeText(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                minFontSize = 12.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = if (screenInfo.isCompact) 11.sp else 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val screenInfo = rememberScreenInfo()
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AutoResizeText(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (screenInfo.isCompact) 14.sp else 16.sp
                ),
                maxLines = 1,
                minFontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            AutoResizeText(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = if (screenInfo.isCompact) 10.sp else 11.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                minFontSize = 8.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
