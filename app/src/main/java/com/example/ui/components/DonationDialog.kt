package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ── URLs ────────────────────────────────────────────────────────────────────
private const val DONATE_SITE_URL  = "https://donate.anbuinfosec.dev"
private const val DONATE_EMBED_URL = "https://donate.anbuinfosec.dev/embed"

// ── Bangla QR (EMVCo / BQR format) — any Bangladeshi bank / MFS ────────────
private const val BANGLA_QR_DATA =
    "00020101021126500009com.bkash010202020420020319920022101615827704252047372530305" +
    "05802BD5918TextLy 016158277046015Purbo boidonath62260211016158277040807PAYMENT630466A7"

/**
 * Donation dialog: tapping "Support Me ☕" directly opens the WebView payment widget.
 * Also shows the Bangla QR code for scanning with any Bangladeshi bank / MFS app.
 */
@Composable
fun DonationDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    // Open the web payment directly — no intermediate sheet
    DonationWebPaymentDialog(onDismiss = onDismiss)
}

/**
 * Full-screen WebView dialog loading donate.anbuinfosec.dev/embed (the widget endpoint).
 * Matches the <script> widget: data-site-url, data-text, data-color, data-position.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DonationWebPaymentDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var showQr by remember { mutableStateOf(false) }

    if (showQr) {
        BanglaQrDialog(onDismiss = { showQr = false })
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.93f)
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Header bar ──────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color(0xFFFF5F5F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text("☕", fontSize = 15.sp) }
                        Column {
                            Text(
                                "Support Me ☕",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "donate.anbuinfosec.dev",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Bangla QR button
                        IconButton(onClick = { showQr = true }) {
                            Icon(
                                Icons.Default.QrCode2,
                                contentDescription = "Bangla QR",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Open in browser
                        IconButton(onClick = {
                            try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DONATE_SITE_URL))) } catch (_: Exception) {}
                        }) {
                            Icon(Icons.Default.OpenInBrowser, "Open in Browser", tint = MaterialTheme.colorScheme.primary)
                        }
                        // Close
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Close")
                        }
                    }
                }

                // Loading progress bar
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFF5F5F)
                    )
                }

                // ── WebView ─────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    loadsImagesAutomatically = true
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                    builtInZoomControls = false
                                    displayZoomControls = false
                                    cacheMode = WebSettings.LOAD_DEFAULT
                                    // Identify as the app so widget can optimise layout
                                    userAgentString = "${settings.userAgentString} BtebResultApp/2.0"
                                }
                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                        isLoading = true
                                    }
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        isLoading = false
                                    }
                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val url = request?.url?.toString() ?: return false
                                        // Hand off deep-links, phone calls, emails to system
                                        if (url.startsWith("intent:") ||
                                            url.startsWith("tel:") ||
                                            url.startsWith("mailto:") ||
                                            url.startsWith("bkash:") ||
                                            url.startsWith("nagad:") ||
                                            url.startsWith("rocket:")
                                        ) {
                                            try {
                                                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                                return true
                                            } catch (_: Exception) {}
                                        }
                                        return false
                                    }
                                }
                                loadUrl(DONATE_EMBED_URL)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Full-screen Bangla QR dialog — works with any Bangladeshi bank or MFS
 * (bKash, Nagad, Rocket, Dutch-Bangla, DBBL, etc.)
 */
@Composable
fun BanglaQrDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "বাংলা QR পেমেন্ট",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            "যেকোনো ব্যাংক বা MFS অ্যাপ দিয়ে স্ক্যান করুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                // QR Code (generated via ZXing — no static image)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QrCodeImage(
                        content = BANGLA_QR_DATA,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        foregroundColor = android.graphics.Color.rgb(10, 20, 80),
                        backgroundColor = android.graphics.Color.WHITE
                    )
                }

                // Bank / MFS logos hint row
                Text(
                    text = "Pay easily with any supported Bangla QR payment app.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Merchant info
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            "TextLy",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "01615827704",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
