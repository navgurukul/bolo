package co.bolo.app.ui.summary

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.analysis.ClassificationSource
import co.bolo.app.analysis.ClassificationType
import co.bolo.app.analysis.TokenAnalysis
import co.bolo.app.data.model.TranscriptChunk
import co.bolo.app.ui.components.BoloAvatar
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloCard
import co.bolo.app.ui.components.BoloItalicAccent
import co.bolo.app.ui.components.BoloPrivacyNote
import co.bolo.app.ui.components.BoloQuietButton
import co.bolo.app.ui.components.BoloSectionLabel
import co.bolo.app.ui.components.BoloSecondaryMetric
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.components.StaticEnglishHalo
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.ui.theme.SansUI
import co.bolo.app.ui.theme.SerifAccent
import co.bolo.app.util.Format
import kotlin.math.roundToInt

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
    var open by remember { mutableStateOf(false) }

    val totalEnglish = state.chunks.sumOf { it.englishCount }
    val totalMeaningful = state.chunks.sumOf { it.meaningfulCount }
    val totalFiller = state.chunks.sumOf { it.fillerCount }
    val totalRawWords = state.chunks.sumOf {
        it.rawText.split(Regex("\\s+")).count { s -> s.isNotBlank() }
    }
    val share = session?.englishShare ?: 0f
    val sharePct = (share * 100f).roundToInt()
    val top = state.topSpeaker ?: state.rows.firstOrNull()

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
                BoloCaption(
                    text = "${session?.topic.orEmpty().ifBlank { "Free talk" }} · " +
                        Format.minutes(session?.totalSpeechMs ?: 0L)
                )
                BoloCaption(
                    text = session?.let { Format.relativeDay(it.startedAt) } ?: ""
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Nice work.",
                color = BoloPalette.Ink,
                fontFamily = SansUI,
                fontWeight = FontWeight.Medium,
                fontSize = 42.sp,
                lineHeight = 46.sp,
                letterSpacing = (-1.2).sp
            )
        }

        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StaticEnglishHalo(share = share, diameter = 200.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            sharePct.toString(),
                            color = BoloPalette.Ink,
                            fontFamily = SansUI,
                            fontWeight = FontWeight.Normal,
                            fontSize = 70.sp,
                            lineHeight = 70.sp,
                            letterSpacing = (-3).sp
                        )
                        Text(
                            "%",
                            color = BoloPalette.InkFaint,
                            fontFamily = SansUI,
                            fontWeight = FontWeight.Normal,
                            fontSize = 26.sp,
                            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    BoloCaption("English usage")
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "${session?.chunksProcessed ?: 0} speech chunks analyzed over " +
                    Format.minutes(session?.totalSpeechMs ?: 0L),
                style = MaterialTheme.typography.bodyMedium,
                color = BoloPalette.InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (top != null) {
            item {
                BoloCard(accent = true) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BoloAvatar(name = top.student.displayName, size = 48.dp, active = true)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            BoloCaption("Most English today")
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${top.student.displayName} · ${Format.percent(top.stat.englishShare)}",
                                color = BoloPalette.Ink,
                                fontFamily = SansUI,
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                letterSpacing = (-0.3).sp
                            )
                        }
                    }
                }
            }
        }

        if (state.rows.isNotEmpty()) {
            item {
                BoloSectionLabel("Everyone today")
                Spacer(Modifier.height(8.dp))
            }
            itemsIndexed(state.rows) { index, row ->
                StudentRankRow(
                    rank = index + 1,
                    name = row.student.displayName,
                    pct = (row.stat.englishShare * 100f).roundToInt(),
                    onClick = { onOpenStudent(row.student.id) }
                )
            }
        }

        item {
            BoloCard {
                Column {
                    BoloCaption("30-second reflection")
                    Spacer(Modifier.height(8.dp))
                    BoloItalicAccent(text = REFLECTION, fontSize = 22)
                }
            }
        }

        // ── "How we counted this" disclosure (replaces DEBUG panel) ──
        item {
            Hairline()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { open = !open }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    BoloCaption("How we counted this")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "The formula, the chunks, the words we weren't sure about.",
                        color = BoloPalette.Ink,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    "▾",
                    color = BoloPalette.InkFaint,
                    fontSize = 14.sp,
                    modifier = Modifier.rotate(if (open) 180f else 0f)
                )
            }
            Hairline()
        }

        if (open) {
            item {
                BoloSectionLabel("The formula")
                Spacer(Modifier.height(10.dp))
                BoloCard {
                    Column {
                        Text(
                            "score = english ÷ meaningful × 100",
                            color = BoloPalette.InkMuted,
                            fontFamily = MonoData,
                            fontSize = 13.sp,
                            letterSpacing = 0.2.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "$totalEnglish ÷ $totalMeaningful × 100",
                                color = BoloPalette.Ink,
                                fontFamily = SansUI,
                                fontWeight = FontWeight.Medium,
                                fontSize = 22.sp,
                                letterSpacing = (-0.3).sp
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "= $sharePct%",
                                color = BoloPalette.SageDeep,
                                fontFamily = SansUI,
                                fontWeight = FontWeight.Medium,
                                fontSize = 22.sp,
                                letterSpacing = (-0.3).sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                MetricGrid(
                    items = listOf(
                        "Raw words" to totalRawWords,
                        "Meaningful tokens" to totalMeaningful,
                        "English words" to totalEnglish,
                        "Filler / noise" to totalFiller
                    )
                )
            }

            if (state.chunks.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    BoloSectionLabel("Per-chunk breakdown")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Showing ${state.chunks.size.coerceAtMost(6)} of ${state.chunks.size}.",
                        color = BoloPalette.InkFaint,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(12.dp))
                }
                items(state.chunks.take(6)) { chunk ->
                    ChunkRow(chunk)
                }
            }

            if (state.tokenBreakdown.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    BoloSectionLabel("Word classification")
                    Spacer(Modifier.height(12.dp))
                }
                items(state.tokenBreakdown.take(10)) { tok ->
                    TokenRow(tok)
                }
            }

            if (state.unknownWords.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(12.dp))
                    BoloSectionLabel("Words we couldn't place")
                    Spacer(Modifier.height(12.dp))
                    UnknownPillFlow(state.unknownWords.take(12))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "These slipped past the dictionary and the classifier. They don't count as English or as non-English — they're left out of the score.",
                        color = BoloPalette.InkFaint,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Hairline()
                }
            }
        }

        item {
            BoloSecondaryMetric(label = "Sustained English (≥ 60s)", value = "—")
            BoloSecondaryMetric(label = "Soft drift events", value = "—")
            if (top != null) {
                BoloSecondaryMetric(label = "Top contributor", value = top.student.displayName)
            }
        }

        if (state.rows.isNotEmpty()) {
            item {
                BoloSectionLabel("In the room")
                Spacer(Modifier.height(12.dp))
                ParticipantsFlow(names = state.rows.map { it.student.displayName })
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            BoloSolidButton(label = "Back to home", onClick = onDone)
            Spacer(Modifier.height(4.dp))
            BoloQuietButton(label = "See the trend", onClick = {
                state.rows.firstOrNull()?.let { onOpenStudent(it.student.id) }
            })
            Spacer(Modifier.height(14.dp))
            BoloPrivacyNote(
                "Counts saved. Speech went to Google's speech service for transcription; only the words you see above are stored on this phone — never the audio."
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StudentRankRow(rank: Int, name: String, pct: Int, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "%02d".format(rank),
                color = BoloPalette.InkFaint,
                fontFamily = MonoData,
                fontSize = 11.sp,
                modifier = Modifier.width(22.dp)
            )
            BoloAvatar(name = name, size = 28.dp)
            Spacer(Modifier.width(12.dp))
            Text(
                name,
                color = BoloPalette.Ink,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            BarRow(value = pct, modifier = Modifier.width(110.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "$pct%",
                color = BoloPalette.Ink,
                fontFamily = MonoData,
                fontSize = 13.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.End
            )
        }
        Hairline()
    }
}

@Composable
private fun BarRow(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(BoloPalette.SurfaceMuted)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(value.coerceIn(0, 100) / 100f)
                .height(4.dp)
                .background(BoloPalette.Sage)
        )
    }
}

