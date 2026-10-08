package com.darkfaker.dkp.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.view.ContextThemeWrapper
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.core.content.edit
import com.darkfaker.dkp.ui.components.ActionButtonsColumn
import com.darkfaker.dkp.ui.components.CurrentRunCard
import com.darkfaker.dkp.ui.components.HeaderSection
import com.darkfaker.dkp.ui.components.MetricCardsRow
import com.darkfaker.dkp.ui.components.RunLevelMetricsRow
import com.darkfaker.dkp.ui.components.GoalMetricCard
import com.darkfaker.dkp.ui.components.LevelMetrics
import com.darkfaker.dkp.ui.components.MainMenu
import com.darkfaker.dkp.ui.components.AnalyzeRunsScreen
import com.darkfaker.dkp.ui.components.RunSort
import com.darkfaker.dkp.ui.components.RunSortControl
import com.darkfaker.dkp.ui.components.SavedRun
import com.darkfaker.dkp.ui.components.PaceChartCard
import com.darkfaker.dkp.ui.components.PacePoint
import com.darkfaker.dkp.ui.components.PersonalBestChart
import com.darkfaker.dkp.ui.components.PersonalBestPoint
import com.darkfaker.dkp.ui.components.SaveRunDisplayCard
import com.darkfaker.dkp.ui.components.SaveRunMenu
import com.darkfaker.dkp.ui.components.formatDeviceDate
import com.darkfaker.dkp.ui.theme.ArcadeColors
import com.darkfaker.dkp.R
import kotlin.math.roundToLong
import kotlin.math.abs
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ArcadeTrackerScreen() {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
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
        // Keep the legacy filename only to migrate existing users' saved data.
        //noinspection SpellCheckingInspection
        val oldPreferences = context.getSharedPreferences("dk_pace_preferences", Context.MODE_PRIVATE)
        val currentPreferences = context.getSharedPreferences("dk_pace_preferences", Context.MODE_PRIVATE)
        if (!currentPreferences.contains("goal") || !currentPreferences.contains("saved_runs")) {
            currentPreferences.edit {
                if (!currentPreferences.contains("goal") && oldPreferences.contains("goal")) {
                    putString("goal", oldPreferences.getString("goal", null))
                }
                if (!currentPreferences.contains("saved_runs") && oldPreferences.contains("saved_runs")) {
                    putString("saved_runs", oldPreferences.getString("saved_runs", null))
                }
            }
        }
        currentPreferences
    }
    var currentLevel by remember { mutableIntStateOf(4) }
    var batterySaverEnabled by remember { mutableStateOf(false) }
    var inputFieldFocused by remember { mutableStateOf(false) }
    var saveRunMenuVisible by remember { mutableStateOf(false) }
    var editingRunIndex by remember { mutableIntStateOf(-1) }
    var editingRunName by remember { mutableStateOf("") }
    var editingRunStatistics by remember { mutableStateOf(false) }
    var editingRunDraftScore by remember { mutableStateOf("") }
    var editingRunDraftDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedMenu by remember { mutableStateOf(MainMenu.TRACKER) }
    var selectedRun by remember { mutableStateOf<SavedRun?>(null) }
    var showingPersonalBests by remember { mutableStateOf(false) }
    var trackerHasBeenOpened by remember { mutableStateOf(false) }
    var selectedPersonalBestRun by remember { mutableStateOf<SavedRun?>(null) }
    var runSort by remember { mutableStateOf(RunSort.DATE) }
    var savedRuns by remember {
        mutableStateOf(loadSavedRuns(preferences.getString("saved_runs", null)))
    }
    LaunchedEffect(selectedMenu, selectedRun) {
        scrollState.scrollTo(0)
    }
    LaunchedEffect(selectedMenu, selectedRun, showingPersonalBests) {
        if (selectedMenu != MainMenu.TRACKER || selectedRun != null || showingPersonalBests) {
            trackerHasBeenOpened = true
        }
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
    LaunchedEffect(goal, editingRunStatistics) {
        if (editingRunStatistics) return@LaunchedEffect
        val roundedGoal = goal.toMetricLong()
        preferences.edit {
            if (roundedGoal > 0L) {
                putString("goal", roundedGoal.formatMetric())
            } else {
                remove("goal")
            }
        }
    }
    val metricsByLevel = remember { mutableStateMapOf<Int, LevelMetrics>() }
    var trackerMetricsBeforeRunEdit by remember { mutableStateOf<Map<Int, LevelMetrics>?>(null) }
    var trackerGoalBeforeRunEdit by remember { mutableStateOf<String?>(null) }
    var trackerLevelBeforeRunEdit by remember { mutableIntStateOf(4) }

    fun returnFromRunStatisticsEdit() {
        metricsByLevel.clear()
        trackerMetricsBeforeRunEdit.orEmpty().forEach { (level, metrics) ->
            metricsByLevel[level] = metrics
        }
        goal = trackerGoalBeforeRunEdit.orEmpty()
        currentLevel = trackerLevelBeforeRunEdit
        inputFieldFocused = false
        editingRunStatistics = false
        editingRunIndex = -1
        editingRunDraftDate = null
        saveRunMenuVisible = false
        selectedMenu = MainMenu.ANALYZE
        focusManager.clearFocus()
    }

    fun beginRunStatisticsEdit(run: SavedRun) {
        focusManager.clearFocus()
        val latestSelectedRun = selectedRun?.let { selected -> savedRuns.firstOrNull { it == selected } }
        val currentRun = latestSelectedRun
            ?: savedRuns.firstOrNull { run.savedAt != 0L && it.savedAt == run.savedAt }
            ?: run
        val runIndex = savedRuns.indexOf(currentRun)
        editingRunIndex = runIndex
        editingRunName = currentRun.name
        editingRunDraftScore = currentRun.score
        editingRunDraftDate = runCatching { LocalDate.parse(currentRun.date) }.getOrNull()
        trackerMetricsBeforeRunEdit = metricsByLevel.toMap()
        trackerGoalBeforeRunEdit = goal
        trackerLevelBeforeRunEdit = currentLevel
        editingRunStatistics = true
        metricsByLevel.clear()
        metricsByLevel.putAll(currentRun.levelMetrics)
        goal = currentRun.goal
        val initialRunLevel = currentRun.level?.minus(if (currentRun.levelIsEndLevel) 1 else 0) ?: 4
        currentLevel = initialRunLevel.coerceIn(4, 21)
        inputFieldFocused = false
        selectedMenu = MainMenu.TRACKER
        saveRunMenuVisible = false
    }


    val currentMetrics = metricsByLevel[currentLevel] ?: LevelMetrics()
    val startingMetrics = metricsByLevel[4] ?: LevelMetrics()
    val start = startingMetrics.score.toMetricLong() + startingMetrics.bonus.toMetricLong()
    val paceHistory = (5..21).mapNotNull { level ->
        val metrics = metricsByLevel[level] ?: return@mapNotNull null
        if (metrics.score.isBlank()) return@mapNotNull null

        val total = metrics.score.toMetricLong() +
            metrics.bonus.toMetricLong() -
            metrics.death.toMetricLong()
        val levelPace = ((total - start).toDouble() / (level - 4)) * (21 - level) +
            metrics.score.toMetricLong() + metrics.bonus.toMetricLong() + 700
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
    val saveRunEndLevel = latestScoredLevel?.plus(1)
    val latestScoredMetrics = metricsByLevel[latestScoredLevel ?: -1]
    val startScore = startingMetrics.score
        .takeIf { it.isNotBlank() && it.toMetricLong() >= 0L }
        ?.toMetricLong()
        ?.formatMetric() ?: "---"
    val saveRunScore = latestScoredMetrics
        ?.takeIf { it.score.isNotBlank() && it.score.toMetricLong() >= 0L }
        ?.let { metrics ->
            (metrics.score.toMetricLong() + metrics.bonus.toMetricLong())
                .roundToHundred()
                .formatMetric()
        }
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
                    (goal.toMetricLong() - metrics.score.toMetricLong() - metrics.bonus.toMetricLong() - 700L)
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
        val calculatedPace = ((currentTotal - start).toDouble() / (currentLevel - 4)) *
            (21 - currentLevel) + currentMetrics.score.toMetricLong() + currentMetrics.bonus.toMetricLong() + 700
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
    val nextLevelCurrentAverage = averageForLevel?.takeIf { currentLevel < 21 && it > 4 }?.let { average ->
        val nextAverage = currentMetrics.score.toMetricLong() +
            currentMetrics.bonus.toMetricLong() + average
        nextAverage.takeIf { it >= 0 }?.roundToLong()
    }
    val neededAverageValue = if (goal.isBlank() || currentMetrics.score.isBlank() || currentLevel >= 21) {
        null
    } else {
        val remainingPoints = goal.toMetricLong() -
            currentMetrics.score.toMetricLong() -
            currentMetrics.bonus.toMetricLong() -
            700L
        remainingPoints.toDouble() / (21 - currentLevel)
    }
    val neededAverage = neededAverageValue?.takeIf { it > 0 }?.roundToLong()
    val nextLevelNeededAverage = neededAverageValue?.takeIf { it > 0 }?.let { needed ->
        val nextNeeded = currentMetrics.score.toMetricLong() + needed
        nextNeeded.takeIf { it > 0 }?.roundToLong()
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
                .pointerInput(selectedMenu, selectedRun, showingPersonalBests, saveRunMenuVisible, editingRunStatistics) {
                    var totalHorizontalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { totalHorizontalDrag = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            totalHorizontalDrag += dragAmount
                        },
                        onDragEnd = {
                            val swipeThreshold = 72.dp.toPx()
                            if (!saveRunMenuVisible && !editingRunStatistics &&
                                abs(totalHorizontalDrag) >= swipeThreshold
                            ) {
                                focusManager.clearFocus()
                                selectedMenu = if (totalHorizontalDrag > 0f) MainMenu.ANALYZE else MainMenu.TRACKER
                                selectedRun = null
                                showingPersonalBests = false
                                selectedPersonalBestRun = null
                            }
                        }
                    )
                }
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
                            } else if (selectedMenu == MainMenu.ANALYZE) {
                                focusManager.clearFocus()
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
                    val updateSelectedRun: ((SavedRun) -> SavedRun) -> Unit = { update ->
                        val runIndex = savedRuns.indexOf(runDetails)
                        if (runIndex >= 0) {
                            val updatedRun = update(runDetails)
                            val updatedRuns = savedRuns.toMutableList().apply { set(runIndex, updatedRun) }
                            savedRuns = updatedRuns
                            selectedRun = updatedRun
                            preferences.edit { putString("saved_runs", saveRunsJson(updatedRuns)) }
                        }
                    }
                    var runLevel by remember(runDetails.savedAt) {
                        val initialLevel = runDetails.level?.minus(if (runDetails.levelIsEndLevel) 1 else 0) ?: 4
                        mutableIntStateOf(initialLevel.coerceAtLeast(4))
                    }
                    val runMetrics = runDetails.levelMetrics
                    val runCurrentMetrics = runMetrics[runLevel] ?: LevelMetrics()
                    val runStartMetrics = runMetrics[4] ?: LevelMetrics()
                    val runStart = runStartMetrics.score.toMetricLong() + runStartMetrics.bonus.toMetricLong()
                    val runEndLevel = runMetrics
                        .filter { (_, metrics) -> metrics.score.isNotBlank() && metrics.score.toMetricLong() >= 0L }
                        .keys
                        .maxOrNull()
                    val runEndMetrics = runMetrics[runEndLevel ?: -1]
                    val runEndAverageValue = if (runEndLevel != null && runEndLevel > 4 && runEndMetrics != null) {
                        (
                            runEndMetrics.score.toMetricLong() + runEndMetrics.bonus.toMetricLong() -
                                runStart - runEndMetrics.death.toMetricLong()
                            ).toDouble() / (runEndLevel - 4)
                    } else null
                    val runEndPaceValue = if (runEndLevel != null && runEndLevel > 4 && runEndMetrics != null) {
                        val net = runEndMetrics.score.toMetricLong() + runEndMetrics.bonus.toMetricLong() -
                            runEndMetrics.death.toMetricLong()
                        (((net - runStart).toDouble() / (runEndLevel - 4)) * (21 - runEndLevel) +
                            runEndMetrics.score.toMetricLong() + runEndMetrics.bonus.toMetricLong() + 700)
                            .takeIf { it >= 0 }?.roundToLong()
                    } else null
                    val runEndPace = if (runMetrics.isEmpty()) {
                        runDetails.pace
                    } else {
                        runEndPaceValue?.roundToHundred()?.formatMetric() ?: "---"
                    }
                    val runEndAverage = if (runMetrics.isEmpty()) {
                        runDetails.average
                    } else {
                        runEndAverageValue?.takeIf { it > 4 }?.roundToLong()?.roundToHundred()?.formatMetric() ?: "---"
                    }
                    val calculatedRunEndScore = if (runMetrics.isEmpty()) {
                        runDetails.score.ifBlank { "---" }
                    } else {
                        runEndMetrics
                            ?.takeIf { it.score.isNotBlank() }
                            ?.let { (it.score.toMetricLong() + it.bonus.toMetricLong()).roundToHundred().formatMetric() }
                            ?: "---"
                    }
                    val runEndScore = if (runDetails.scoreManuallyEdited) {
                        runDetails.score.ifBlank { "---" }
                    } else {
                        calculatedRunEndScore
                    }
                    val runStartDisplay = if (
                        runStartMetrics.score.isNotBlank() || runStartMetrics.bonus.isNotBlank()
                    ) runStart.roundToHundred().formatMetric() else "---"
                    val runStatisticDate = runCatching {
                        formatDeviceDate(LocalDate.parse(runDetails.date))
                    }.getOrDefault(runDetails.date)
                    val runScoreExists = runCurrentMetrics.score.isNotBlank()
                    val runPaceValue = if (runLevel == 4 || !runScoreExists) null else {
                        val net = runCurrentMetrics.score.toMetricLong() + runCurrentMetrics.bonus.toMetricLong() -
                            runCurrentMetrics.death.toMetricLong()
                        (((net - runStart).toDouble() / (runLevel - 4)) * (21 - runLevel) +
                            runCurrentMetrics.score.toMetricLong() + runCurrentMetrics.bonus.toMetricLong() + 700)
                            .takeIf { it >= 0 }?.roundToLong()
                    }
                    val runAverageValue = if (runLevel == 4 || !runScoreExists) null else {
                        (runCurrentMetrics.score.toMetricLong() + runCurrentMetrics.bonus.toMetricLong() -
                            runStart - runCurrentMetrics.death.toMetricLong()).toDouble() / (runLevel - 4)
                    }
                    val runAverage = runAverageValue?.takeIf { it > 4 }?.roundToLong()
                    val runNextCurrent = runAverageValue?.takeIf { runLevel < 21 && it > 4 }?.let { average ->
                        (runCurrentMetrics.score.toMetricLong() + runCurrentMetrics.bonus.toMetricLong() + average)
                            .takeIf { it >= 0 }?.roundToLong()
                    }
                    val runNeededValue = if (runDetails.goal.isBlank() || !runScoreExists || runLevel >= 21) null else {
                        (runDetails.goal.toMetricLong() - runCurrentMetrics.score.toMetricLong() -
                            runCurrentMetrics.bonus.toMetricLong() - 700L).toDouble() / (21 - runLevel)
                    }
                    val runNeeded = runNeededValue?.takeIf { it > 0 }?.roundToLong()
                    val runNextNeeded = runNeededValue?.takeIf { it > 0 }?.let { needed ->
                        (runCurrentMetrics.score.toMetricLong() + needed).takeIf { it > 0 }?.roundToLong()
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
                        title = "ANALYZE RUNS",
                        titleIcon = Icons.Default.Visibility,
                        showBatterySaver = false,
                        trailingContent = {
                            Box(
                                modifier = Modifier
                                    .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
                                    .border(1.dp, ArcadeColors.CyanBorder, RoundedCornerShape(12.dp))
                                    .padding(2.dp)
                            ) {
                                IconButton(onClick = {
                                    focusManager.clearFocus()
                                    selectedRun = null
                                }, modifier = Modifier.size(32.dp)) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GoalMetricCard(
                            value = runDetails.goal,
                            onValueChange = { value ->
                                updateSelectedRun { it.copy(goal = value) }
                            },
                            onInputFocusChanged = { inputFieldFocused = it },
                            modifier = Modifier.weight(1f)
                        )
                        ActionButtonsColumn(
                            modifier = Modifier.weight(1f),
                            saveLabel = "EDIT",
                            saveIcon = Icons.Default.Edit,
                            resetLabel = "DELETE",
                            resetIcon = Icons.Default.Delete,
                            onSaveRun = {
                                beginRunStatisticsEdit(runDetails)
                            },
                            onResetRun = {
                                focusManager.clearFocus()
                                val latestSelectedRun = selectedRun?.let { selected ->
                                    savedRuns.firstOrNull { it == selected }
                                } ?: runDetails
                                val deleteIndex = savedRuns.indexOf(latestSelectedRun)
                                if (deleteIndex >= 0) {
                                    val updatedRuns = savedRuns.filterIndexed { index, _ -> index != deleteIndex }
                                    savedRuns = updatedRuns
                                    preferences.edit { putString("saved_runs", saveRunsJson(updatedRuns)) }
                                }
                                selectedRun = null
                                selectedPersonalBestRun = null
                            }
                        )
                    }
                    SaveRunDisplayCard(
                        title = "RUN STATISTICS",
                        runName = runDetails.name,
                        onRunNameChange = { name ->
                            updateSelectedRun { it.copy(name = name) }
                        },
                        date = runStatisticDate,
                        onDateClick = {
                            focusManager.clearFocus()
                            val currentDate = runCatching { LocalDate.parse(runDetails.date) }
                                .getOrDefault(LocalDate.now(ZoneId.systemDefault()))
                            DatePickerDialog(
                                ContextThemeWrapper(context, R.style.DatePickerDialogTheme),
                                { _, year, month, day ->
                                    updateSelectedRun {
                                        it.copy(date = LocalDate.of(year, month + 1, day).toString())
                                    }
                                },
                                currentDate.year,
                                currentDate.monthValue - 1,
                                currentDate.dayOfMonth
                            ).show()
                        },
                        startScore = runStartDisplay,
                        score = if (runDetails.scoreManuallyEdited) runDetails.score else runEndScore,
                        onScoreChange = { score ->
                            updateSelectedRun { it.copy(score = score, scoreManuallyEdited = true) }
                        },
                        level = runDetails.level,
                        pace = runEndPace,
                        average = runEndAverage,
                        averageColor = if (
                            (runMetrics.isEmpty() && runDetails.averageIsRed) ||
                                (runEndPaceValue != null && runDetails.goal.isNotBlank() &&
                                    runEndPaceValue.roundToHundred() < runDetails.goal.toMetricLong())
                        ) ArcadeColors.RedAccent else ArcadeColors.GreenAccent
                    )
                    PaceChartCard(
                        points = runPoints,
                        goal = runDetails.goal.takeIf { it.isNotBlank() }?.toMetricLong(),
                        deathpoints = runMetrics.values.sumOf { it.death.toMetricLong().coerceAtLeast(0L) },
                        selectedLevel = runLevel,
                        onLevelSelected = {
                            focusManager.clearFocus()
                            runLevel = it
                        },
                        animateDeathpointsOnEnter = true
                    )
                    key(runDetails) {
                        RunLevelMetricsRow(
                            score = runCurrentMetrics.score,
                            bonus = runCurrentMetrics.bonus,
                            death = runCurrentMetrics.death,
                            currentLevel = runLevel
                        )
                    }
                    key(runDetails) {
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
                            title = "LEVEL STATISTICS"
                        )
                    }
                } else if (showingPersonalBests) {
                    val personalBestPoints = remember(savedRuns) {
                        var highestScore = -1L
                        savedRuns.mapNotNull { run ->
                            val score = run.score.toMetricLong()
                            val date = runCatching { LocalDate.parse(run.date) }.getOrNull()
                            if (date == null || score < 0L) null else Triple(run, date, score)
                        }
                            .sortedWith(compareBy<Triple<SavedRun, LocalDate, Long>> { it.second }
                                .thenBy { it.first.savedAt })
                            .mapNotNull { (run, date, score) ->
                                if (score <= highestScore) return@mapNotNull null
                                highestScore = score
                                PersonalBestPoint(
                                    dateEpochDay = date.toEpochDay(),
                                    score = score,
                                    run = run
                                )
                            }
                    }
                    HeaderSection(
                        title = "PB IMPROVEMENT",
                        titleIcon = Icons.Default.EmojiEvents,
                        showBatterySaver = false,
                        trailingContent = {
                            Box(
                                modifier = Modifier
                                    .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
                                    .border(1.dp, ArcadeColors.CyanBorder, RoundedCornerShape(12.dp))
                                    .height(35.dp)
                                    .padding(2.dp)
                            ) {
                                IconButton(onClick = {
                                    focusManager.clearFocus()
                                    showingPersonalBests = false
                                    selectedPersonalBestRun = null
                                }, modifier = Modifier.size(32.dp)) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back to Analyze Runs",
                                        tint = ArcadeColors.TextWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    )
                    PersonalBestChart(
                        points = personalBestPoints,
                        selectedRun = selectedPersonalBestRun,
                        onPointSelected = { selectedPersonalBestRun = it }
                    )
                    selectedPersonalBestRun?.let { run ->
                        AnalyzeRunsScreen(
                            runs = listOf(run),
                            sort = runSort,
                            onRunClick = { selectedRun = it }
                        )
                    }
                } else {
                    HeaderSection(
                        title = "ANALYZE RUNS",
                        titleIcon = Icons.Default.Visibility,
                        showBatterySaver = false,
                        trailingContent = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
                                        .border(1.dp, ArcadeColors.CyanBorder, RoundedCornerShape(12.dp))
                                        .padding(2.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            focusManager.clearFocus()
                                            showingPersonalBests = true
                                            selectedPersonalBestRun = null
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = "PB Improvement",
                                            tint = ArcadeColors.TextWhite,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                RunSortControl(sort = runSort, onSortSelected = { runSort = it })
                            }
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
            HeaderSection(
                title = if (editingRunStatistics) "DK PACE TRACKER - EDIT" else "DK PACE TRACKER",
                showBatterySaver = !editingRunStatistics,
                onBatterySaverClick = {
                    focusManager.clearFocus()
                    batterySaverEnabled = true
                }
            )

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
                    modifier = Modifier.weight(1f),
                    animateOnEnter = trackerHasBeenOpened
                )

                ActionButtonsColumn(
                    modifier = Modifier.weight(1f),
                    onResetRun = {
                        focusManager.clearFocus()
                        if (editingRunStatistics) {
                            returnFromRunStatisticsEdit()
                        } else {
                            metricsByLevel.clear()
                            currentLevel = 4
                        }
                    },
                    onSaveRun = {
                        focusManager.clearFocus()
                        if (editingRunStatistics) {
                            val editedRun = savedRuns.getOrNull(editingRunIndex)
                            if (editedRun != null) {
                                val updatedRun = editedRun.copy(
                                    name = editingRunName.trim().ifBlank { editingRunDraftScore },
                                    score = editingRunDraftScore,
                                    date = editingRunDraftDate?.toString() ?: editedRun.date,
                                    pace = saveRunPace,
                                    level = saveRunEndLevel,
                                    average = saveRunAverage,
                                    averageIsRed = saveRunProgressColor == ArcadeColors.RedAccent,
                                    neededAverage = saveRunNeededAverage,
                                    goal = goal,
                                    levelMetrics = metricsByLevel.toMap(),
                                    levelIsEndLevel = true,
                                    scoreManuallyEdited = false
                                )
                                val updatedRuns = savedRuns.mapIndexed { index, run ->
                                    if (index == editingRunIndex) updatedRun else run
                                }
                                savedRuns = updatedRuns
                                selectedRun = updatedRun
                                preferences.edit { putString("saved_runs", saveRunsJson(updatedRuns)) }
                            }
                            returnFromRunStatisticsEdit()
                        } else {
                            saveRunMenuVisible = true
                        }
                    },
                    resetLabel = if (editingRunStatistics) "CANCEL" else "RESET",
                    resetIcon = if (editingRunStatistics) Icons.Default.Close else Icons.Default.Refresh
                )
            }

            // 4. Row of 4 Metric Cards
            MetricCardsRow(
                score = currentMetrics.score,
                bonus = currentMetrics.bonus,
                death = currentMetrics.death,
                currentLevel = currentLevel,
                animateOnEnter = trackerHasBeenOpened,
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
                progressColor = progressColor,
                animateOnEnter = trackerHasBeenOpened
            )

            // 6. Pace Over Time Chart Card
            PaceChartCard(
                points = paceHistory,
                goal = goal.takeIf { it.isNotBlank() }?.toMetricLong(),
                deathpoints = metricsByLevel.values.sumOf { metrics ->
                    metrics.death.toMetricLong().takeIf { it > 0L } ?: 0L
                },
                selectedLevel = currentLevel,
                onLevelSelected = { currentLevel = it },
                animateDeathpointsOnEnter = trackerHasBeenOpened
            )

            // Bottom Spacer
            Spacer(modifier = Modifier.height(8.dp))
            }
        }
        }

        if (saveRunMenuVisible && !editingRunStatistics) {
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
                    level = saveRunEndLevel,
                    pace = saveRunPace,
                    average = saveRunAverage,
                    averageColor = saveRunProgressColor,
                    onDismiss = {
                        saveRunMenuVisible = false
                    },
                    onSave = { enteredScore, date ->
                        val run = SavedRun(
                            name = saveRunName.trim().ifBlank { enteredScore },
                            score = enteredScore,
                            date = date.toString(),
                            pace = saveRunPace,
                            level = saveRunEndLevel,
                            average = saveRunAverage,
                            savedAt = System.currentTimeMillis(),
                            averageIsRed = saveRunProgressColor == ArcadeColors.RedAccent,
                            neededAverage = saveRunNeededAverage,
                            goal = goal,
                            levelMetrics = metricsByLevel.toMap(),
                            levelIsEndLevel = true,
                            scoreManuallyEdited = false
                        )
                        savedRuns = savedRuns + run
                        saveRunMenuVisible = false
                        metricsByLevel.clear()
                        currentLevel = 4
                        preferences.edit { putString("saved_runs", saveRunsJson(savedRuns)) }
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
                    title = "SAVE RUN",
                    initialDate = LocalDate.now(ZoneId.systemDefault())
                )
            }
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

private fun paceHistoryFor(metricsByLevel: Map<Int, LevelMetrics>): List<PacePoint> {
    val startingMetrics = metricsByLevel[4] ?: LevelMetrics()
    val start = startingMetrics.score.toMetricLong() + startingMetrics.bonus.toMetricLong()
    return (5..21).mapNotNull { level ->
        val metrics = metricsByLevel[level] ?: return@mapNotNull null
        if (metrics.score.isBlank()) return@mapNotNull null
        val net = metrics.score.toMetricLong() + metrics.bonus.toMetricLong() - metrics.death.toMetricLong()
        val pace = ((net - start).toDouble() / (level - 4)) * (21 - level) +
            metrics.score.toMetricLong() + metrics.bonus.toMetricLong() + 700
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
            }.orEmpty(),
            levelIsEndLevel = item.optBoolean("levelIsEndLevel", false),
            scoreManuallyEdited = item.optBoolean("scoreManuallyEdited", false)
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
            put("levelIsEndLevel", run.levelIsEndLevel)
            put("scoreManuallyEdited", run.scoreManuallyEdited)
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
