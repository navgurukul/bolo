package co.bolo.app.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.theme.BoloPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantCountScreen(
    vm: SessionViewModel,
    onNext: (Int) -> Unit,
    onBack: () -> Unit
) {
    var countText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .padding(horizontal = 22.dp, vertical = 28.dp)
    ) {
        Text(
            "Back",
            modifier = Modifier.clickable(onClick = onBack),
            style = MaterialTheme.typography.labelLarge,
            color = BoloPalette.InkMuted
        )
        Spacer(Modifier.height(28.dp))
        Text(
            "Setup Session",
            style = MaterialTheme.typography.headlineMedium,
            color = BoloPalette.Ink
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "How many people are joining this session?",
            style = MaterialTheme.typography.bodyMedium,
            color = BoloPalette.InkMuted
        )
        Spacer(Modifier.height(24.dp))

        TextField(
            value = countText,
            onValueChange = {
                if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 2)) {
                    countText = it
                    errorMessage = null
                }
            },
            placeholder = { Text("e.g. 5") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null,
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BoloPalette.Surface,
                unfocusedContainerColor = BoloPalette.Surface,
                errorContainerColor = BoloPalette.Surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                cursorColor = BoloPalette.Ink
            )
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BoloPalette.Ink)
                .clickable {
                    val count = countText.toIntOrNull()
                    if (count != null && count in 3..10) {
                        vm.setParticipantCount(count)
                        onNext(count)
                    } else {
                        errorMessage = "Please enter a number between 3 and 10"
                    }
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Next", color = BoloPalette.Bg, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEditingScreen(
    vm: SessionViewModel,
    onStartSession: () -> Unit,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .padding(horizontal = 22.dp, vertical = 28.dp)
    ) {
        Text(
            "Back",
            modifier = Modifier.clickable(onClick = onBack),
            style = MaterialTheme.typography.labelLarge,
            color = BoloPalette.InkMuted
        )
        Spacer(Modifier.height(28.dp))
        Text(
            "Participant Names",
            style = MaterialTheme.typography.headlineMedium,
            color = BoloPalette.Ink
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Assign names to each participant for tracking.",
            style = MaterialTheme.typography.bodyMedium,
            color = BoloPalette.InkMuted
        )
        Spacer(Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(state.setupNames) { index, name ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = BoloPalette.Surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BoloPalette.Hairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Participant ${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BoloPalette.InkFaint
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { vm.updateSetupName(index, it) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BoloPalette.Sage,
                                unfocusedBorderColor = BoloPalette.Hairline,
                                cursorColor = BoloPalette.Ink
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BoloPalette.Ink)
                .clickable {
                    vm.finalizeParticipants()
                    onStartSession()
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Start Session", color = BoloPalette.Bg, style = MaterialTheme.typography.titleMedium)
        }
    }
}