@Composable
private fun MetricGrid(items: List<Pair<String, Int>>) {
    val rows = items.chunked(2)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(20.dp))
    ) {
        rows.forEachIndexed { rIdx, row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { cIdx, (label, value) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(BoloPalette.Surface)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            label.uppercase(),
                            color = BoloPalette.InkFaint,
                            fontFamily = MonoData,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            value.toString(),
                            color = BoloPalette.Ink,
                            fontFamily = SansUI,
                            fontWeight = FontWeight.Normal,
                            fontSize = 26.sp,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    if (cIdx < row.size - 1) {
                        Spacer(
                            Modifier
                                .width(1.dp)
                                .height(70.dp)
                                .background(BoloPalette.Hairline)
                        )
                    }
                }
            }
            if (rIdx < rows.size - 1) {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BoloPalette.Hairline)
                )
            }
        }
    }
}

@Composable
private fun ChunkRow(chunk: TranscriptChunk) {
    val pct = if (chunk.meaningfulCount > 0) {
        ((chunk.englishCount.toFloat() / chunk.meaningfulCount.toFloat()) * 100f).roundToInt()
    } else 0
    val pctColor = when {
        pct >= 60 -> BoloPalette.SageDeep
        pct >= 30 -> BoloPalette.Amber
        else -> BoloPalette.MicRed
    }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "CHUNK · ${"%02d".format(chunk.sequence + 1)}",
                color = BoloPalette.InkFaint,
                fontFamily = MonoData,
                fontSize = 10.5.sp,
                letterSpacing = 1.sp
            )
            Text(
                "$pct% · ${chunk.englishCount}/${chunk.meaningfulCount}",
                color = pctColor,
                fontFamily = MonoData,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "“${chunk.rawText}”",
            color = BoloPalette.Ink,
            fontFamily = SerifAccent,
            fontStyle = FontStyle.Italic,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            letterSpacing = (-0.1).sp
        )
    }
    Hairline()
}

