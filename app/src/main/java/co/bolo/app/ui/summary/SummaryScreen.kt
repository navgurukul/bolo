package co.bolo.app.ui.summary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.data.model.TranscriptChunk
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.components.StaticEnglishHalo
import co.bolo.app.ui.theme.AccentItalic
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.util.ClassificationReason
import co.bolo.app.util.Format
import co.bolo.app.util.TokenAnalysis

private const val REFLECTION = "Which moment did you switch — and what word was missing?"

@Composable
fun SummaryScreen(
    sessionId: String,
    onDone: () -> Unit,
    onOpenStudent: (String) -> Unit,
    vm: SummaryViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val session = state.session
    var isTranscriptExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SESSION SUMMARY", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                Text(
                    session?.let { Format.relativeDay(it.startedAt) } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = BoloPalette.InkFaint
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                session?.topic.orEmpty(),
                style = MaterialTheme.typography.headlineLarge,
                color = BoloPalette.Ink
            )
        }

        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StaticEnglishHalo(share = session?.englishShare ?: 0f, diameter = 200.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        Format.percent(session?.englishShare ?: 0f),
                        style = MaterialTheme.typography.displayLarge,
                        color = BoloPalette.Ink
                    )
                    Text(
                        "English Usage",
                        style = MaterialTheme.typography.labelMedium,
                        color = BoloPalette.InkFaint
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            val total = session?.totalSpeechMs ?: 0L
            Text(
                "Analyzed ${session?.chunksProcessed ?: 0} speech chunks over ${Format.minutes(total)}",
                style = MaterialTheme.typography.bodyMedium,
                color = BoloPalette.InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- TRANSCRIPT ANALYSIS SECTION (Expandable) ---
        item {
            TranscriptAnalysisHeader(
                expanded = isTranscriptExpanded,
                onToggle = { isTranscriptExpanded = !isTranscriptExpanded }
            )
        }

        if (isTranscriptExpanded) {
            item {
                AnalysisSummaryCard(state)
            }
            
            item {
                Text(
                    "CHUNK BREAKDOWN",
                    style = MaterialTheme.typography.labelSmall,
                    color = BoloPalette.InkFaint,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(state.chunks) { chunk ->
                ChunkDetailItem(chunk)
            }

            item {
                ClassificationSamples(state.tokenBreakdown)
            }

            if (state.unknownWords.isNotEmpty()) {
                item {
                    UnknownWordsSection(state.unknownWords)
                }
            }
        }

        item {
            Hairline()
            Spacer(Modifier.height(4.dp))
            Text("PARTICIPANTS", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
            Spacer(Modifier.height(8.dp))
            val names = state.rows.joinToString(", ") { it.student.displayName }
            Text(
                names.ifBlank { "Group Session" },
                style = MaterialTheme.typography.bodyLarge,
                color = BoloPalette.Ink
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BoloPalette.Surface)
                    .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(18.dp))
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text("30-SECOND REFLECTION", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "“$REFLECTION”",
                        style = AccentItalic,
                        color = BoloPalette.Ink,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BoloPalette.Ink)
                    .clickable(onClick = onDone)
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Done", color = BoloPalette.Bg, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "The conversation transcript has been saved locally for internal analysis.",
                style = MaterialTheme.typography.bodySmall,
                color = BoloPalette.InkFaint,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TranscriptAnalysisHeader(expanded: Boolean, onToggle: () -> Unit) {
    Column {
        Hairline()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TRANSCRIPT ANALYSIS (DEBUG)",
                style = MaterialTheme.typography.labelSmall,
                color = if (expanded) BoloPalette.Ink else BoloPalette.InkFaint
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = BoloPalette.InkFaint
            )
        }
    }
}

@Composable
private fun AnalysisSummaryCard(state: SummaryUiState) {
    val totalChunks = state.chunks.size
    val totalEnglish = state.chunks.sumOf { it.englishCount }
    val totalMeaningful = state.chunks.sumOf { it.meaningfulCount }
    val totalFiller = state.chunks.sumOf { it.fillerCount }
    val totalRawWords = state.chunks.sumOf { it.rawText.split("\\s+".toRegex()).count { s -> s.isNotBlank() } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BoloPalette.SurfaceMuted)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Calculation Summary", style = MaterialTheme.typography.titleSmall, color = BoloPalette.Ink)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Chunks", style = MaterialTheme.typography.bodySmall, color = BoloPalette.InkMuted)
                Text("$totalChunks", style = MaterialTheme.typography.bodySmall, color = BoloPalette.Ink)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Raw Word Count", style = MaterialTheme.typography.bodySmall, color = BoloPalette.InkMuted)
                Text("$totalRawWords", style = MaterialTheme.typography.bodySmall, color = BoloPalette.Ink)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Meaningful Tokens", style = MaterialTheme.typography.bodySmall, color = BoloPalette.InkMuted)
                Text("$totalMeaningful", style = MaterialTheme.typography.bodySmall, color = BoloPalette.Ink)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("English Words", style = MaterialTheme.typography.bodySmall, color = BoloPalette.InkMuted)
                Text("$totalEnglish", style = MaterialTheme.typography.bodySmall, color = BoloPalette.Ink)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Filler Count", style = MaterialTheme.typography.bodySmall, color = BoloPalette.InkMuted)
                Text("$totalFiller", style = MaterialTheme.typography.bodySmall, color = BoloPalette.Ink)
            }

            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BoloPalette.Bg.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text("FORMULA", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint, fontSize = 9.sp)
                    Text(
                        "Score = (English / Meaningful) × 100",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = BoloPalette.Ink
                    )
                    val percentStr = if (totalMeaningful > 0) {
                        Format.percent(totalEnglish.toFloat() / totalMeaningful.toFloat())
                    } else "0%"
                    Text(
                        "Score = ($totalEnglish / $totalMeaningful) × 100 = $percentStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = BoloPalette.SageDeep
                    )
                }
            }
        }
    }
}

