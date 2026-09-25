package com.example.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

data class ScreenInfo(
    val widthDp: Dp,
    val heightDp: Dp,
    val densityDpi: Int,
    val fontScale: Float,
    val isCompact: Boolean,
    val isMedium: Boolean,
    val isExpanded: Boolean
)

@Composable
fun rememberScreenInfo(): ScreenInfo {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp.dp
    val heightDp = configuration.screenHeightDp.dp
    val densityDpi = configuration.densityDpi
    val fontScale = configuration.fontScale

    return remember(widthDp, heightDp, densityDpi, fontScale) {
        ScreenInfo(
            widthDp = widthDp,
            heightDp = heightDp,
            densityDpi = densityDpi,
            fontScale = fontScale,
            isCompact = widthDp < 380.dp,
            isMedium = widthDp in 380.dp..599.dp,
            isExpanded = widthDp >= 600.dp
        )
    }
}

/**
 * Returns an adaptive DP value based on the screen width classification.
 */
@Composable
fun adaptiveDp(compact: Dp, standard: Dp, expanded: Dp = standard * 1.25f): Dp {
    val screenInfo = rememberScreenInfo()
    return when {
        screenInfo.isExpanded -> expanded
        screenInfo.isCompact -> compact
        else -> standard
    }
}

/**
 * Returns an adaptive SP text size that smoothly adjusts to screen size and user font scale.
 */
@Composable
fun adaptiveSp(compact: TextUnit, standard: TextUnit, expanded: TextUnit = standard): TextUnit {
    val screenInfo = rememberScreenInfo()
    val base = when {
        screenInfo.isExpanded -> expanded
        screenInfo.isCompact -> compact
        else -> standard
    }
    // Prevent extreme fontScale blowout in dense cards while preserving accessibility
    return if (screenInfo.fontScale > 1.2f && screenInfo.isCompact) {
        (base.value * 0.95f).sp
    } else {
        base
    }
}

/**
 * Auto-resizing text composable that scales down font size automatically
 * if content would overflow the allotted container boundaries.
 */
@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minFontSize: TextUnit = 9.sp,
    step: Float = 0.5f
) {
    val initialFontSize = if (fontSize.isSpecified) fontSize else if (style.fontSize.isSpecified) style.fontSize else 14.sp
    var currentFontSize by remember(text, initialFontSize) { mutableStateOf(initialFontSize) }
    var readyToDraw by remember(text, initialFontSize) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        style = style,
        fontSize = currentFontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && currentFontSize.value > minFontSize.value) {
                val nextSize = (currentFontSize.value - step).coerceAtLeast(minFontSize.value)
                currentFontSize = nextSize.sp
            } else {
                readyToDraw = true
            }
        },
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        }
    )
}
