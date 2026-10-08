package com.darkfaker.dkp.ui.components

import android.app.DatePickerDialog
import android.view.ContextThemeWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import kotlin.math.roundToLong
import com.darkfaker.dkp.R
import com.darkfaker.dkp.ui.theme.ArcadeColors
import java.util.Locale
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun SaveRunMenu(
    runName: String,
    onRunNameChange: (String) -> Unit,
    startScore: String,
    score: String,
    level: Int?,
    pace: String,
    average: String,
    averageColor: Color,
    onDismiss: () -> Unit,
    onSave: (score: String, date: LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "SAVE RUN",
    initialDate: LocalDate = LocalDate.now(ZoneId.systemDefault())
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var selectedDate by remember(initialDate) {
        mutableStateOf(initialDate)
    }
    var editedScore by remember(score) { mutableStateOf(score) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ArcadeColors.CardBackground, RoundedCornerShape(18.dp))
            .border(1.5.dp, ArcadeColors.CyanBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                    contentDescription = null,
                    tint = ArcadeColors.CyanPrimary,
                    modifier = Modifier.padding(top = 2.dp).size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title.uppercase(Locale.ROOT),
                    color = ArcadeColors.CyanPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SaveRunNameInput(
                    modifier = Modifier.weight(1.15f),
                    value = runName,
                    onValueChange = onRunNameChange
                )
                SaveRunScoreInput(
                    modifier = Modifier.weight(0.85f),
                    initialScore = score,
                    onScoreChange = { editedScore = it },
                    animateOnEnter = true
                )
            }

            Spacer(Modifier.height(14.dp))

            SaveRunDateItem(
                date = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                onClick = {
                    DatePickerDialog(
                        ContextThemeWrapper(context, R.style.DatePickerDialogTheme),
                        { _, year, month, dayOfMonth ->
                            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                        },
                        selectedDate.year,
                        selectedDate.monthValue - 1,
                        selectedDate.dayOfMonth
                    ).show()
                }
            )

            Spacer(Modifier.height(14.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val trackerLevelBlockWidth = (maxWidth + 18.dp) / 3.5f - 16.dp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SaveRunSummaryItem(
                        modifier = Modifier.weight(1f).height(80.dp),
                        label = "START",
                        value = startScore,
                        icon = Icons.Default.PlayArrow,
                        tint = ArcadeColors.GoldAccent,
                        borderColor = ArcadeColors.GoldAccent,
                        valueColor = ArcadeColors.GoldAccent,
                        valueFontSize = 13.sp,
                        animateOnEnter = true
                    )
                    SaveRunSummaryItem(
                        modifier = Modifier.weight(1.1f).height(80.dp),
                        label = "PACE",
                        value = pace,
                        icon = Icons.Default.EmojiEvents,
                        tint = Color.White,
                        borderColor = Color.White,
                        valueColor = Color.White,
                        valueFontSize = 13.sp,
                        animateOnEnter = true
                    )
                    SaveRunSummaryItem(
                        modifier = Modifier.weight(1f).height(80.dp),
                        label = "AVERAGE",
                        value = average,
                        icon = Icons.Default.Balance,
                        tint = averageColor,
                        borderColor = averageColor,
                        valueColor = averageColor,
                        valueFontSize = 13.sp,
                        animateOnEnter = true
                    )
                    SaveRunSummaryItem(
                        modifier = Modifier.width(trackerLevelBlockWidth).height(80.dp),
                        label = "LEVEL",
                        value = level?.let { String.format(Locale.ROOT, "L = %02d", it) } ?: "---",
                        icon = Icons.Default.Adjust,
                        tint = ArcadeColors.CyanPrimary,
                        borderColor = ArcadeColors.CyanBorder,
                        emphasizeValue = level != null,
                        valueColor = if (level == null) ArcadeColors.CyanPrimary else null,
                        progress = level?.let {
                            (14f + (it - 4).coerceIn(0, 17) * 6f) / 117f
                        },
                        progressAnimationFromZero = level != null,
                        animateOnEnter = level != null,
                        animatedValueFormat = { animatedLevel ->
                            String.format(Locale.ROOT, "L = %02d", animatedLevel)
                        }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SaveRunActionButton(
                    modifier = Modifier.weight(1f),
                    label = "CANCEL",
                    icon = Icons.Default.Close,
                    color = ArcadeColors.RedAccent,
                    outlined = true,
                    onClick = {
                        focusManager.clearFocus()
                        onDismiss()
                    }
                )
                SaveRunActionButton(
                    modifier = Modifier.weight(1f),
                    label = "SAVE",
                    icon = Icons.Default.Save,
                    color = ArcadeColors.GreenAccent,
                    onClick = {
                        focusManager.clearFocus()
                        onSave(editedScore, selectedDate)
                    }
                )
            }
        }
    }
}

@Composable
fun SaveRunDisplayCard(
    title: String = "SAVE RUN",
    runName: String,
    onRunNameChange: (String) -> Unit,
    date: String,
    onDateClick: () -> Unit,
    startScore: String,
    score: String,
    onScoreChange: (String) -> Unit,
    level: Int?,
    pace: String,
    average: String,
    averageColor: Color,
    modifier: Modifier = Modifier
) {
    val initialScore = remember { score }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ArcadeColors.CardBackground, RoundedCornerShape(18.dp))
            .border(1.5.dp, ArcadeColors.CyanBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                    contentDescription = null,
                    tint = ArcadeColors.CyanPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    color = ArcadeColors.CyanPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SaveRunNameInput(
                    modifier = Modifier.weight(1.15f),
                    value = runName,
                    onValueChange = onRunNameChange
                )
                SaveRunScoreInput(
                    modifier = Modifier.weight(0.85f),
                    initialScore = initialScore,
                    onScoreChange = onScoreChange,
                    animateOnEnter = true
                )
            }
            Spacer(Modifier.height(14.dp))
            SaveRunDateItem(date = date, onClick = onDateClick)
            Spacer(Modifier.height(14.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val levelBlockWidth = (maxWidth + 18.dp) / 3.5f - 16.dp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SaveRunSummaryItem(
                        modifier = Modifier.weight(1f).height(80.dp),
                        label = "START",
                        value = startScore,
                        icon = Icons.Default.PlayArrow,
                        tint = ArcadeColors.GoldAccent,
                        borderColor = ArcadeColors.GoldAccent,
                        valueColor = ArcadeColors.GoldAccent,
                        valueFontSize = 13.sp,
                        animateOnEnter = true
                    )
                    SaveRunSummaryItem(
                        modifier = Modifier.weight(1.1f).height(80.dp),
                        label = "PACE",
                        value = pace,
                        icon = Icons.Default.EmojiEvents,
                        tint = Color.White,
                        borderColor = Color.White,
                        valueColor = Color.White,
                        valueFontSize = 13.sp,
                        animateOnEnter = true
                    )
                    SaveRunSummaryItem(
                        modifier = Modifier.weight(1f).height(80.dp),
                        label = "AVERAGE",
                        value = average,
                        icon = Icons.Default.Balance,
                        tint = averageColor,
                        borderColor = averageColor,
                        valueColor = averageColor,
                        valueFontSize = 13.sp,
                        animateOnEnter = true
                    )
                    SaveRunSummaryItem(
                        modifier = Modifier.width(levelBlockWidth).height(80.dp),
                        label = "LEVEL",
                        value = level?.let { String.format(Locale.ROOT, "L = %02d", it) } ?: "---",
                        icon = Icons.Default.Adjust,
                        tint = ArcadeColors.CyanPrimary,
                        borderColor = ArcadeColors.CyanBorder,
                        emphasizeValue = level != null,
                        valueColor = if (level == null) ArcadeColors.CyanPrimary else null,
                        progress = level?.let { (14f + (it - 4).coerceIn(0, 17) * 6f) / 117f },
                        progressAnimationFromZero = level != null,
                        animateOnEnter = level != null,
                        animatedValueFormat = { animatedLevel ->
                            String.format(Locale.ROOT, "L = %02d", animatedLevel)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveRunNameInput(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = ArcadeColors.CyanPrimary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "RUN NAME",
                color = ArcadeColors.CyanPrimary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    ArcadeColors.CyanPrimary.copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "New Run",
                    color = ArcadeColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            BasicTextField(
                value = value,
                onValueChange = { input ->
                    val capitalized = input.replaceFirstChar { it.uppercase() }
                    val length = capitalized.codePointCount(0, capitalized.length)
                    val capped = if (length > 20) {
                        capitalized.substring(0, capitalized.offsetByCodePoints(0, 20))
                    } else {
                        capitalized
                    }
                    onValueChange(capped)
                },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = ArcadeColors.CyanPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(ArcadeColors.CyanPrimary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SaveRunScoreInput(
    modifier: Modifier,
    initialScore: String,
    onScoreChange: (String) -> Unit,
    animateOnEnter: Boolean = false
) {
    var score by remember(initialScore) { mutableStateOf(initialScore) }
    var previousScore by remember(initialScore) { mutableStateOf(initialScore) }
    var wasFocused by remember { mutableStateOf(false) }
    var wasEdited by remember { mutableStateOf(false) }
    val animatedScore = remember { Animatable(0f) }
    var entranceAnimationFinished by remember {
        mutableStateOf(!animateOnEnter || initialScore.isBlank() || initialScore == "---")
    }
    LaunchedEffect(animateOnEnter) {
        if (animateOnEnter && initialScore.isNotBlank() && initialScore != "---") {
            animatedScore.animateTo(
                targetValue = metricTextToLong(initialScore).toFloat(),
                animationSpec = tween(durationMillis = 281)
            )
            entranceAnimationFinished = true
        }
    }
    val displayedScore = if (
        animateOnEnter && !entranceAnimationFinished && !wasFocused && !wasEdited
    ) {
        formatMetricNumber(roundMetricToHundred(animatedScore.value.roundToLong()))
    } else {
        score
    }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFB000FF),
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "SCORE",
                color = Color(0xFFB000FF),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    Color(0xFFB000FF).copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (score.isEmpty() && !wasFocused) {
                Text(
                    text = "New Score",
                    color = ArcadeColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            BasicTextField(
                value = displayedScore,
                onValueChange = { input ->
                    val digits = input.filter(Char::isDigit)
                    if (digits.length <= 7) {
                        score = digits
                        onScoreChange(digits)
                    }
                },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFFB000FF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(Color(0xFFB000FF)),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && !wasFocused) {
                            previousScore = score
                            wasFocused = true
                            wasEdited = true
                            score = ""
                        } else if (!focusState.isFocused && wasFocused) {
                            wasFocused = false
                            score = if (score.isBlank()) {
                                previousScore
                            } else {
                                formatSaveRunScore(score)
                            }
                            onScoreChange(score)
                        }
                    }
            )
        }
    }
}

private fun formatSaveRunScore(value: String): String {
    val digits = value.filter(Char::isDigit)
    val expandedDigits = if (digits.length == 4) "${digits}00" else digits
    val rounded = expandedDigits.toLongOrNull()?.let { ((it + 50) / 100) * 100 } ?: return ""
    return rounded.toString().reversed().chunked(3).joinToString(".").reversed()
}

@Composable
private fun SaveRunDateItem(date: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(9.dp))
            .border(1.dp, ArcadeColors.BorderSubtle, RoundedCornerShape(9.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = ArcadeColors.TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "DATE",
            color = ArcadeColors.TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = date,
            color = ArcadeColors.TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SaveRunActionButton(
    modifier: Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    outlined: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .height(48.dp)
            .background(color, RoundedCornerShape(12.dp))
            .then(
                if (outlined) Modifier.border(1.5.dp, color, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp),
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

@Composable
private fun SaveRunSummaryItem(
    modifier: Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    borderColor: Color = ArcadeColors.BorderSubtle,
    labelColor: Color = tint,
    emphasizeValue: Boolean = false,
    valueColor: Color? = null,
    valueFontSize: TextUnit? = null,
    progress: Float? = null,
    progressAnimationFromZero: Boolean = false,
    animateOnEnter: Boolean = false,
    animatedValueFormat: (Long) -> String = {
        formatMetricNumber(roundMetricToHundred(it))
    }
) {
    val animatedValue = remember { Animatable(0f) }
    var entranceAnimationFinished by remember {
        mutableStateOf(!animateOnEnter || value.isBlank() || value == "---")
    }
    LaunchedEffect(animateOnEnter) {
        if (animateOnEnter && value.isNotBlank() && value != "---") {
            animatedValue.animateTo(
                targetValue = metricTextToLong(value).toFloat(),
                animationSpec = tween(durationMillis = 281)
            )
            entranceAnimationFinished = true
        }
    }
    val displayedValue = if (animateOnEnter && !entranceAnimationFinished) {
        animatedValueFormat(animatedValue.value.roundToLong())
    } else {
        value
    }
    val animatedProgress = remember {
        Animatable(if (progressAnimationFromZero) 0f else progress ?: 0f)
    }
    LaunchedEffect(progress, progressAnimationFromZero) {
        val targetProgress = progress ?: 0f
        if (progressAnimationFromZero) {
            animatedProgress.animateTo(
                targetValue = targetProgress,
                animationSpec = tween(durationMillis = 500)
            )
        } else {
            animatedProgress.snapTo(targetProgress)
        }
    }
    Column(
        modifier = modifier
            .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(9.dp))
            .border(1.dp, borderColor, RoundedCornerShape(9.dp))
            .padding(horizontal = 5.dp, vertical = if (progress != null) 4.dp else 9.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(3.dp))
            Text(
                text = label,
                color = labelColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = displayedValue,
            color = valueColor ?: if (emphasizeValue) tint else ArcadeColors.TextWhite,
            fontSize = valueFontSize ?: if (emphasizeValue) 18.sp else 11.sp,
            fontWeight = if (emphasizeValue) FontWeight.ExtraBold else FontWeight.Bold,
            letterSpacing = if (emphasizeValue) 1.sp else 0.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (progress != null) {
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ArcadeColors.CyanBorder.copy(alpha = 0.55f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress.value.coerceIn(0f, 1f))
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ArcadeColors.CyanPrimary)
                )
            }
        }
    }
}
