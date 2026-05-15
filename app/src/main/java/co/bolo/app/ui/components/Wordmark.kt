package co.bolo.app.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.SerifAccent

/** One of the three "human moments" that use Newsreader italic. */
@Composable
fun Wordmark(modifier: Modifier = Modifier) {
    Text(
        text = "Bolo.",
        modifier = modifier,
        color = BoloPalette.Ink,
        fontFamily = SerifAccent,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    )
}
