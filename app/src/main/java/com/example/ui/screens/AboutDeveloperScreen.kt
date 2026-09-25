package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.GithubDeveloperProfile
import com.example.ui.components.DonationDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.UpdateDialog
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoLight
import com.example.util.UpdateManager
import com.example.viewmodel.BtebViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

// Hard-coded fallback with real live data from @anbuinfosec
private val OFFLINE_PROFILE = GithubDeveloperProfile(
    login = "anbuinfosec",
    name = "Mohammad Alamin",
    avatarUrl = "https://avatars.githubusercontent.com/u/135030867?v=4",
    bio = "Whispering in code's quiet hum, I craft light from shadows, building bridges where logic and dreams unite—worlds born line by line.",
    htmlUrl = "https://github.com/anbuinfosec",
    publicRepos = 111,
    followers = 188,
    following = 1,
    location = "Bangladesh",
    blog = "https://anbuinfosec.dev",
    company = "@AnbuSoft",
    twitterUsername = "anbuinfosec",
    hireable = true,
    createdAt = "2023-05-30T12:59:50Z"
)

private const val GITHUB_API = "https://api.github.com/users/anbuinfosec"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDeveloperScreen(
    viewModel: BtebViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var showDonationDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    val updateInfo by viewModel.updateInfoState.collectAsStateWithLifecycle()

    // Start with offline data, try fetching live from GitHub
    var profile by remember { mutableStateOf(OFFLINE_PROFILE) }
    var isLoadingProfile by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoadingProfile = true
        try {
            val json = withContext(Dispatchers.IO) {
                URL(GITHUB_API).readText(Charsets.UTF_8)
            }
            val obj = JSONObject(json)
            profile = GithubDeveloperProfile(
                login = obj.optString("login", OFFLINE_PROFILE.login),
                name = obj.optString("name").ifBlank { OFFLINE_PROFILE.name },
                avatarUrl = obj.optString("avatar_url").ifBlank { OFFLINE_PROFILE.avatarUrl },
                bio = obj.optString("bio").ifBlank { OFFLINE_PROFILE.bio },
                htmlUrl = obj.optString("html_url").ifBlank { OFFLINE_PROFILE.htmlUrl },
                publicRepos = obj.optInt("public_repos", OFFLINE_PROFILE.publicRepos ?: 111),
                followers = obj.optInt("followers", OFFLINE_PROFILE.followers ?: 188),
                following = obj.optInt("following", OFFLINE_PROFILE.following ?: 1),
                location = obj.optString("location").ifBlank { OFFLINE_PROFILE.location },
                blog = obj.optString("blog").ifBlank { OFFLINE_PROFILE.blog },
                company = obj.optString("company").ifBlank { OFFLINE_PROFILE.company },
                twitterUsername = obj.optString("twitter_username").ifBlank { OFFLINE_PROFILE.twitterUsername },
                hireable = if (obj.isNull("hireable")) OFFLINE_PROFILE.hireable else obj.optBoolean("hireable"),
                createdAt = obj.optString("created_at").ifBlank { OFFLINE_PROFILE.createdAt }
            )
        } catch (_: Exception) {
            // No internet — keep OFFLINE_PROFILE
        }
        isLoadingProfile = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About & Credits",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    if (onOpenDrawer != null) {
                        IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("about_drawer_button")) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    } else {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("about_back_button")) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("about_developer_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── App Banner ──────────────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.ic_bteb_logo),
                        contentDescription = "BTEB Logo",
                        modifier = Modifier.size(76.dp).clip(CircleShape)
                    )
                    Text(
                        text = "BTEB Result & Academic Portal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Version ${UpdateManager.CURRENT_VERSION} (Production Release)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Designed for 800+ polytechnic institutes & 500,000+ students across Bangladesh.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Developer Profile Card ───────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Developer — @${profile.login}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        if (isLoadingProfile) {
                            Spacer(Modifier.width(8.dp))
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        }
                    }
                    HorizontalDivider()

                    // Avatar + core info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            AsyncImage(
                                model = profile.avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                // Fallback to initials if image fails
                                error = painterResource(id = R.drawable.ic_bteb_logo)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = profile.name ?: "Mohammad Alamin",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            profile.company?.let { company ->
                                if (company.isNotBlank()) DevInfoRow(Icons.Default.Work, company)
                            }
                            profile.location?.let { loc ->
                                if (loc.isNotBlank()) DevInfoRow(Icons.Default.LocationOn, loc)
                            }
                            if (profile.hireable == true) {
                                Surface(
                                    color = EmeraldSuccess.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "✓ Available for hire",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Bio
                    profile.bio?.let { bio ->
                        if (bio.isNotBlank()) {
                            Text(
                                text = "\"$bio\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }

                    // GitHub stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GitStatChip("Repos", "${profile.publicRepos ?: 0}", Icons.Default.Code)
                        GitStatChip("Followers", "${profile.followers ?: 0}", Icons.Default.People)
                        GitStatChip("Following", "${profile.following ?: 0}", Icons.Default.StarBorder)
                    }

                    HorizontalDivider()

                    // Website link
                    profile.blog?.let { blog ->
                        if (blog.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        openUrl(context, if (blog.startsWith("http")) blog else "https://$blog")
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Language, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(blog, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Twitter/X link
                    profile.twitterUsername?.let { tw ->
                        if (tw.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { openUrl(context, "https://twitter.com/$tw") }
                                    .padding(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Share, null, Modifier.size(16.dp), tint = Color(0xFF1DA1F2))
                                Spacer(Modifier.width(8.dp))
                                Text("@$tw (X / Twitter)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1DA1F2))
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { openUrl(context, profile.htmlUrl ?: "https://github.com/anbuinfosec") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("GitHub")
                        }

                        Button(
                            onClick = { showDonationDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5F5F)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Favorite, null, Modifier.size(14.dp), tint = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text("Support ☕", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Website button
                    OutlinedButton(
                        onClick = { openUrl(context, "https://anbuinfosec.dev") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Language, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("anbuinfosec.dev")
                    }
                }
            }

            // ── Updates & Integrity ─────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Updates & Integrity",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "GitHub Release Channel",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = if (updateInfo?.isUpdateAvailable == true)
                                    "New version ${updateInfo?.latestVersion} available!"
                                else "You have the latest version",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (updateInfo?.isUpdateAvailable == true)
                                    EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.checkForUpdates()
                                if (updateInfo?.isUpdateAvailable == true) {
                                    showUpdateDialog = true
                                } else {
                                    Toast.makeText(context, "Checking for updates…", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.SystemUpdate, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Check")
                        }
                    }
                }
            }

            // ── Architecture Stack ──────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, null, Modifier.size(18.dp), tint = IndigoLight)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Architecture & Tech Stack",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    HorizontalDivider()
                    listOf(
                        "• 100% Kotlin 2.0+ & Jetpack Compose Material 3",
                        "• Room SQLite — instant offline caching",
                        "• Retrofit 2 & OkHttp 4 — REST API integration",
                        "• MVVM + StateFlow + Coroutines architecture",
                        "• ZXing — Bangla QR payment generation",
                        "• Coil — async image loading",
                        "• Open Source under MIT License"
                    ).forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            }

            // ── Donate section ──────────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Favorite, null, Modifier.size(28.dp), tint = Color(0xFFFF5F5F))
                    Text(
                        "Support the Developer",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "donate.anbuinfosec.dev",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { openUrl(context, "https://donate.anbuinfosec.dev") }
                    )
                    Button(
                        onClick = { showDonationDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5F5F)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Support Me ☕", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    if (showDonationDialog) {
        DonationDialog(onDismiss = { showDonationDialog = false })
    }

    if (showUpdateDialog && updateInfo != null) {
        UpdateDialog(updateInfo = updateInfo!!, onDismiss = { showUpdateDialog = false })
    }
}

@Composable
private fun DevInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GitStatChip(label: String, value: String, icon: ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(icon, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: Exception) {}
}
