package com.example.dkpace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.dkpace.ui.theme.ArcadeColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class SavedRun(
    val name: String,
    val score: String,
    val date: String,
    val pace: String,
    val level: Int?,
    val average: String,
    val savedAt: Long = 0L,
    val averageIsRed: Boolean = false
)

enum class RunSort(val label: String, val icon: ImageVector) {
    DATE("Date", Icons.Default.CalendarMonth),
    NAME("Name", Icons.Default.Edit),
    SCORE("Score", Icons.Default.Star),
    PACE("Pace", Icons.Default.EmojiEvents),
    LEVEL("Level", Icons.Default.Adjust)
}

@Composable
fun RunSortControl(
    sort: RunSort,
    onSortSelected: (RunSort) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val sortControlWidth = 95.dp
    val sortMenuShape = RoundedCornerShape(9.dp)
    Box(modifier = modifier.width(sortControlWidth)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ArcadeColors.CardBackground, sortMenuShape)
                .border(1.dp, ArcadeColors.CyanBorder, sortMenuShape)
                .clickable { menuExpanded = true }
                .padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null, tint = ArcadeColors.TextWhite, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sort: ", color = ArcadeColors.TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(sort.label, color = ArcadeColors.CyanPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
                .width(sortControlWidth)
                .clip(sortMenuShape)
                .background(ArcadeColors.CardBackground, sortMenuShape)
                .border(1.dp, ArcadeColors.CyanBorder, sortMenuShape)
        ) {
            RunSort.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = null,
                                tint = if (sort == option) ArcadeColors.CyanPrimary else ArcadeColors.TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                option.label,
                                color = if (sort == option) ArcadeColors.CyanPrimary else ArcadeColors.TextWhite
                            )
                        }
                    },
                    onClick = { onSortSelected(option); menuExpanded = false }
                )
            }
        }
    }
}

@Composable
fun AnalyzeRunsScreen(runs: List<SavedRun>, sort: RunSort, modifier: Modifier = Modifier) {
    val sortedRuns = when (sort) {
        RunSort.NAME -> runs.sortedByDescending { it.name.lowercase(Locale.ROOT) }
        RunSort.SCORE -> runs.sortedByDescending { it.score.toSortNumber() }
        RunSort.DATE -> runs.sortedWith(
            compareByDescending<SavedRun> { it.date }.thenByDescending { it.savedAt }
        )
        RunSort.PACE -> runs.sortedByDescending { it.pace.toSortNumber() }
        RunSort.LEVEL -> runs.sortedByDescending { it.level ?: Int.MIN_VALUE }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(Modifier.height(10.dp))
        if (sortedRuns.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
                    .border(1.dp, ArcadeColors.BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No saved runs", color = ArcadeColors.TextMuted, fontSize = 12.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                sortedRuns.forEach { run -> SavedRunCard(run, sort) }
            }
        }
    }
}

private enum class RunMetricType { SCORE, AVERAGE, LEVEL, PACE }

private fun metricOrder(sort: RunSort): List<RunMetricType> {
    val defaultOrder = listOf(
        RunMetricType.SCORE,
        RunMetricType.AVERAGE,
        RunMetricType.LEVEL,
        RunMetricType.PACE
    )
    val sortedMetric = when (sort) {
        RunSort.SCORE -> RunMetricType.SCORE
        RunSort.LEVEL -> RunMetricType.LEVEL
        RunSort.PACE -> RunMetricType.PACE
        RunSort.DATE, RunSort.NAME -> null
    }
    return sortedMetric?.let { metric -> listOf(metric) + defaultOrder.filterNot { it == metric } }
        ?: defaultOrder
}

@Composable
private fun SavedRunCard(run: SavedRun, sort: RunSort) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
            .border(1.dp, ArcadeColors.CyanBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = run.name.ifBlank { "Run" },
                    modifier = Modifier.weight(1f, fill = false),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = ArcadeColors.TextSecondary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(Modifier.width(3.dp))
            Text(run.date.toDisplayDate(), color = ArcadeColors.TextMuted, fontSize = 9.sp, maxLines = 1)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            metricOrder(sort).forEach { metric ->
                when (metric) {
                    RunMetricType.SCORE -> RunMetric(
                        "SCORE", run.score.ifBlank { "---" }, Icons.Default.Star,
                        Color(0xFFB000FF), Modifier.weight(1f)
                    )
                    RunMetricType.AVERAGE -> RunMetric(
                        "AVERAGE", run.average, Icons.Default.Balance,
                        if (run.averageIsRed) ArcadeColors.RedAccent else ArcadeColors.GreenAccent,
                        Modifier.weight(1f)
                    )
                    RunMetricType.LEVEL -> RunMetric(
                        "LEVEL", run.level?.let { String.format(Locale.ROOT, "L = %02d", it) } ?: "---",
                        Icons.Default.Adjust, ArcadeColors.CyanPrimary, Modifier.weight(1f)
                    )
                    RunMetricType.PACE -> RunMetric(
                        "PACE", run.pace, Icons.Default.EmojiEvents,
                        Color.White, Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RunMetric(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(9.dp))
            .border(1.dp, color, RoundedCornerShape(9.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(11.dp))
            Text(label, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private fun String.toSortNumber(): Long = filter { it.isDigit() || it == '-' }.toLongOrNull() ?: Long.MIN_VALUE

private fun String.toDisplayDate(): String = runCatching {
    LocalDate.parse(this).format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT))
}.getOrDefault(this)
