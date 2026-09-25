package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RoseReferred

@Composable
fun ErrorStateView(
    message: String,
    onRetry: (() -> Unit)? = null,
    onAlternativeAction: (() -> Unit)? = null,
    alternativeActionText: String = "Try Different Search",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(message) {
        // Subtle error shake animation
        shakeOffset.animateTo(
            targetValue = 12f,
            animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = -12f,
            animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = 6f,
            animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing)
        )
    }

    val isNetworkError = message.contains("connection", ignoreCase = true) ||
            message.contains("network", ignoreCase = true) ||
            message.contains("timed out", ignoreCase = true) ||
            message.contains("unable to resolve", ignoreCase = true)

    val isNotFound = message.contains("not found", ignoreCase = true) ||
            message.contains("no result", ignoreCase = true)

    val errorTitle = when {
        isNotFound -> "Result Record Not Found"
        isNetworkError -> "Connection Timeout"
        else -> "Server Communication Notice"
    }

    val iconVector = when {
        isNotFound -> Icons.Default.SearchOff
        isNetworkError -> Icons.Default.CloudOff
        else -> Icons.Default.ErrorOutline
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated icon badge
        Box(
            modifier = Modifier
                .offset(x = shakeOffset.value.dp)
                .size(76.dp)
                .clip(CircleShape)
                .background(RoseReferred.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = "Error",
                tint = RoseReferred,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = errorTitle,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Troubleshooting & Hints Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Troubleshooting Tips",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isNotFound) {
                    Text(
                        text = "• Double-check your 6-digit board Roll number.\n" +
                                "• Ensure you selected the matching Exam (e.g. Diploma in Engineering).\n" +
                                "• If you are an older batch, check regulation or try again during board publishing hours.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                } else {
                    Text(
                        text = "• Verify your internet / Wi-Fi connection.\n" +
                                "• BTEB servers may experience peak load during publishing rush.\n" +
                                "• You can still access Booklists & Syllabuses offline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onRetry != null) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("error_retry_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retry")
                }
            }

            if (onAlternativeAction != null) {
                OutlinedButton(
                    onClick = onAlternativeAction,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(alternativeActionText, maxLines = 1)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Copy error details
        OutlinedButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("BTEB Error Log", message)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Error message copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy Error Details", fontSize = 12.sp)
        }
    }
}
