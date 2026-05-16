package co.bolo.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.ui.theme.SansUI
import co.bolo.app.ui.theme.SerifAccent

// ─────────────────────────────────────────────────────────────
// Buttons
// ─────────────────────────────────────────────────────────────

/** Filled black pill — the primary CTA across the app. */
@Composable
fun BoloSolidButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    danger: Boolean = false
) {
    val bg = when {
        !enabled -> BoloPalette.SurfaceMuted
        danger -> BoloPalette.MicRed
        else -> BoloPalette.Ink
    }
    val fg = when {
        !enabled -> BoloPalette.InkFaint
        else -> BoloPalette.Bg
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = fg, style = MaterialTheme.typography.titleMedium)
    }
}

/** Ghost = surface bg with hairline border. */
@Composable
fun BoloGhostButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BoloPalette.Surface)
            .border(BorderStroke(1.dp, BoloPalette.Hairline), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = BoloPalette.Ink, style = MaterialTheme.typography.titleMedium)
    }
}

/** Quiet = transparent, muted text. */
@Composable
fun BoloQuietButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = BoloPalette.InkMuted, style = MaterialTheme.typography.titleMedium)
    }
}

// ─────────────────────────────────────────────────────────────
// Avatar — initial-in-circle, active variant with sage ring
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloAvatar(
    name: String,
    size: Dp = 36.dp,
    active: Boolean = false,
    modifier: Modifier = Modifier
) {
    val initial = name.firstOrNull()?.uppercase() ?: ""
    val ringColor = if (active) BoloPalette.Sage else BoloPalette.Hairline
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BoloPalette.SurfaceMuted)
            .border(if (active) 2.dp else 1.dp, ringColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initial,
            color = BoloPalette.Ink,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Top bar — back chevron + uppercase mono label
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloTopBar(
    label: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(1.dp, BoloPalette.Hairline, CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text("‹", color = BoloPalette.Ink, fontSize = 22.sp, fontWeight = FontWeight.Normal)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            label.uppercase(),
            color = BoloPalette.InkFaint,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = MonoData
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Caption — uppercase mono small text
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloCaption(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = BoloPalette.InkFaint
) {
    Text(
        text.uppercase(),
        modifier = modifier,
        color = color,
        fontFamily = MonoData,
        fontSize = 10.5.sp,
        letterSpacing = 1.4.sp
    )
}

// ─────────────────────────────────────────────────────────────
// Screen title — big editorial heading with optional eyebrow
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    textAlign: TextAlign = TextAlign.Start
) {
    if (eyebrow != null) {
        BoloCaption(eyebrow)
        Spacer(Modifier.height(10.dp))
    }
    Text(
        text,
        modifier = modifier.fillMaxWidth(),
        color = BoloPalette.Ink,
        fontFamily = SansUI,
        fontWeight = FontWeight.Medium,
        fontSize = 38.sp,
        lineHeight = 42.sp,
        letterSpacing = (-1).sp,
        textAlign = textAlign
    )
}

// ─────────────────────────────────────────────────────────────
// Card — surface + hairline; accent variant uses sage soft
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloCard(
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    content: @Composable () -> Unit
) {
    val bg = if (accent) BoloPalette.SageSoft else BoloPalette.Surface
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .then(
                if (!accent)
                    Modifier.border(1.dp, BoloPalette.Hairline, RoundedCornerShape(20.dp))
                else Modifier
            )
            .padding(20.dp)
    ) { content() }
}

// ─────────────────────────────────────────────────────────────
// Delta pill — ↑/↓ N pts
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloDelta(value: Int, modifier: Modifier = Modifier) {
    val pos = value >= 0
    val bg = if (pos) BoloPalette.SageSoft else BoloPalette.SurfaceMuted
    val fg = if (pos) BoloPalette.SageDeep else BoloPalette.InkMuted
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (pos) "↑" else "↓",
            color = fg,
            fontFamily = MonoData,
            fontSize = 12.sp
        )
        Spacer(Modifier.width(4.dp))
        Text(
            "${kotlin.math.abs(value)} pts",
            color = fg,
            fontFamily = MonoData,
            fontSize = 12.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Secondary metric row — label / mono value, hairline bottom
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloSecondaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = BoloPalette.InkMuted, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = BoloPalette.Ink, fontFamily = MonoData, fontSize = 14.sp)
    }
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BoloPalette.Hairline)
    )
}

// ─────────────────────────────────────────────────────────────
// Section label — left-aligned small heading
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloSectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        modifier = modifier,
        color = BoloPalette.InkMuted,
        fontFamily = SansUI,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
}

// ─────────────────────────────────────────────────────────────
// Privacy note — small muted line with shield glyph
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloPrivacyNote(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Text(
            "🛡",
            color = BoloPalette.InkFaint,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 1.dp, end = 8.dp)
        )
        Text(
            text,
            color = BoloPalette.InkFaint,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Italic accent text — reuses SerifAccent for the "human moments"
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloItalicAccent(
    text: String,
    fontSize: Int = 22,
    modifier: Modifier = Modifier,
    color: Color = BoloPalette.Ink
) {
    Text(
        text,
        modifier = modifier,
        color = color,
        fontFamily = SerifAccent,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 1.3).sp,
        letterSpacing = (-0.3).sp
    )
}

// ─────────────────────────────────────────────────────────────
// Synced pill — sage-soft chip with mono "SYNCED"
// ─────────────────────────────────────────────────────────────
@Composable
fun BoloSyncedPill(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(BoloPalette.SageSoft)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(BoloPalette.Sage)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "SYNCED",
            color = BoloPalette.SageDeep,
            fontFamily = MonoData,
            fontSize = 10.5.sp,
            letterSpacing = 0.6.sp
        )
    }
}
