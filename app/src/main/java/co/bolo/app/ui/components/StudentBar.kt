package co.bolo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.util.Format

@Composable
fun StudentBar(
    name: String,
    share: Float,
    speechMs: Long,
    isTop: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, style = MaterialTheme.typography.titleMedium, color = BoloPalette.Ink)
                if (isTop) {
                    Text(
                        " · most English today",
                        style = MaterialTheme.typography.labelSmall,
                        color = BoloPalette.SageDeep
                    )
                }
            }
            Text(
                Format.percent(share),
                style = MaterialTheme.typography.titleMedium,
                color = if (isTop) BoloPalette.SageDeep else BoloPalette.Ink
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BoloPalette.SurfaceMuted)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(share.coerceIn(0f, 1f))
                    .height(6.dp)
                    .background(if (isTop) BoloPalette.SageDeep else BoloPalette.Sage)
            )
        }
        Text(
            Format.minutes(speechMs) + " of speaking",
            style = MaterialTheme.typography.labelSmall,
            color = BoloPalette.InkFaint,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
