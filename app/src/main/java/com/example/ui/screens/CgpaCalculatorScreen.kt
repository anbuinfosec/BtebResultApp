package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.InfoDialog
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoLight
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaCalculatorScreen(
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val context = LocalContext.current

    // Standard BTEB weights: 1st-3rd: 5%, 4th: 10%, 5th-6th: 15%, 7th: 20%, 8th: 25%
    val semWeights = remember { listOf(5.0, 5.0, 5.0, 10.0, 15.0, 15.0, 20.0, 25.0) }
    val gpaValues = remember { mutableStateListOf(3.50f, 3.60f, 3.75f, 3.80f, 3.70f, 3.85f, 3.90f, 4.00f) }

    var showInfoDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Live CGPA calculation
    val totalWeightedPoints = gpaValues.indices.sumOf { i ->
        gpaValues[i].toDouble() * (semWeights[i] / 100.0)
    }
    val calculatedCgpa = (totalWeightedPoints * 100).roundToInt() / 100.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BTEB CGPA Calculator",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    if (onOpenDrawer != null) {
                        IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("cgpa_drawer_button")) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    } else {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("cgpa_back_button")) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Formula Info")
                    }
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset GPAs")
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
                .testTag("cgpa_calculator_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live CGPA Result Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {
                    Text(
                        text = "Estimated Final CGPA",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%.2f", calculatedCgpa),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 48.sp
                        )
                    )
                    Text(
                        text = when {
                            calculatedCgpa >= 3.75 -> "First Class (Outstanding Distinction)"
                            calculatedCgpa >= 3.00 -> "First Class"
                            calculatedCgpa >= 2.25 -> "Second Class"
                            else -> "Pass"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("CGPA", "My estimated BTEB CGPA is ${String.format("%.2f", calculatedCgpa)}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "CGPA ${String.format("%.2f", calculatedCgpa)} copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy CGPA")
                        }
                    }
                }
            }

            // Semesters List with Sliders
            Text(
                text = "Semester GPA Inputs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            for (i in 0 until 8) {
                val semesterNumber = i + 1
                val weight = semWeights[i]
                val currentGpa = gpaValues[i]

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Semester $semesterNumber (${weight.toInt()}% Weight)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = String.format("%.2f", currentGpa),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Slider(
                            value = currentGpa,
                            onValueChange = { gpaValues[i] = (it * 100).roundToInt() / 100f },
                            valueRange = 2.00f..4.00f,
                            steps = 39,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (showInfoDialog) {
            InfoDialog(
                title = "Official BTEB CGPA Formula",
                message = "According to Bangladesh Technical Education Board (BTEB) Diploma in Engineering regulations, final CGPA is calculated using the weighted average formula across 8 semesters:\n\n• 1st Semester: 5%\n• 2nd Semester: 5%\n• 3rd Semester: 5%\n• 4th Semester: 10%\n• 5th Semester: 15%\n• 6th Semester: 15%\n• 7th Semester: 20%\n• 8th Semester: 25%\n\nTotal = 100% Weighted Points.",
                onDismiss = { showInfoDialog = false }
            )
        }

        if (showResetDialog) {
            ConfirmationDialog(
                title = "Reset All GPAs",
                message = "Are you sure you want to reset all 8 semester GPAs to standard defaults?",
                confirmText = "Reset",
                isDestructive = false,
                onConfirm = {
                    val defaults = listOf(3.50f, 3.60f, 3.75f, 3.80f, 3.70f, 3.85f, 3.90f, 4.00f)
                    defaults.indices.forEach { i -> gpaValues[i] = defaults[i] }
                    Toast.makeText(context, "All semester GPAs reset to default", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showResetDialog = false }
            )
        }
    }
}
