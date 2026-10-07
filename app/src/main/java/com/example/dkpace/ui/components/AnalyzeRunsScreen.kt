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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val averageIsRed: Boolean = false,
    val neededAverage: String = "---",
    val goal: String = "",
    val levelMetrics: Map<Int, LevelMetrics> = emptyMap(),
    val levelIsEndLevel: Boolean = false,
    val scoreManuallyEdited: Boolean = false
)

enum class RunSort(val label: String, val icon: ImageVector) {
    DATE("Date", Icons.Default.CalendarMonth),
    NAME("Name", Icons.Default.Edit),
    SCORE("Score", Icons.Default.Star),
    PACE("Pace", Icons.Default.EmojiEvents),
    LEVEL("Level", Icons.Default.Adjust),
    AVERAGE("Avg", Icons.Default.Balance)
}

@Composable
fun RunSortControl(
    sort: RunSort,
    onSortSelected: (RunSort) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val sortControlWidth = 93.dp
    val sortMenuShape = RoundedCornerShape(9.dp)
    Box(modifier = modifier.width(sortControlWidth)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ArcadeColors.CardBackground, sortMenuShape)
                .border(1.dp, ArcadeColors.CyanBorder, sortMenuShape)
                .clickable { menuExpanded = true }
                .padding(horizontal = 7.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = sort.icon,
                    contentDescription = null,
                    tint = ArcadeColors.TextWhite,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    sort.label,
                    modifier = Modifier.weight(1f),
                    color = ArcadeColors.TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Choose sort order",
                    tint = ArcadeColors.TextWhite,
                    modifier = Modifier.size(19.dp)
                )
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
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = null,
                                tint = if (sort == option) ArcadeColors.CyanPrimary else ArcadeColors.TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                option.label,
                                color = if (sort == option) ArcadeColors.CyanPrimary else ArcadeColors.TextWhite,
                                fontSize = 12.sp,
                                maxLines = 1
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
fun AnalyzeRunsScreen(
    runs: List<SavedRun>,
    sort: RunSort,
    onRunClick: (SavedRun) -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedRuns = when (sort) {
        RunSort.NAME -> runs.sortedByDescending { it.name.lowercase(Locale.ROOT) }
        RunSort.SCORE -> runs.sortedByDescending { it.score.toSortNumber() }
        RunSort.DATE -> runs.sortedWith(
            compareByDescending<SavedRun> { it.date }.thenByDescending { it.savedAt }
        )
        RunSort.PACE -> runs.sortedByDescending { it.pace.toSortNumber() }
        RunSort.LEVEL -> runs.sortedByDescending { it.level ?: Int.MIN_VALUE }
        RunSort.AVERAGE -> runs.sortedByDescending { it.average.toSortNumber() }
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
                sortedRuns.forEachIndexed { index, run ->
                    SavedRunCard(run, sort, runSidebarColors[index % runSidebarColors.size]) {
                        onRunClick(run)
                    }
                }
            }
        }
    }
}

private enum class RunMetricType { AVERAGE, LEVEL, PACE }

private val runSidebarColors = listOf(
    ArcadeColors.CyanPrimary,
    Color(0xFFB000FF),
    ArcadeColors.GreenAccent,
    ArcadeColors.RedAccent,
    ArcadeColors.GoldAccent,
    Color.White,
    Color(0xFFFF9800)
)

private fun metricOrder(sort: RunSort): List<RunMetricType> {
    val defaultOrder = listOf(
        RunMetricType.PACE,
        RunMetricType.AVERAGE,
        RunMetricType.LEVEL
    )
    val sortedMetric = when (sort) {
        RunSort.AVERAGE -> RunMetricType.AVERAGE
        RunSort.LEVEL -> RunMetricType.LEVEL
        RunSort.PACE -> RunMetricType.PACE
        RunSort.DATE, RunSort.NAME, RunSort.SCORE -> null
    }
    return sortedMetric?.let { metric -> listOf(metric) + defaultOrder.filterNot { it == metric } }
        ?: defaultOrder
}

@Composable
private fun SavedRunCard(
    run: SavedRun,
    sort: RunSort,
    sidebarColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
            .border(1.dp, ArcadeColors.CyanBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .drawBehind {
                val sidebarWidth = 5.dp.toPx()
                val verticalInset = 10.dp.toPx()
                val horizontalInset = 4.dp.toPx()
                drawRoundRect(
                    color = sidebarColor,
                    topLeft = Offset(horizontalInset, verticalInset),
                    size = Size(sidebarWidth, (size.height - verticalInset * 2).coerceAtLeast(0f)),
                    cornerRadius = CornerRadius(sidebarWidth / 2f, sidebarWidth / 2f)
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(start = 18.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = run.name.ifBlank { "Run" },
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = ArcadeColors.TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(run.date.toDisplayDate(), color = ArcadeColors.TextMuted, fontSize = 9.sp, maxLines = 1)
                }
            }
            RunMetric(
                "SCORE", run.score.ifBlank { "---" }, Icons.Default.Star,
                Color(0xFFB000FF), Modifier.width(115.dp), labelColor = Color(0xFFB000FF)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            metricOrder(sort).forEach { metric ->
                when (metric) {
                    RunMetricType.AVERAGE -> RunMetric(
                        "AVERAGE", run.average, Icons.Default.Balance,
                        if (run.averageIsRed) ArcadeColors.RedAccent else ArcadeColors.GreenAccent,
                        Modifier.weight(1.05f)
                    )
                    RunMetricType.LEVEL -> RunMetric(
                        "LEVEL", run.level?.let { String.format(Locale.ROOT, "L = %02d", it) } ?: "---",
                        Icons.Default.Adjust, ArcadeColors.CyanPrimary, Modifier.weight(0.9f)
                    )
                    RunMetricType.PACE -> RunMetric(
                        "PACE", run.pace, Icons.Default.EmojiEvents,
                        Color.White, Modifier.weight(1.05f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RunMetric(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier,
    labelColor: Color = color
) {
    Row(
        modifier = modifier
            .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(9.dp))
            .border(1.dp, color, RoundedCornerShape(9.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
        Text(label, color = labelColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(
            value,
            modifier = Modifier.weight(1f),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun String.toSortNumber(): Long = filter { it.isDigit() || it == '-' }.toLongOrNull() ?: Long.MIN_VALUE

private fun String.toDisplayDate(): String = runCatching {
    LocalDate.parse(this).format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT))
}.getOrDefault(this)
