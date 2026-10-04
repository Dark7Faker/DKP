package com.example.dkpace.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dkpace.ui.components.ActionButtonsColumn
import com.example.dkpace.ui.components.CurrentRunCard
import com.example.dkpace.ui.components.HeaderSection
import com.example.dkpace.ui.components.MetricCardsRow
import com.example.dkpace.ui.components.GoalMetricCard
import com.example.dkpace.ui.components.LevelMetrics
import com.example.dkpace.ui.components.MainNavigationBar
import com.example.dkpace.ui.components.MainMenu
import com.example.dkpace.ui.components.AnalyzeRunsScreen
import com.example.dkpace.ui.components.RunSort
import com.example.dkpace.ui.components.RunSortControl
import com.example.dkpace.ui.components.SavedRun
import com.example.dkpace.ui.components.PaceChartCard
import com.example.dkpace.ui.components.PacePoint
import com.example.dkpace.ui.components.SaveRunMenu
import com.example.dkpace.ui.theme.ArcadeColors
import kotlin.math.roundToLong
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

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
    var editingRunIndex by remember { mutableIntStateOf(-1) }
    var editingRunName by remember { mutableStateOf("") }
    var selectedMenu by remember { mutableStateOf(MainMenu.TRACKER) }
    var selectedRun by remember { mutableStateOf<SavedRun?>(null) }
    var runSort by remember { mutableStateOf(RunSort.DATE) }
    var savedRuns by remember {
        mutableStateOf(loadSavedRuns(preferences.getString("saved_runs", null)))
    }
    LaunchedEffect(selectedMenu, selectedRun) {
        scrollState.scrollTo(0)
    }
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
    val saveRunNeededAverage = latestScoredLevel
        ?.takeIf { it != 4 && it < 21 && goal.isNotBlank() }
        ?.let { level ->
            latestScoredMetrics
                ?.takeIf { it.score.isNotBlank() }
                ?.let { metrics ->
                    (goal.toMetricLong() - metrics.score.toMetricLong() - metrics.bonus.toMetricLong())
                        .toDouble() / (21 - level)
                }
        }
        ?.takeIf { it > 0 }
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
                .pointerInput(currentLevel, selectedMenu) {
                    detectTapGestures(
                        onTap = { tap ->
                            if (selectedMenu == MainMenu.TRACKER) {
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
                        }
                    )
                }
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedMenu == MainMenu.ANALYZE) {
                val runDetails = selectedRun
                if (runDetails != null) {
                    var runLevel by remember(runDetails.savedAt, runDetails.name) {
                        mutableIntStateOf(runDetails.level ?: 4)
                    }
                    val runMetrics = runDetails.levelMetrics
                    val runCurrentMetrics = runMetrics[runLevel] ?: LevelMetrics()
                    val runStartMetrics = runMetrics[4] ?: LevelMetrics()
                    val runStart = runStartMetrics.score.toMetricLong() + runStartMetrics.bonus.toMetricLong()
                    val runScoreExists = runCurrentMetrics.score.isNotBlank()
                    val runPaceValue = if (runLevel == 4 || !runScoreExists) null else {
                        val net = runCurrentMetrics.score.toMetricLong() + runCurrentMetrics.bonus.toMetricLong() -
                            runCurrentMetrics.death.toMetricLong()
                        (((net - runStart).toDouble() / (runLevel - 4)) * 17 + 700 + runStart)
                            .takeIf { it >= 0 }?.roundToLong()
                    }
                    val runAverageValue = if (runLevel == 4 || !runScoreExists) null else {
                        (runCurrentMetrics.score.toMetricLong() + runCurrentMetrics.bonus.toMetricLong() -
                            runStart - runCurrentMetrics.death.toMetricLong()).toDouble() / (runLevel - 4)
                    }
                    val runAverage = runAverageValue?.takeIf { it > 4 }?.roundToLong()
                    val runNextCurrent = if (runAverage == null || runAverageValue == null) null else {
                        (runCurrentMetrics.score.toMetricLong() + runCurrentMetrics.bonus.toMetricLong() + runAverageValue)
                            .takeIf { it >= 0 }?.roundToLong()
                    }
                    val runNeededValue = if (runDetails.goal.isBlank() || !runScoreExists || runLevel >= 21) null else {
                        (runDetails.goal.toMetricLong() - runCurrentMetrics.score.toMetricLong() -
                            runCurrentMetrics.bonus.toMetricLong()).toDouble() / (21 - runLevel)
                    }
                    val runNeeded = runNeededValue?.takeIf { it > 0 }?.roundToLong()
                    val runNextNeeded = if (runNeeded == null || runNeededValue == null) null else {
                        (runCurrentMetrics.score.toMetricLong() + runNeededValue).takeIf { it > 0 }?.roundToLong()
                    }
                    val runPreviousMetrics = runMetrics[runLevel - 1] ?: LevelMetrics()
                    val runPointsThisLevel = if (
                        runLevel == 4 || !runScoreExists || runPreviousMetrics.score.isBlank()
                    ) null else {
                        (runCurrentMetrics.score.toMetricLong() + runCurrentMetrics.bonus.toMetricLong() -
                            runCurrentMetrics.death.toMetricLong() - runPreviousMetrics.score.toMetricLong() -
                            runPreviousMetrics.bonus.toMetricLong()).takeIf { it >= 0L }
                    }
                    val runPoints = paceHistoryFor(runMetrics)
                    val displayedRunPace = if (runMetrics.isEmpty()) runDetails.pace else runPaceValue?.formatMetric() ?: "---"
                    val displayedRunAverage = if (runMetrics.isEmpty()) runDetails.average else runAverage?.formatMetric() ?: "---"
                    val displayedRunNeeded = if (runMetrics.isEmpty()) runDetails.neededAverage else runNeeded?.formatMetric() ?: "---"
                    HeaderSection(
                        title = runDetails.name,
                        titleIcon = Icons.Default.Edit,
                        showBatterySaver = false,
                        trailingContent = {
                            Box(
                                modifier = Modifier
                                    .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
                                    .border(1.dp, ArcadeColors.CyanBorder, RoundedCornerShape(12.dp))
                                    .padding(2.dp)
                            ) {
                                IconButton(onClick = { selectedRun = null }, modifier = Modifier.size(32.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Back to Analyze Runs",
                                        tint = ArcadeColors.TextWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RunDetailActionButton(
                                label = "DELETE",
                                icon = Icons.Default.Delete,
                                color = ArcadeColors.RedAccent,
                                withBorder = true,
                                onClick = {
                                    val deleteIndex = savedRuns.indexOf(runDetails)
                                    if (deleteIndex >= 0) {
                                        val updatedRuns = savedRuns.filterIndexed { index, _ -> index != deleteIndex }
                                        savedRuns = updatedRuns
                                        preferences.edit().putString("saved_runs", saveRunsJson(updatedRuns)).apply()
                                    }
                                    selectedRun = null
                                }
                            )
                            RunDetailActionButton(
                                label = "EDIT",
                                icon = Icons.Default.Edit,
                                color = ArcadeColors.GreenAccent,
                                onClick = {
                                    editingRunIndex = savedRuns.indexOf(runDetails)
                                    editingRunName = runDetails.name
                                }
                            )
                        }
                    }
                    CurrentRunCard(
                        pace = displayedRunPace,
                        currentAverage = displayedRunAverage,
                        nextLevelCurrentAverage = runNextCurrent?.formatMetric() ?: "---",
                        neededAverage = displayedRunNeeded,
                        nextLevelNeededAverage = runNextNeeded?.formatMetric() ?: "---",
                        pointsInThisLevel = runPointsThisLevel?.formatMetric() ?: "---",
                        progressColor = if (
                            (runMetrics.isEmpty() && runDetails.averageIsRed) ||
                            (runPaceValue != null && runDetails.goal.isNotBlank() &&
                                runPaceValue.roundToHundred() < runDetails.goal.toMetricLong())
                        ) {
                            ArcadeColors.RedAccent
                        } else {
                            ArcadeColors.GreenAccent
                        },
                        title = "RUN STATISTIC"
                    )
                    PaceChartCard(
                        points = runPoints,
                        goal = runDetails.goal.takeIf { it.isNotBlank() }?.toMetricLong(),
                        deathpoints = runMetrics.values.sumOf { it.death.toMetricLong().coerceAtLeast(0L) },
                        selectedLevel = runLevel,
                        onLevelSelected = { runLevel = it }
                    )
                } else {
                    HeaderSection(
                        title = "ANALYZE RUNS",
                        titleIcon = Icons.Default.Visibility,
                        showBatterySaver = false,
                        trailingContent = {
                            RunSortControl(sort = runSort, onSortSelected = { runSort = it })
                        },
                        onBatterySaverClick = {
                            focusManager.clearFocus()
                            batterySaverEnabled = true
                        }
                    )
                    AnalyzeRunsScreen(
                        runs = savedRuns,
                        sort = runSort,
                        onRunClick = { selectedRun = it }
                    )
                }
            } else {
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
        }

        MainNavigationBar(
            selectedMenu = selectedMenu,
            onMenuSelected = {
                selectedMenu = it
                selectedRun = null
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        val editingRun = savedRuns.getOrNull(editingRunIndex)
        if (saveRunMenuVisible || editingRun != null) {
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
                    runName = if (editingRun != null) editingRunName else saveRunName,
                    onRunNameChange = {
                        if (editingRun != null) editingRunName = it else saveRunName = it
                    },
                    startScore = if (editingRun != null) {
                        editingRun.levelMetrics[4]?.score?.takeIf { it.isNotBlank() }
                            ?.let { it.toMetricLong().formatMetric() } ?: "---"
                    } else startScore,
                    score = editingRun?.score ?: saveRunScore,
                    level = if (editingRun != null) editingRun.level else latestScoredLevel,
                    pace = editingRun?.pace ?: saveRunPace,
                    average = editingRun?.average ?: saveRunAverage,
                    averageColor = if (editingRun != null && editingRun.averageIsRed) {
                        ArcadeColors.RedAccent
                    } else if (editingRun != null) {
                        ArcadeColors.GreenAccent
                    } else saveRunProgressColor,
                    onDismiss = {
                        saveRunMenuVisible = false
                        editingRunIndex = -1
                    },
                    onSave = { enteredScore, date ->
                        if (editingRun != null) {
                            val updatedRun = editingRun.copy(
                                name = editingRunName.trim().ifBlank { enteredScore },
                                score = enteredScore,
                                date = date.toString()
                            )
                            savedRuns = savedRuns.mapIndexed { index, run ->
                                if (index == editingRunIndex) updatedRun else run
                            }
                            selectedRun = updatedRun
                            editingRunIndex = -1
                        } else {
                            val run = SavedRun(
                                name = saveRunName.trim().ifBlank { enteredScore },
                                score = enteredScore,
                                date = date.toString(),
                                pace = saveRunPace,
                                level = latestScoredLevel,
                                average = saveRunAverage,
                                savedAt = System.currentTimeMillis(),
                                averageIsRed = saveRunProgressColor == ArcadeColors.RedAccent,
                                neededAverage = saveRunNeededAverage,
                                goal = goal,
                                levelMetrics = metricsByLevel.toMap()
                            )
                            savedRuns = savedRuns + run
                            saveRunMenuVisible = false
                        }
                        preferences.edit().putString("saved_runs", saveRunsJson(savedRuns)).apply()
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
                    title = if (editingRun != null) "EDIT RUN" else "SAVE RUN",
                    initialDate = editingRun?.date?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                        ?: LocalDate.now()
                )
            }
        }
    }
}

@Composable
private fun RunDetailActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    withBorder: Boolean = false
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .width(112.dp)
            .height(40.dp)
            .background(color, shape)
            .then(if (withBorder) Modifier.border(1.5.dp, color, shape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
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

private fun paceHistoryFor(metricsByLevel: Map<Int, LevelMetrics>): List<PacePoint> {
    val startingMetrics = metricsByLevel[4] ?: LevelMetrics()
    val start = startingMetrics.score.toMetricLong() + startingMetrics.bonus.toMetricLong()
    return (5..21).mapNotNull { level ->
        val metrics = metricsByLevel[level] ?: return@mapNotNull null
        if (metrics.score.isBlank()) return@mapNotNull null
        val net = metrics.score.toMetricLong() + metrics.bonus.toMetricLong() - metrics.death.toMetricLong()
        val pace = ((net - start).toDouble() / (level - 4)) * 17 + 700 + start
        if (pace < 0) return@mapNotNull null
        PacePoint(
            level = level,
            pace = pace.roundToLong().roundToHundred(),
            death = metrics.death.toMetricLong(),
            hasDeathEntry = metrics.death.isNotBlank()
        )
    }
}

private fun loadSavedRuns(json: String?): List<SavedRun> = runCatching {
    val array = JSONArray(json ?: "[]")
    List(array.length()) { index ->
        val item = array.getJSONObject(index)
        SavedRun(
            name = item.optString("name"),
            score = item.optString("score"),
            date = item.optString("date"),
            pace = item.optString("pace", "---"),
            level = if (item.isNull("level")) null else item.optInt("level"),
            average = item.optString("average", "---"),
            savedAt = item.optLong("savedAt", 0L),
            averageIsRed = item.optBoolean("averageIsRed", false),
            neededAverage = item.optString("neededAverage", "---"),
            goal = item.optString("goal", ""),
            levelMetrics = item.optJSONArray("levelMetrics")?.let { metricsArray ->
                buildMap {
                    for (metricIndex in 0 until metricsArray.length()) {
                        val metric = metricsArray.getJSONObject(metricIndex)
                        put(
                            metric.optInt("level"),
                            LevelMetrics(
                                score = metric.optString("score", ""),
                                bonus = metric.optString("bonus", ""),
                                death = metric.optString("death", "")
                            )
                        )
                    }
                }
            }.orEmpty()
        )
    }
}.getOrDefault(emptyList())

private fun saveRunsJson(runs: List<SavedRun>): String = JSONArray().apply {
    runs.forEach { run ->
        put(JSONObject().apply {
            put("name", run.name)
            put("score", run.score)
            put("date", run.date)
            put("pace", run.pace)
            put("level", run.level ?: JSONObject.NULL)
            put("average", run.average)
            put("savedAt", run.savedAt)
            put("averageIsRed", run.averageIsRed)
            put("neededAverage", run.neededAverage)
            put("goal", run.goal)
            put("levelMetrics", JSONArray().apply {
                run.levelMetrics.toSortedMap().forEach { (level, metrics) ->
                    put(JSONObject().apply {
                        put("level", level)
                        put("score", metrics.score)
                        put("bonus", metrics.bonus)
                        put("death", metrics.death)
                    })
                }
            })
        })
    }
}.toString()

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
