package com.darkfaker.dkp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkfaker.dkp.ui.theme.ArcadeColors
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

data class PersonalBestPoint(
    val dateEpochDay: Long,
    val score: Long,
    val run: SavedRun
)

private const val SCORE_AXIS_BASE_STEP = 50_000L

@Composable
fun PersonalBestChart(
    points: List<PersonalBestPoint>,
    selectedRun: SavedRun?,
    onPointSelected: (SavedRun) -> Unit,
    modifier: Modifier = Modifier
) {
    val minimumScore = points.minOfOrNull { it.score } ?: 0L
    val maximumScore = points.maxOfOrNull { it.score } ?: 0L
    val scoreRange = (maximumScore - minimumScore).coerceAtLeast(0L)
    var tickStep = max(
        SCORE_AXIS_BASE_STEP,
        ceil(scoreRange.toDouble() / (SCORE_AXIS_BASE_STEP * 4)).toLong() * SCORE_AXIS_BASE_STEP
    )
    var axisMinimum: Long
    var axisMaximum: Long
    do {
        axisMinimum = ((minimumScore / tickStep) * tickStep - tickStep).coerceAtLeast(0L)
        axisMaximum = axisMinimum + tickStep * 4
        if (axisMaximum < maximumScore) tickStep += SCORE_AXIS_BASE_STEP
    } while (axisMaximum < maximumScore)
    val yLabels = (0..4).map { index -> axisMaximum - tickStep * index }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ArcadeColors.CardBackground, RoundedCornerShape(16.dp))
            .border(1.5.dp, ArcadeColors.CyanBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = null,
                    tint = ArcadeColors.CyanPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "PB OVER TIME",
                    color = ArcadeColors.CyanPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
            Spacer(Modifier.height(16.dp))

            if (points.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.PriorityHigh, null, tint = ArcadeColors.TextMuted, modifier = Modifier.size(16.dp))
                        Text("No Graph can be shown", color = ArcadeColors.TextMuted, fontSize = 12.sp)
                        Icon(Icons.Default.PriorityHigh, null, tint = ArcadeColors.TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    val firstDate = points.minOf { it.dateEpochDay }
                    val dateRange = points.maxOf { it.dateEpochDay } - firstDate
                    val dateProgress: (Int) -> Float = { index ->
                        if (points.size == 1) {
                            0.5f
                        } else if (dateRange > 0L) {
                            ((points[index].dateEpochDay - firstDate).toDouble() / dateRange).toFloat()
                        } else {
                            index.toFloat() / (points.size - 1)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(modifier = Modifier.width(34.dp).height(180.dp)) {
                            yLabels.forEachIndexed { index, label ->
                                val centerY = 10.dp + 140.dp * index / 4f
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp)
                                        .offset(y = centerY - 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = formatScoreAxisValue(label),
                                        color = ArcadeColors.TextMuted,
                                        fontSize = 8.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(6.dp))
                        Canvas(
                            modifier = Modifier
                                .weight(1f)
                                .height(180.dp)
                                .pointerInput(points, axisMinimum, axisMaximum) {
                                    detectTapGestures { tap ->
                                        val chartWidth = size.width.toFloat()
                                        val chartHeight = size.height.toFloat()
                                        val verticalInset = 10.dp.toPx()
                                        val plotHeight = chartHeight - verticalInset * 2f
                                        val range = (axisMaximum - axisMinimum).toDouble().coerceAtLeast(1.0)
                                        val horizontalInset = if (points.size > 1) 13.dp.toPx() else 0f
                                        val usableWidth = chartWidth - horizontalInset * 2f
                                        val xPositions = points.indices.map { index ->
                                            horizontalInset + usableWidth * dateProgress(index)
                                        }
                                        points.forEachIndexed { index, point ->
                                            val x = xPositions[index]
                                            val normalized = ((point.score - axisMinimum) / range).toFloat()
                                                .coerceIn(0f, 1f)
                                            val y = chartHeight - verticalInset - normalized * plotHeight
                                            val inPoint = (tap.x - x) * (tap.x - x) +
                                                (tap.y - y) * (tap.y - y) <= 18.dp.toPx() * 18.dp.toPx()
                                            val nearestGap = when {
                                                xPositions.size == 1 -> Float.POSITIVE_INFINITY
                                                index == 0 -> xPositions[1] - xPositions[0]
                                                index == xPositions.lastIndex -> xPositions[index] - xPositions[index - 1]
                                                else -> minOf(xPositions[index] - xPositions[index - 1], xPositions[index + 1] - xPositions[index])
                                            }
                                            val barWidth = minOf(26.dp.toPx(), nearestGap * 0.45f)
                                            val inBar = tap.x in (x - barWidth / 2f)..(x + barWidth / 2f) &&
                                                tap.y in y..(chartHeight - verticalInset)
                                            if (inPoint || inBar) {
                                                onPointSelected(point.run)
                                                return@detectTapGestures
                                            }
                                        }
                                    }
                                }
                        ) {
                            val chartWidth = size.width
                            val chartHeight = size.height
                            val verticalInset = 10.dp.toPx()
                            val plotHeight = chartHeight - verticalInset * 2f
                            val range = (axisMaximum - axisMinimum).toDouble().coerceAtLeast(1.0)

                            yLabels.forEachIndexed { index, _ ->
                                val y = verticalInset + plotHeight * index / 4f
                                drawLine(
                                    color = Color(0xFF0F263E),
                                    start = Offset(0f, y),
                                    end = Offset(chartWidth, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            val insetPx = if (points.size > 1) 13.dp.toPx() else 0f
                            val usableWidthPx = chartWidth - insetPx * 2f
                            val pointPositions = points.mapIndexed { index, point ->
                                val x = insetPx + usableWidthPx * dateProgress(index)
                                val normalized = ((point.score - axisMinimum) / range).toFloat().coerceIn(0f, 1f)
                                val y = chartHeight - verticalInset - normalized * plotHeight
                                Offset(x, y)
                            }

                            pointPositions.forEachIndexed { index, point ->
                                val nearestGap = when {
                                    pointPositions.size == 1 -> Float.POSITIVE_INFINITY
                                    index == 0 -> pointPositions[1].x - point.x
                                    index == pointPositions.lastIndex -> point.x - pointPositions[index - 1].x
                                    else -> minOf(
                                        point.x - pointPositions[index - 1].x,
                                        pointPositions[index + 1].x - point.x
                                    )
                                }
                                val barWidth = minOf(26.dp.toPx(), nearestGap * 0.45f)
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            ArcadeColors.GreenAccent,
                                            Color(0xFF009B57),
                                            Color(0xFF003D26)
                                        ),
                                        startY = point.y,
                                        endY = chartHeight - verticalInset
                                    ),
                                    topLeft = Offset(point.x - barWidth / 2f, point.y),
                                    size = Size(barWidth, chartHeight - verticalInset - point.y),
                                    cornerRadius = CornerRadius(6f, 6f)
                                )
                            }

                            if (pointPositions.size > 1) {
                                val line = Path().apply {
                                    moveTo(pointPositions.first().x, pointPositions.first().y)
                                    pointPositions.drop(1).forEach { lineTo(it.x, it.y) }
                                }
                                drawPath(line, Color(0xFF00B0FF), style = Stroke(width = 3.dp.toPx()))
                                drawPath(line, Color(0xFFB8F3FF).copy(alpha = 0.85f), style = Stroke(width = 1.dp.toPx()))
                            }

                            pointPositions.forEachIndexed { index, point ->
                                val isSelected = points[index].run == selectedRun
                                if (isSelected) {
                                    drawCircle(Color(0xFF00B0FF).copy(alpha = 0.2f), 16.dp.toPx(), point)
                                    drawCircle(Color(0xFF00B0FF).copy(alpha = 0.38f), 11.dp.toPx(), point)
                                } else {
                                    drawCircle(Color(0xFF00B0FF).copy(alpha = 0.22f), 7.dp.toPx(), point)
                                }
                                drawCircle(Color(0xFF00B0FF), 6.dp.toPx(), point)
                                drawCircle(Color.White, 3.dp.toPx(), point)
                            }
                        }
                    }

                }
            }
        }
    }
}

private fun formatScoreAxisValue(value: Long): String = when {
    value >= 1_000_000 -> {
        val millions = value / 1_000_000.0
        "${String.format(Locale.ROOT, "%.2f", millions).trimEnd('0').trimEnd('.')}M"
    }
    value >= 1_000 -> "${value / 1_000}K"
    else -> value.toString()
}
