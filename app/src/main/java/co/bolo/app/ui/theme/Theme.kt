package co.bolo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BoloLightColors = lightColorScheme(
    primary = BoloPalette.Sage,
    onPrimary = BoloPalette.Surface,
    primaryContainer = BoloPalette.SageSoft,
    onPrimaryContainer = BoloPalette.SageDeep,
    secondary = BoloPalette.InkMuted,
    onSecondary = BoloPalette.Surface,
    background = BoloPalette.Bg,
    onBackground = BoloPalette.Ink,
    surface = BoloPalette.Surface,
    onSurface = BoloPalette.Ink,
    surfaceVariant = BoloPalette.SurfaceMuted,
    onSurfaceVariant = BoloPalette.InkMuted,
    outline = BoloPalette.Hairline,
    outlineVariant = BoloPalette.Hairline,
    error = BoloPalette.MicRed,
    onError = BoloPalette.Surface
)

@Composable
fun BoloTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Whisper palette is light-only. Dark mode deferred — see DECISIONS.md.
    MaterialTheme(
        colorScheme = BoloLightColors,
        typography = BoloTypography,
        content = content
    )
}
