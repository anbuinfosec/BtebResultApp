package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CustomTextField
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseReferred
import com.example.viewmodel.BtebViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(
    viewModel: BtebViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val contactState by viewModel.contactState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    androidx.compose.runtime.LaunchedEffect(contactState) {
        if (contactState is UiState.Success) {
            Toast.makeText(context, "Your message has been sent successfully!", Toast.LENGTH_LONG).show()
            name = ""
            subject = ""
            message = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contact & Support",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    if (onOpenDrawer != null) {
                        IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("contact_drawer_button")) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    } else {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("contact_back_button")) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("contact_support_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Submit a Query / Report Issue",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Have missing result data or feedback? Fill out the form below to get in touch.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Your Name",
                        placeholder = "e.g. John Doe",
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        testTag = "contact_name_input"
                    )

                    CustomTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = "Subject",
                        placeholder = "e.g. Missing Result / Roll Query",
                        leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Filled.Subject, contentDescription = null) },
                        testTag = "contact_subject_input"
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message") },
                        placeholder = { Text("Describe your issue or feedback in detail...") },
                        minLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_message_input"),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    PrimaryButton(
                        text = "Submit Message",
                        onClick = { viewModel.submitContact(name, subject, message) },
                        enabled = name.isNotBlank() && subject.isNotBlank() && message.isNotBlank(),
                        isLoading = contactState is UiState.Loading,
                        icon = Icons.AutoMirrored.Filled.Send,
                        testTag = "submit_contact_button"
                    )
                }
            }

            // Status Banner
            when (val state = contactState) {
                is UiState.Success -> {
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = state.data.message ?: "Your message has been sent successfully!",
                                style = MaterialTheme.typography.titleMedium.copy(color = EmeraldSuccess)
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    Surface(
                        color = RoseReferred.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoseReferred),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = RoseReferred)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium.copy(color = RoseReferred)
                            )
                        }
                    }
                }

                else -> {}
            }
        }
    }
}
