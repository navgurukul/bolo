package co.bolo.app.ui.cohort

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloPrivacyNote
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.theme.BoloPalette

/**
 * Create a new cohort. Persisted immediately; the new cohort becomes
 * selectable on Home from the next render.
 */
@Composable
fun NewCohortScreen(
    onBack: () -> Unit,
    onCreated: (cohortId: String) -> Unit,
    vm: NewCohortViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        BoloTopBar(label = "New cohort", onBack = onBack)
        Spacer(Modifier.height(22.dp))
        BoloScreenTitle("What do you\ncall this group?")
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.Text(
            "Anything you'd recognise — \"Pune Batch 14\", \"Saturday English club\". You can rename later.",
            color = BoloPalette.InkMuted,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(22.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            BoloCaption("Cohort name")
            Spacer(Modifier.height(8.dp))
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = BoloPalette.Ink,
                    fontSize = 18.sp
                ),
                cursorBrush = SolidColor(BoloPalette.Ink),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BoloPalette.Ink.copy(alpha = 0.22f))
            )
        }
        Spacer(Modifier.height(18.dp))
        BoloPrivacyNote(
            "A cohort is just a list of names that live on this phone. Add students once — Bolo keeps them around for every session after."
        )
        Spacer(Modifier.height(28.dp))
        val enabled = name.trim().isNotEmpty()
        BoloSolidButton(
            label = "Create cohort →",
            onClick = {
                if (!enabled) return@BoloSolidButton
                vm.create(name) { id -> onCreated(id) }
            },
            enabled = enabled
        )
    }
}
