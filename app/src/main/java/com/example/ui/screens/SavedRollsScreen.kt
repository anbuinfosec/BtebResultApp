package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.BookmarkEntity
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.RoseReferred
import com.example.viewmodel.BtebViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRollsScreen(
    viewModel: BtebViewModel,
    onNavigateToResult: (roll: String, exam: String) -> Unit,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val bookmarks by viewModel.savedBookmarks.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var itemToDelete by remember { mutableStateOf<BookmarkEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saved Bookmarks (${bookmarks.size})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    if (onOpenDrawer != null) {
                        IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("saved_drawer_button")) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    } else {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("saved_back_button")) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("saved_rolls_screen")
        ) {
            if (bookmarks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "No saved roll numbers yet.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the bookmark icon on any result screen to save it for quick offline access.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bookmarks) { item ->
                        BookmarkRow(
                            item = item,
                            onClick = {
                                Toast.makeText(context, "Opening Roll ${item.roll}...", Toast.LENGTH_SHORT).show()
                                onNavigateToResult(item.roll, item.curriculum)
                            },
                            onDelete = { itemToDelete = item }
                        )
                    }
                }
            }
        }

        itemToDelete?.let { item ->
            ConfirmationDialog(
                title = "Remove Bookmark",
                message = "Are you sure you want to remove Roll ${item.roll} from your saved bookmarks?",
                confirmText = "Remove",
                isDestructive = true,
                onConfirm = {
                    viewModel.toggleBookmark(
                        com.example.data.model.StudentResult(
                            exam = item.curriculum,
                            rawRoll = item.roll,
                            rawRegulation = item.regulation,
                            institute = com.example.data.model.InstituteData(name = item.instituteName)
                        ),
                        currentlyBookmarked = true
                    )
                    Toast.makeText(context, "Removed Roll ${item.roll} from bookmarks", Toast.LENGTH_SHORT).show()
                    itemToDelete = null
                },
                onDismiss = { itemToDelete = null }
            )
        }
    }
}

@Composable
private fun BookmarkRow(
    item: BookmarkEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Roll: ${item.roll}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusChip(isPassed = item.status == "PASSED")
                }
                Text(
                    text = item.instituteName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${item.curriculum} • Reg: ${item.regulation}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = RoseReferred
                )
            }
        }
    }
}