@Composable
private fun TokenRow(tok: TokenAnalysis) {
    val color = when (tok.classification) {
        ClassificationType.ENGLISH, ClassificationType.LIKELY_ENGLISH -> BoloPalette.SageDeep
        ClassificationType.PROPER_NOUN -> BoloPalette.Sage
        ClassificationType.NON_ENGLISH -> BoloPalette.MicRed
        ClassificationType.FILLER -> BoloPalette.InkFaint
        ClassificationType.UNKNOWN -> BoloPalette.InkFaint
    }
    val sourceLabel = when (tok.source) {
        ClassificationSource.DICTIONARY -> "Dictionary"
        ClassificationSource.HEURISTIC -> "Heuristic"
        ClassificationSource.AI_FASTTEXT -> "AI FastText"
        ClassificationSource.NOISE_OR_FILLER -> "Noise list"
        ClassificationSource.UNKNOWN -> "Unknown"
    }
    val confSuffix = if (tok.source == ClassificationSource.AI_FASTTEXT) {
        " · ${(tok.confidence * 100).toInt()}% conf."
    } else ""

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                tok.token,
                color = BoloPalette.Ink,
                fontFamily = MonoData,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                modifier = Modifier.width(110.dp)
            )
            Text("→", color = BoloPalette.InkFaint, fontSize = 12.sp)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    tok.classification.name.replace('_', ' '),
                    color = color,
                    fontFamily = MonoData,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp
                )
                Spacer(Modifier.height(2.dp))
                val detail = buildString {
                    append(sourceLabel)
                    append(confSuffix)
                    tok.reason?.let { append(" · ").append(it) }
                }
                Text(detail, color = BoloPalette.InkFaint, fontSize = 11.sp)
            }
        }
    }
    Hairline()
}

@Composable
private fun UnknownPillFlow(items: List<Pair<String, Int>>) {
    val rows = items.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { (word, count) ->
                    Pill(word = word, count = count)
                }
            }
        }
    }
}

@Composable
private fun Pill(word: String, count: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(BoloPalette.Surface)
            .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            word,
            color = BoloPalette.InkMuted,
            fontFamily = MonoData,
            fontSize = 12.sp
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "×$count",
            color = BoloPalette.InkFaint,
            fontFamily = MonoData,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ParticipantsFlow(names: List<String>) {
    val rows = names.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { name -> ParticipantPill(name) }
            }
        }
    }
}

@Composable
private fun ParticipantPill(name: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(BoloPalette.Surface)
            .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(999.dp))
            .padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoloAvatar(name = name, size = 22.dp)
        Spacer(Modifier.width(8.dp))
        Text(name, color = BoloPalette.Ink, style = MaterialTheme.typography.bodyMedium)
    }
}