@Composable
private fun ChunkDetailItem(chunk: TranscriptChunk) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BoloPalette.Surface)
            .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Chunk #${chunk.sequence + 1}", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
            val chunkShare = if (chunk.meaningfulCount > 0) chunk.englishCount.toFloat() / chunk.meaningfulCount else 0f
            Text(
                "${Format.percent(chunkShare)} English",
                style = MaterialTheme.typography.labelSmall,
                color = if (chunkShare > 0.5f) BoloPalette.SageDeep else BoloPalette.InkMuted
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "\"${chunk.rawText}\"",
            style = MaterialTheme.typography.bodyMedium,
            color = BoloPalette.Ink
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Cleaned: ${chunk.cleanedText}",
            style = MaterialTheme.typography.bodySmall,
            color = BoloPalette.InkMuted,
            lineHeight = 16.sp
        )
        Text(
            text = "Tokens: ${chunk.meaningfulCount} meaningful, ${chunk.englishCount} English, ${chunk.fillerCount} filler",
            style = MaterialTheme.typography.labelSmall,
            color = BoloPalette.InkFaint,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ClassificationSamples(tokens: List<TokenAnalysis>) {
    if (tokens.isEmpty()) return
    
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text("CLASSIFICATION SAMPLES", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            tokens.forEach { token ->
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            token.text,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(100.dp)
                        )
                        Text("→", modifier = Modifier.padding(horizontal = 8.dp), color = BoloPalette.InkFaint)
                        val label = when {
                            token.isEnglish -> "English"
                            token.isFiller -> "Filler"
                            token.isIgnored -> "Ignored"
                            else -> "Non-English"
                        }
                        val color = when {
                            token.isEnglish -> BoloPalette.SageDeep
                            token.isFiller || token.isIgnored -> BoloPalette.InkFaint
                            else -> BoloPalette.MicRed
                        }
                        Text(label, style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Medium)
                    }
                    Text(
                        "Reason: ${token.reason.description}${if (token.stem != null) " (Stem: ${token.stem})" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BoloPalette.InkMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun UnknownWordsSection(unknown: List<Pair<String, Int>>) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("UNKNOWN / UNCLASSIFIED WORDS", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BoloPalette.SurfaceMuted)
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                unknown.forEach { (word, count) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(word, style = MaterialTheme.typography.bodySmall, color = BoloPalette.Ink)
                        Text("$count times", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                    }
                }
            }
        }
    }
}
