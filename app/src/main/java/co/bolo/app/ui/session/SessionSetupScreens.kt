package co.bolo.app.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.components.BoloAvatar
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloPrivacyNote
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.ui.theme.SansUI

/**
 * Step 1 of 2 — pick the participant count.
 *
 * Replaces the prior Material text-input form with the Bolo design system's
 * tactile stepper + quick-pick chip pattern (see bolo-screens-extra.jsx,
 * ParticipantCountScreen).
 */
@Composable
fun ParticipantCountScreen(
    vm: SessionViewModel,
    onNext: (Int) -> Unit,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var count by remember { mutableStateOf(state.participantCount.takeIf { it in 2..10 } ?: 5) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 28.dp)
    ) {
        item {
            BoloTopBar(label = "New session · step 1 of 2", onBack = onBack)
            Spacer(Modifier.height(22.dp))
            BoloScreenTitle(text = "A small number\nkeeps things honest.", eyebrow = "How many of us are here?")
            Spacer(Modifier.height(14.dp))
            Text(
                "Bolo works best with two to ten people in the room. We'll ask for names on the next screen — or use the ones you've already enrolled.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(36.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepperButton(symbol = "−", enabled = count > 2) { count = (count - 1).coerceAtLeast(2) }
                Spacer(Modifier.width(26.dp))
                Box(
                    modifier = Modifier.width(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        color = BoloPalette.Ink,
                        fontFamily = SansUI,
                        fontWeight = FontWeight.Normal,
                        fontSize = 120.sp,
                        letterSpacing = (-5).sp,
                        lineHeight = 110.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.width(26.dp))
                StepperButton(symbol = "+", enabled = count < 10) { count = (count + 1).coerceAtMost(10) }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (count == 1) "PERSON IN THE CIRCLE" else "PEOPLE IN THE CIRCLE",
                color = BoloPalette.InkFaint,
                fontFamily = MonoData,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))
        }

        item {
            BoloCaption("Or pick a usual size")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(3, 5, 6, 8, 10).forEach { n ->
                    QuickPickChip(value = n, selected = n == count) { count = n }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        item {
            BoloSolidButton(label = "Next: name everyone →", onClick = {
                vm.setParticipantCount(count)
                onNext(count)
            })
            Spacer(Modifier.height(14.dp))
            BoloPrivacyNote(
                "Names live only on this phone. They're never sent anywhere, never tied to audio."
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StepperButton(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) BoloPalette.Surface else BoloPalette.Bg
    val ring = if (enabled) BoloPalette.InkMuted.copy(alpha = 0.22f) else BoloPalette.Hairline
    val fg = if (enabled) BoloPalette.Ink else BoloPalette.InkFaint
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, ring, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            symbol,
            color = fg,
            fontFamily = SansUI,
            fontWeight = FontWeight.Light,
            fontSize = 28.sp,
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun QuickPickChip(value: Int, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) BoloPalette.Ink else BoloPalette.Surface
    val fg = if (selected) BoloPalette.Bg else BoloPalette.Ink
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .then(if (selected) Modifier else Modifier.border(1.dp, BoloPalette.Hairline, RoundedCornerShape(999.dp)))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(value.toString(), color = fg, style = MaterialTheme.typography.titleMedium)
    }
}

/**
 * Step 2 of 2 — name each participant.
 *
 * Replaces the prior stacked Material Card + OutlinedTextField rows with the
 * Bolo bottom-bordered field-list pattern (see bolo-screens-extra.jsx,
 * ParticipantNamesScreen). Numbered rows with optional avatar when the typed
 * name matches an enrolled student.
 */
@Composable
fun NameEditingScreen(
    vm: SessionViewModel,
    onStartSession: () -> Unit,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val partCount = state.setupNames.size.takeIf { it > 0 } ?: state.participantCount
    val enrolledNames = remember(state.students) {
        state.students.map { it.displayName }.distinct()
    }
    val filled = state.setupNames.count { it.isNotBlank() }
    val allFilled = filled == partCount && partCount > 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 28.dp)
    ) {
        item {
            BoloTopBar(label = "New session · step 2 of 2", onBack = onBack)
            Spacer(Modifier.height(22.dp))
            BoloScreenTitle(text = "Who's here\nby name?", eyebrow = "$partCount people in the room")
            Spacer(Modifier.height(12.dp))
            Text(
                "First name is enough. If they're already enrolled, their voice trend will pick up.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(18.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (enrolledNames.isNotEmpty()) {
                    AccentChip(label = "Use enrolled cohort") {
                        enrolledNames.take(partCount).forEachIndexed { i, n -> vm.updateSetupName(i, n) }
                    }
                }
                GhostChip(label = "Clear all") {
                    repeat(partCount) { i -> vm.updateSetupName(i, "") }
                }
            }
            Spacer(Modifier.height(22.dp))
        }

        itemsIndexed(state.setupNames) { index, name ->
            val matched = enrolledNames.firstOrNull { it.equals(name.trim(), ignoreCase = true) }
            NameRow(
                index = index,
                value = name,
                matchedName = matched,
                onChange = { vm.updateSetupName(index, it) }
            )
        }

        item {
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$filled of $partCount named",
                    color = BoloPalette.InkFaint,
                    fontFamily = MonoData,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
                val enrolledCount = state.setupNames.count { n ->
                    enrolledNames.any { it.equals(n.trim(), ignoreCase = true) }
                }
                Text(
                    "$enrolledCount enrolled",
                    color = BoloPalette.InkFaint,
                    fontFamily = MonoData,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(Modifier.height(22.dp))
            BoloSolidButton(
                label = if (allFilled) "Start session →"
                        else "Add ${partCount - filled} more ${if (partCount - filled == 1) "name" else "names"}",
                onClick = {
                    if (allFilled) {
                        vm.finalizeParticipants()
                        onStartSession()
                    }
                },
                enabled = allFilled
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NameRow(
    index: Int,
    value: String,
    matchedName: String?,
    onChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "%02d".format(index + 1),
                color = BoloPalette.InkFaint,
                fontFamily = MonoData,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.width(28.dp)
            )
            if (matchedName != null) {
                BoloAvatar(name = matchedName, size = 32.dp)
            } else {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(1.dp, BoloPalette.Hairline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = BoloPalette.InkFaint, fontFamily = SansUI, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            BasicTextField(
                value = value,
                onValueChange = onChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    color = BoloPalette.Ink,
                    fontFamily = SansUI,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                cursorBrush = SolidColor(BoloPalette.Ink),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            "Name",
                            color = BoloPalette.InkFaint,
                            fontFamily = SansUI,
                            fontSize = 16.sp
                        )
                    }
                    inner()
                }
            )
            if (matchedName != null) {
                Spacer(Modifier.width(8.dp))
                Text(
                    "ENROLLED",
                    color = BoloPalette.SageDeep,
                    fontFamily = MonoData,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BoloPalette.Hairline)
        )
    }
}

@Composable
private fun AccentChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(BoloPalette.SageSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, color = BoloPalette.SageDeep, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun GhostChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, color = BoloPalette.InkMuted, style = MaterialTheme.typography.labelLarge)
    }
}
