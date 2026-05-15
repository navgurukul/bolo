package co.bolo.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Phase 0: bundled fonts deferred until Phase 4 polish. Use system defaults but match the
// design's role separation: SansUI (Geist substitute), SerifAccent (Newsreader italic
// substitute) for human moments, MonoData (Geist Mono substitute) for numerals.
// When we wire real font files, swap the FontFamily values below — call sites stay stable.
val SansUI: FontFamily = FontFamily.SansSerif
val SerifAccent: FontFamily = FontFamily.Serif
val MonoData: FontFamily = FontFamily.Monospace

val BoloTypography = Typography(
    displayLarge = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Light, fontSize = 48.sp, letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Normal, fontSize = 36.sp, letterSpacing = (-0.4).sp),
    headlineLarge = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Normal, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Medium, fontSize = 22.sp),
    titleLarge = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Medium, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Medium, fontSize = 15.sp),
    bodyLarge = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(fontFamily = SansUI, fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.4.sp),
    labelSmall = TextStyle(fontFamily = MonoData, fontWeight = FontWeight.Normal, fontSize = 11.sp, letterSpacing = 1.2.sp)
)

// Reserved for the three human moments: wordmark, read-aloud sentence, reflection prompt.
val AccentItalic = TextStyle(
    fontFamily = SerifAccent,
    fontStyle = FontStyle.Italic,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 32.sp
)
