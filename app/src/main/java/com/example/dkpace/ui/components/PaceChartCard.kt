package com.example.dkpace.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dkpace.ui.theme.ArcadeColors
import com.example.dkpace.R
import kotlin.math.roundToLong
import java.util.Locale

private const val PACE_AXIS_BASE_STEP = 50_000L

data class PacePoint(
    val level: Int,
    val pace: Long,
    val death: Long = 0L,
    val hasDeathEntry: Boolean = death > 0L
)

@Composable
fun PaceChartCard(
    points: List<PacePoint>,
    goal: Long?,
    selectedLevel: Int,
    onLevelSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val xAxisLevels = points.maxOfOrNull { it.level }
        ?.let { highestLevel -> (5..highestLevel).toList() }
        .orEmpty()
    val minimumPace = points.minOfOrNull { it.pace } ?: 0L
    val maximumPace = points.maxOfOrNull { it.pace } ?: 100_000L
    val paceRange = (maximumPace - minimumPace).coerceAtLeast(0L)
    var tickStep = maxOf(
        PACE_AXIS_BASE_STEP,
        ((paceRange + PACE_AXIS_BASE_STEP * 4 - 1) / (PACE_AXIS_BASE_STEP * 4)) * PACE_AXIS_BASE_STEP
    )
    var axisMinimum: Long
    var axisMaximum: Long
    do {
        axisMinimum = ((minimumPace / tickStep) * tickStep - tickStep).coerceAtLeast(0L)
        axisMaximum = axisMinimum + tickStep * 4
        if (axisMaximum < maximumPace) tickStep += PACE_AXIS_BASE_STEP
    } while (axisMaximum < maximumPace)
    val yLabels = (0..4).map { index ->
        axisMaximum - tickStep * index
    }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        tint = ArcadeColors.CyanPrimary,
                        modifier = Modifier.padding(top = 2.dp).size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PACE OVER TIME",
                        color = ArcadeColors.CyanPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(190.dp)) {
                val plotWidth = (maxWidth - 38.dp).coerceAtLeast(0.dp)
                val barWidth = if (xAxisLevels.isNotEmpty()) {
                    minOf(plotWidth / xAxisLevels.size * 0.45f, 26.dp)
                } else {
                    0.dp
                }
                val pointInset = if (xAxisLevels.size > 1) barWidth / 2f else 0.dp

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Y-Axis Labels
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(160.dp)
                    ) {
                        if (points.isNotEmpty()) {
                            yLabels.forEachIndexed { index, label ->
                                val centerYDp = 10.dp + (140.dp * index / 4f) - 3.dp
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp)
                                        .offset(y = centerYDp - 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = formatAxisValue(label),
                                        color = ArcadeColors.TextMuted,
                                        fontSize = 8.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Chart Canvas (Grid Lines + Bars + Line + Dots)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                    ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(points, xAxisLevels, axisMinimum, axisMaximum) {
                                detectTapGestures { tap ->
                                    if (points.isNotEmpty()) {
                                        val chartWidth = size.width.toFloat()
                                        val chartHeight = size.height.toFloat()
                                        val barWidth = minOf(
                                            chartWidth / xAxisLevels.size * 0.45f,
                                            26.dp.toPx()
                                        )
                                        val horizontalInset = if (xAxisLevels.size > 1) barWidth / 2f else 0f
                                        val usableWidth = chartWidth - horizontalInset * 2f
                                        val spacing = if (xAxisLevels.size > 1) {
                                            usableWidth / (xAxisLevels.size - 1)
                                        } else {
                                            chartWidth
                                        }
                                        val verticalInset = 10.dp.toPx()
                                        val plotHeight = chartHeight - verticalInset * 2f
                                        val range = (axisMaximum - axisMinimum).toDouble()
                                        val hitRadius = 16.dp.toPx()

                                        points.forEach { point ->
                                            val slotIndex = point.level - 5
                                            val x = if (xAxisLevels.size == 1) chartWidth / 2f
                                            else horizontalInset + spacing * slotIndex
                                            val normalized = ((point.pace - axisMinimum) / range).toFloat()
                                            val y = chartHeight - verticalInset -
                                                normalized.coerceIn(0f, 1f) * plotHeight
                                            val withinPoint = (tap.x - x) * (tap.x - x) +
                                                (tap.y - y) * (tap.y - y) <= hitRadius * hitRadius
                                            val withinBar = tap.x in (x - barWidth / 2f)..(x + barWidth / 2f) &&
                                                tap.y in y..(chartHeight - verticalInset)
                                            if (withinPoint || withinBar) {
                                                onLevelSelected(point.level)
                                                return@detectTapGestures
                                            }
                                        }
                                    }
                                }
                            }
                    ) {
                        val chartWidth = size.width
                        val chartHeight = size.height
                        val range = (axisMaximum - axisMinimum).toDouble()
                        val verticalInset = 10.dp.toPx()
                        val plotHeight = chartHeight - verticalInset * 2f

                        // Draw Grid Lines horizontally centered with Y-labels
                        yLabels.forEachIndexed { index, _ ->
                            val y = verticalInset + plotHeight * index / 4f
                            drawLine(
                                color = Color(0xFF0F263E),
                                start = Offset(0f, y),
                                end = Offset(chartWidth, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        if (points.isNotEmpty() && goal != null && goal in axisMinimum..axisMaximum) {
                            val normalizedGoal = (goal - axisMinimum).toDouble() / range
                            val goalY = chartHeight - verticalInset - normalizedGoal.toFloat() * plotHeight
                            drawLine(
                                color = ArcadeColors.GreenAccent.copy(alpha = 0.35f),
                                start = Offset(0f, goalY),
                                end = Offset(chartWidth, goalY),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        if (points.isNotEmpty()) {
                            val barWidth = minOf(
                                chartWidth / xAxisLevels.size * 0.45f,
                                26.dp.toPx()
                            )
                            val horizontalInset = if (xAxisLevels.size > 1) barWidth / 2f else 0f
                            val usableWidth = chartWidth - horizontalInset * 2f
                            val spacing = if (xAxisLevels.size > 1) {
                                usableWidth / (xAxisLevels.size - 1)
                            } else {
                                chartWidth
                            }
                            val pointPositions = points.map { point ->
                                val slotIndex = point.level - 5
                                val x = if (xAxisLevels.size == 1) chartWidth / 2f
                                else horizontalInset + spacing * slotIndex
                                val normalized = ((point.pace - axisMinimum) / range).toFloat()
                                val y = chartHeight - verticalInset -
                                    normalized.coerceIn(0f, 1f) * plotHeight
                                Offset(x, y)
                            }

                            points.forEachIndexed { index, pacePoint ->
                                val point = pointPositions[index]
                                val barTop = point.y.coerceIn(verticalInset, chartHeight - verticalInset)
                                val barGradientColors = when {
                                    goal == null -> listOf(
                                        Color(0xFF00B0FF),
                                        Color(0xFF005299),
                                        Color(0xFF002244)
                                    )
                                    pacePoint.pace >= goal -> listOf(
                                        ArcadeColors.GreenAccent,
                                        Color(0xFF009B57),
                                        Color(0xFF003D26)
                                    )
                                    else -> listOf(
                                        ArcadeColors.RedAccent,
                                        Color(0xFFB01235),
                                        Color(0xFF4A0717)
                                    )
                                }
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = barGradientColors,
                                        startY = barTop,
                                        endY = chartHeight - verticalInset
                                    ),
                                    topLeft = Offset(point.x - barWidth / 2f, barTop),
                                    size = Size(barWidth, chartHeight - verticalInset - barTop),
                                    cornerRadius = CornerRadius(6f, 6f)
                                )
                            }

                            if (pointPositions.size > 1) {
                                val path = Path().apply {
                                    moveTo(pointPositions.first().x, pointPositions.first().y)
                                    pointPositions.drop(1).forEach { lineTo(it.x, it.y) }
                                }
                                drawPath(
                                    path,
                                    Color(0xFF00B0FF),
                                    style = Stroke(width = 3.dp.toPx())
                                )
                                drawPath(
                                    path,
                                    Color(0xFFB8F3FF).copy(alpha = 0.85f),
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }

                            pointPositions.forEachIndexed { index, point ->
                                val isSelected = points[index].level == selectedLevel
                                val pointColor = Color(0xFF00B0FF)
                                drawCircle(
                                    color = pointColor.copy(alpha = 0.22f),
                                    radius = 7.dp.toPx(),
                                    center = point
                                )
                                if (isSelected) {
                                    drawCircle(
                                        color = pointColor.copy(alpha = 0.2f),
                                        radius = 16.dp.toPx(),
                                        center = point
                                    )
                                    drawCircle(
                                        color = pointColor.copy(alpha = 0.38f),
                                        radius = 11.dp.toPx(),
                                        center = point
                                    )
                                    drawCircle(pointColor, radius = 6.dp.toPx(), center = point)
                                } else {
                                    drawCircle(pointColor, radius = 6.dp.toPx(), center = point)
                                }
                                drawCircle(Color.White, radius = 3.dp.toPx(), center = point)
                            }
                        }
                    }
                    if (points.isNotEmpty()) {
                        val barWidthDp = minOf(
                            plotWidth / xAxisLevels.size * 0.45f,
                            26.dp
                        )
                        val horizontalInsetDp = if (xAxisLevels.size > 1) barWidthDp / 2f else 0.dp
                        val usableWidthDp = plotWidth - horizontalInsetDp * 2f
                        val spacingDp = if (xAxisLevels.size > 1) {
                            usableWidthDp / (xAxisLevels.size - 1)
                        } else {
                            plotWidth
                        }
                        points.filter { it.hasDeathEntry }.forEach { point ->
                            val slotIndex = point.level - 5
                            val x = if (xAxisLevels.size == 1) plotWidth / 2f
                            else horizontalInsetDp + spacingDp * slotIndex
                            val normalized = ((point.pace - axisMinimum).toDouble() /
                                (axisMaximum - axisMinimum)).toFloat()
                            val barTop = (150.dp - 140.dp * normalized.coerceIn(0f, 1f))
                                .coerceIn(10.dp, 150.dp)
                            val centerY = barTop + (150.dp - barTop) / 2f
                            Icon(
                                painter = painterResource(R.drawable.ic_skull),
                                contentDescription = null,
                                modifier = Modifier
                                    .offset(x = x - 8.dp, y = centerY - 8.dp)
                                    .size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                    }
                }

                // X-Axis Labels at Bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(start = 38.dp + pointInset, end = pointInset)
                        .padding(bottom = 12.dp)
                        .fillMaxWidth()
                        .height(22.dp),
                    horizontalArrangement = if (xAxisLevels.size == 1) Arrangement.Center else Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    xAxisLevels.forEach { level ->
                        Box(Modifier.width(0.dp), contentAlignment = Alignment.BottomCenter) {
                            Text(
                                text = level.toString(),
                                modifier = Modifier.wrapContentWidth(unbounded = true),
                                color = ArcadeColors.TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

        }
    }
}

private fun formatAxisValue(rounded: Long): String {
    return when {
        rounded >= 1_000_000 -> {
            val millions = rounded / 1_000_000.0
            "${"%.2f".format(Locale.GERMANY, millions).trimEnd('0').trimEnd(',')}M"
        }
        rounded >= 1_000 -> "${(rounded / 1_000.0).roundToLong()}K"
        else -> rounded.toString()
    }
}
