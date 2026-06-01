package co.bolo.app.ui.firstrun

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloCard
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.theme.BoloPalette

@Composable
fun CohortSetupScreen(
    onReady: () -> Unit
) {
    var name by remember { mutableStateOf("Pune · Batch 14") }
    var facil by remember { mutableStateOf("Anjali") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        Spacer(Modifier.height(24.dp))
        BoloCaption("Step 4 of 4 · Name your cohort")
        Spacer(Modifier.height(10.dp))
        BoloScreenTitle("Last thing —\nwhat shall we call this group?")

        Spacer(Modifier.height(28.dp))
        UnderlinedField(label = "Cohort name", value = name, onChange = { name = it })
        Spacer(Modifier.height(18.dp))
        UnderlinedField(label = "Your name (facilitator)", value = facil, onChange = { facil = it })

        Spacer(Modifier.height(22.dp))
        BoloCard {
            Column {
                BoloCaption("What happens next")
                Spacer(Modifier.height(8.dp))
                Text(
                    "On the home screen, you'll add each student and record their ten-second voice fingerprint. After that, three taps starts a session.",
                    color = BoloPalette.InkMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        BoloSolidButton("We're ready →", onClick = onReady)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun UnderlinedField(label: String, value: String, onChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BoloCaption(label)
        Spacer(Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = BoloPalette.Ink,
                fontSize = 18.sp
            ),
            cursorBrush = SolidColor(BoloPalette.Ink),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BoloPalette.Ink.copy(alpha = 0.22f))
        )
    }
}
