package co.bolo.app.ui.attendance

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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

/**
 * Standalone add-student screen. Used from Settings → Manage students,
 * and as a deeper alternative to the inline add field on the attendance
 * screen. Persists immediately on continue, then returns.
 */
@Composable
fun AddStudentScreen(
    cohortId: String,
    onBack: () -> Unit,
    onContinue: (name: String) -> Unit,
    vm: AttendanceViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        BoloTopBar(label = "Add a student", onBack = onBack)
        Spacer(Modifier.height(22.dp))
        BoloScreenTitle("What's their name?")
        Spacer(Modifier.height(22.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            BoloCaption("First name")
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
            "Names live on this phone only. Bolo never uploads them, and there's no voice fingerprint to forget later."
        )
        Spacer(Modifier.height(28.dp))
        val enabled = name.trim().isNotEmpty() && cohortId.isNotBlank()
        BoloSolidButton(
            label = "Save to cohort →",
            onClick = {
                if (!enabled) return@BoloSolidButton
                scope.launch {
                    vm.addStudent(name) {}
                    onContinue(name.trim())
                }
            },
            enabled = enabled
        )
    }
}
