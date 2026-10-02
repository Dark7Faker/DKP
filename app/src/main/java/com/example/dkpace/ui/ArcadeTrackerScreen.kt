package com.example.dkpace.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dkpace.ui.components.ActionButtonsColumn
import com.example.dkpace.ui.components.CurrentRunCard
import com.example.dkpace.ui.components.HeaderSection
import com.example.dkpace.ui.components.MetricCardsRow
import com.example.dkpace.ui.components.GoalMetricCard
import com.example.dkpace.ui.components.LevelMetrics
import com.example.dkpace.ui.components.PaceChartCard
import com.example.dkpace.ui.components.PacePoint
import com.example.dkpace.ui.components.SaveRunMenu
import com.example.dkpace.ui.theme.ArcadeColors
import kotlin.math.roundToLong

@Composable
fun ArcadeTrackerScreen() {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val window = remember(context) { context.findActivity()?.window }
    val originalWindowBrightness = remember(window) {
        window?.attributes?.screenBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    }
    val originallyKeptScreenOn = remember(window) {
        window?.let {
            (it.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0
        } ?: false
    }
    val preferences = remember(context) {
        context.getSharedPreferences("dkpace_preferences", Context.MODE_PRIVATE)
    }
    var currentLevel by remember { mutableIntStateOf(4) }
    var batterySaverEnabled by remember { mutableStateOf(false) }
    var inputFieldFocused by remember { mutableStateOf(false) }
    var saveRunMenuVisible by remember { mutableStateOf(false) }
    SideEffect {
        window?.let { currentWindow ->
            val keepScreenOnFlag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            currentWindow.attributes = currentWindow.attributes.apply {
                screenBrightness = if (batterySaverEnabled) 0.05f else originalWindowBrightness
                flags = if (batterySaverEnabled || originallyKeptScreenOn) {
                    flags or keepScreenOnFlag
                } else {
                    flags and keepScreenOnFlag.inv()
                }
            }
        }
    }
    DisposableEffect(window) {
        onDispose {
            window?.let { currentWindow ->
                val keepScreenOnFlag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                currentWindow.attributes = currentWindow.attributes.apply {
                    screenBrightness = originalWindowBrightness
                    flags = if (originallyKeptScreenOn) {
                        flags or keepScreenOnFlag
                    } else {
                        flags and keepScreenOnFlag.inv()
                    }
                }
            }
        }
    }
    var goal by remember {
        mutableStateOf(preferences.getString("goal", "").orEmpty())
    }
    var saveRunName by remember { mutableStateOf("") }
    LaunchedEffect(goal) {
        val roundedGoal = goal.toMetricLong()
        if (roundedGoal > 0L) {
            preferences.edit().putString("goal", roundedGoal.formatMetric()).apply()
        } else {
            preferences.edit().remove("goal").apply()
        }
    }
    val metricsByLevel = remember { mutableStateMapOf<Int, LevelMetrics>() }


    val currentMetrics = metricsByLevel[currentLevel] ?: LevelMetrics()
    val startingMetrics = metricsByLevel[4] ?: LevelMetrics()
    val start = startingMetrics.score.toMetricLong() + startingMetrics.bonus.toMetricLong()
    val paceHistory = (5..21).mapNotNull { level ->
        val metrics = metricsByLevel[level] ?: return@mapNotNull null
        if (metrics.score.isBlank()) return@mapNotNull null

        val total = metrics.score.toMetricLong() +
            metrics.bonus.toMetricLong() -
            metrics.death.toMetricLong()
        val levelPace = ((total - start).toDouble() / (level - 4)) * 17 + 700 + start
        if (levelPace < 0) return@mapNotNull null

        PacePoint(
            level = level,
            pace = levelPace.roundToLong().roundToHundred(),
            death = metrics.death.toMetricLong(),
            hasDeathEntry = metrics.death.isNotBlank()
        )
    }
    val latestScoredLevel = metricsByLevel
        .filter { (_, metrics) -> metrics.score.isNotBlank() && metrics.score.toMetricLong() >= 0L }
        .keys
        .maxOrNull()
    val latestScoredMetrics = latestScoredLevel?.let { metricsByLevel[it] }
    val startScore = startingMetrics.score
        .takeIf { it.isNotBlank() && it.toMetricLong() >= 0L }
        ?.let { it.toMetricLong().formatMetric() } ?: "---"
    val saveRunScore = latestScoredMetrics?.score
        ?.takeIf { it.isNotBlank() && it.toMetricLong() >= 0L }
        ?.let { it.toMetricLong().formatMetric() }
        .orEmpty()
    val saveRunPacePoint = paceHistory.firstOrNull { it.level == latestScoredLevel }
    val saveRunPace = saveRunPacePoint?.pace?.formatMetric() ?: "---"
    val saveRunAverageValue = latestScoredLevel
        ?.takeIf { it > 4 }
        ?.let { level ->
            latestScoredMetrics
                ?.takeIf { it.score.isNotBlank() }
                ?.let { metrics ->
                    (
                        metrics.score.toMetricLong() +
                            metrics.bonus.toMetricLong() -
                            start -
                            metrics.death.toMetricLong()
                        ).toDouble() / (level - 4)
                }
        }
    val saveRunAverage = saveRunAverageValue
        ?.takeIf { it > 4 }
        ?.roundToLong()
        ?.formatMetric() ?: "---"
    val saveRunProgressColor = if (
        saveRunPacePoint != null && goal.isNotBlank() && saveRunPacePoint.pace < goal.toMetricLong()
    ) {
        ArcadeColors.RedAccent
    } else {
        ArcadeColors.GreenAccent
    }
    val pace = if (currentLevel == 4 || currentMetrics.score.isBlank()) {
        null
    } else {
        val currentTotal = currentMetrics.score.toMetricLong() +
            currentMetrics.bonus.toMetricLong() -
            currentMetrics.death.toMetricLong()
        val calculatedPace = ((currentTotal - start).toDouble() / (currentLevel - 4)) * 17 + 700 + start
        calculatedPace.takeIf { it >= 0 }?.roundToLong()
    }
    val averageForLevel = if (currentLevel == 4 || currentMetrics.score.isBlank()) {
        null
    } else {
        val pointsAfterDeath = currentMetrics.score.toMetricLong() +
            currentMetrics.bonus.toMetricLong() -
            start -
            currentMetrics.death.toMetricLong()
        pointsAfterDeath.toDouble() / (currentLevel - 4)
    }
    val currentAverage = averageForLevel?.takeIf { it > 4 }?.roundToLong()
    val nextLevelCurrentAverage = if (currentAverage == null || averageForLevel == null) {
        null
    } else {
        val nextAverage = currentMetrics.score.toMetricLong() +
            currentMetrics.bonus.toMetricLong() + averageForLevel
        nextAverage.takeIf { it >= 0 }?.roundToLong()
    }
    val neededAverageValue = if (goal.isBlank() || currentMetrics.score.isBlank() || currentLevel >= 21) {
        null
    } else {
        val remainingPoints = goal.toMetricLong() -
            currentMetrics.score.toMetricLong() -
            currentMetrics.bonus.toMetricLong()
        remainingPoints.toDouble() / (21 - currentLevel)
    }
    val neededAverage = neededAverageValue?.takeIf { it > 0 }?.roundToLong()
    val nextLevelNeededAverage = if (neededAverage == null) {
        null
    } else {
        neededAverageValue?.let { needed ->
            val nextNeeded = currentMetrics.score.toMetricLong() + needed
            nextNeeded.takeIf { it > 0 }?.roundToLong()
        }
    }
    val progressColor = if (
        pace != null && goal.isNotBlank() && pace.roundToHundred() < goal.toMetricLong()
    ) {
        ArcadeColors.RedAccent
    } else {
        ArcadeColors.GreenAccent
    }
    val previousMetrics = metricsByLevel[currentLevel - 1] ?: LevelMetrics()
    val pointsInThisLevel = if (
        currentLevel == 4 || currentMetrics.score.isBlank() || previousMetrics.score.isBlank()
    ) {
        null
    } else {
        val points = currentMetrics.score.toMetricLong() +
            currentMetrics.bonus.toMetricLong() -
            currentMetrics.death.toMetricLong() -
            previousMetrics.score.toMetricLong() -
            previousMetrics.bonus.toMetricLong()
        points.takeIf { it >= 0L }
    }

    if (batterySaverEnabled) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { batterySaverEnabled = false },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Battery2Bar,
                contentDescription = "Exit battery saver",
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )
        }
    } else Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = ArcadeColors.Background
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(currentLevel) {
                    detectTapGestures(
                        onTap = { tap ->
                            val edgeWidth = 48.dp.toPx()
                            val tappedLevelEdge = tap.x <= edgeWidth || tap.x >= size.width - edgeWidth
                            if (inputFieldFocused) {
                                focusManager.clearFocus()
                                when {
                                    tap.x <= edgeWidth && currentLevel > 4 -> currentLevel--
                                    tap.x >= size.width - edgeWidth && currentLevel < 21 -> currentLevel++
                                }
                            } else {
                                when {
                                    tap.x <= edgeWidth && currentLevel > 4 -> {
                                        focusManager.clearFocus()
                                        currentLevel--
                                    }
                                    tap.x >= size.width - edgeWidth && currentLevel < 21 -> {
                                        focusManager.clearFocus()
                                        currentLevel++
                                    }
                                    !tappedLevelEdge -> focusManager.clearFocus()
                                }
                            }
                        }
                    )
                }
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top Header
            HeaderSection(onBatterySaverClick = {
                focusManager.clearFocus()
                batterySaverEnabled = true
            })

            // 2. Goal block beside the vertically stacked action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GoalMetricCard(
                    value = goal,
                    onValueChange = { goal = it },
                    onInputFocusChanged = { inputFieldFocused = it },
                    modifier = Modifier.weight(1f)
                )

                ActionButtonsColumn(
                    modifier = Modifier.weight(1f),
                    onResetRun = {
                        focusManager.clearFocus()
                        metricsByLevel.clear()
                        currentLevel = 4
                    },
                    onSaveRun = {
                        focusManager.clearFocus()
                        saveRunMenuVisible = true
                    }
                )
            }

            // 4. Row of 4 Metric Cards
            MetricCardsRow(
                score = currentMetrics.score,
                bonus = currentMetrics.bonus,
                death = currentMetrics.death,
                currentLevel = currentLevel,
                onScoreChange = {
                    metricsByLevel[currentLevel] =
                        (metricsByLevel[currentLevel] ?: LevelMetrics()).copy(score = it)
                },
                onBonusChange = {
                    metricsByLevel[currentLevel] =
                        (metricsByLevel[currentLevel] ?: LevelMetrics()).copy(bonus = it)
                },
                onDeathChange = {
                    metricsByLevel[currentLevel] =
                        (metricsByLevel[currentLevel] ?: LevelMetrics()).copy(death = it)
                },
                onInputFocusChanged = { inputFieldFocused = it }
            )

            // 5. Current Run Grid Card
            CurrentRunCard(
                pace = pace?.formatMetric() ?: "---",
                currentAverage = currentAverage?.formatMetric() ?: "---",
                nextLevelCurrentAverage = nextLevelCurrentAverage?.formatMetric() ?: "---",
                neededAverage = neededAverage?.formatMetric() ?: "---",
                nextLevelNeededAverage = nextLevelNeededAverage?.formatMetric() ?: "---",
                pointsInThisLevel = pointsInThisLevel?.formatMetric() ?: "---",
                progressColor = progressColor
            )

            // 6. Pace Over Time Chart Card
            PaceChartCard(
                points = paceHistory,
                goal = goal.takeIf { it.isNotBlank() }?.toMetricLong(),
                deathpoints = metricsByLevel.values.sumOf { metrics ->
                    metrics.death.toMetricLong().takeIf { it > 0L } ?: 0L
                },
                selectedLevel = currentLevel,
                onLevelSelected = { currentLevel = it }
            )

            // Bottom Spacer
            Spacer(modifier = Modifier.height(8.dp))
        }
        }

        if (saveRunMenuVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { focusManager.clearFocus() },
                contentAlignment = Alignment.Center
            ) {
                SaveRunMenu(
                    runName = saveRunName,
                    onRunNameChange = { saveRunName = it },
                    startScore = startScore,
                    score = saveRunScore,
                    level = latestScoredLevel,
                    pace = saveRunPace,
                    average = saveRunAverage,
                    averageColor = saveRunProgressColor,
                    onDismiss = { saveRunMenuVisible = false },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp)
                )
            }
        }
    }
}

private fun String.toMetricLong(): Long =
    filter(Char::isDigit).toLongOrNull()?.roundToHundred() ?: 0L

private fun Long.formatMetric(): String {
    val value = roundToHundred().toString()
    val isNegative = value.startsWith('-')
    val digits = value.removePrefix("-").reversed().chunked(3).joinToString(".").reversed()
    return if (isNegative) "-$digits" else digits
}

private fun Long.roundToHundred(): Long = ((this + 50) / 100) * 100

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
