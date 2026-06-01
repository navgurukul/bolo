package co.bolo.app.update

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import co.bolo.app.ui.components.BoloGhostButton
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.theme.BoloPalette

@Composable
fun UpdateAvailableDialog(
    available: UpdateState.Available,
    onInstall: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Ink.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BoloPalette.Surface)
                .clickable(enabled = false) { }
                .padding(26.dp)
        ) {
            Text(
                "Bolo ${available.versionName} is available",
                color = BoloPalette.Ink,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Tap Update to download and install. Android will ask you to confirm.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            if (available.notes.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .heightIn(max = 220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BoloPalette.SurfaceMuted)
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        available.notes,
                        color = BoloPalette.InkMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(22.dp))
            BoloSolidButton("Update now", onClick = onInstall)
            Spacer(Modifier.height(8.dp))
            BoloGhostButton("Later", onClick = onDismiss)
        }
    }
}
