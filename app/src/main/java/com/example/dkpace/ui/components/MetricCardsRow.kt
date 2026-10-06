package com.example.dkpace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dkpace.ui.theme.ArcadeColors
import com.example.dkpace.R

data class LevelMetrics(
    val score: String = "",
    val bonus: String = "",
    val death: String = ""
)

private enum class MetricShortcut {
    SCORE,
    BONUS_OR_DEATH,
    NONE
}

@Composable
fun MetricCardsRow(
    modifier: Modifier = Modifier,
    score: String,
    bonus: String,
    death: String,
    currentLevel: Int,
    onScoreChange: (String) -> Unit,
    onBonusChange: (String) -> Unit,
    onDeathChange: (String) -> Unit,
    onInputFocusChanged: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. SCORE Card
        SingleMetricCard(
            modifier = Modifier.weight(1f).height(80.dp),
            label = "SCORE",
            value = score,
            maxDigits = 7,
            shortcut = MetricShortcut.SCORE,
            onValueChange = onScoreChange,
            onFocusChanged = onInputFocusChanged,
            accentColor = Color(0xFFB000FF),
            borderColor = Color(0xFFB000FF),
            inputTextColor = Color(0xFFB000FF),
            zeroAsEmpty = true,
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFB000FF),
                    modifier = Modifier.size(15.dp)
                )
            }
        )

        // 2. BONUS Card
        SingleMetricCard(
            modifier = Modifier.weight(0.75f).height(80.dp),
            label = "BONUS",
            value = bonus,
            maxDigits = 5,
            shortcut = MetricShortcut.BONUS_OR_DEATH,
            onValueChange = onBonusChange,
            onFocusChanged = onInputFocusChanged,
            accentColor = Color(0xFFC0C0C0),
            borderColor = Color(0xFFC0C0C0),
            inputTextColor = Color(0xFFC0C0C0),
            zeroAsEmpty = true,
            icon = {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color(0xFFC0C0C0),
                    modifier = Modifier.size(15.dp)
                )
            }
        )

        // 3. DEATH BONUS Card
        SingleMetricCard(
            modifier = Modifier.weight(0.75f).height(80.dp),
            label = "DEATH",
            value = death,
            maxDigits = 5,
            shortcut = MetricShortcut.BONUS_OR_DEATH,
            onValueChange = onDeathChange,
            onFocusChanged = onInputFocusChanged,
            accentColor = ArcadeColors.PinkAccent,
            borderColor = ArcadeColors.PinkAccent,
            inputTextColor = ArcadeColors.PinkAccent,
            restorePreviousOnEmpty = false,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_skull),
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = ArcadeColors.PinkAccent
                )
            }
        )

        CurrentLevelCard(
            modifier = Modifier.weight(1f).height(80.dp),
            currentLevel = currentLevel,
            compact = true,
        )
    }
}

@Composable
fun RunLevelMetricsRow(
    modifier: Modifier = Modifier,
    score: String,
    bonus: String,
    death: String,
    currentLevel: Int
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ReadOnlyMetricCard(
            modifier = Modifier.weight(1f).height(80.dp),
            label = "SCORE",
            value = score,
            accentColor = Color(0xFFB000FF),
            borderColor = Color(0xFFB000FF),
            icon = {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFB000FF), modifier = Modifier.size(15.dp))
            }
        )
        ReadOnlyMetricCard(
            modifier = Modifier.weight(0.75f).height(80.dp),
            label = "BONUS",
            value = bonus,
            accentColor = Color(0xFFC0C0C0),
            borderColor = Color(0xFFC0C0C0),
            icon = {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFFC0C0C0), modifier = Modifier.size(15.dp))
            }
        )
        ReadOnlyMetricCard(
            modifier = Modifier.weight(0.75f).height(80.dp),
            label = "DEATH",
            value = death,
            accentColor = ArcadeColors.PinkAccent,
            borderColor = ArcadeColors.PinkAccent,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_skull),
                    contentDescription = null,
                    tint = ArcadeColors.PinkAccent,
                    modifier = Modifier.size(13.dp)
                )
            }
        )
        CurrentLevelCard(
            modifier = Modifier.weight(1f).height(80.dp),
            currentLevel = currentLevel,
            compact = true
        )
    }
}

@Composable
private fun ReadOnlyMetricCard(
    modifier: Modifier,
    label: String,
    value: String,
    accentColor: Color,
    borderColor: Color,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                icon()
                Spacer(Modifier.width(3.dp))
                Text(label, color = accentColor, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(8.dp))
                    .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    color = accentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GoalMetricCard(
    value: String,
    onValueChange: (String) -> Unit,
    onInputFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleMetricCard(
        modifier = modifier.fillMaxWidth().height(88.dp),
        label = "GOAL",
        value = value,
        maxDigits = 7,
        shortcut = MetricShortcut.NONE,
        onValueChange = onValueChange,
        onFocusChanged = onInputFocusChanged,
        accentColor = ArcadeColors.GoldAccent,
        borderColor = ArcadeColors.GoldAccent,
        inputTextColor = ArcadeColors.GoldAccent,
        compact = true,
        restorePreviousOnEmpty = false,
        icon = {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = ArcadeColors.GoldAccent,
                modifier = Modifier.size(15.dp)
            )
        }
    )
}

@Composable
fun GoalDisplayCard(
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(88.dp)
            .background(ArcadeColors.CardBackground, RoundedCornerShape(12.dp))
            .border(1.dp, ArcadeColors.GoldAccent, RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = ArcadeColors.GoldAccent,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "GOAL",
                    color = ArcadeColors.GoldAccent,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(8.dp))
                    .border(1.dp, ArcadeColors.GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    color = ArcadeColors.GoldAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SingleMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    maxDigits: Int,
    shortcut: MetricShortcut,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    accentColor: Color,
    borderColor: Color,
    inputTextColor: Color = ArcadeColors.TextWhite,
    hideBorder: Boolean = false,
    restorePreviousOnEmpty: Boolean = true,
    compact: Boolean = false,
    zeroAsEmpty: Boolean = false,
    icon: @Composable () -> Unit
) {
    var wasFocused by remember { mutableStateOf(false) }
    var previousValue by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Box(
        modifier = modifier
            .background(
                color = ArcadeColors.CardBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (hideBorder) Color.Transparent else borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 6.dp, vertical = if (compact) 6.dp else 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Label Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                icon()
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = label,
                    color = accentColor,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(if (compact) 4.dp else 8.dp))

            // Value Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = ArcadeColors.InnerBoxBackground,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = borderColor.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { input ->
                        val digits = input.filter(Char::isDigit)
                        if (digits.length <= maxDigits) onValueChange(digits)
                    },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = inputTextColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(inputTextColor),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onValueChange(formatMetric(value, shortcut, zeroAsEmpty))
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            onFocusChanged(focusState.isFocused)
                            if (focusState.isFocused && !wasFocused) {
                                wasFocused = true
                                previousValue = value
                                onValueChange("")
                            } else if (!focusState.isFocused && wasFocused) {
                                wasFocused = false
                                if (restorePreviousOnEmpty && value.isBlank() && previousValue.isNotBlank()) {
                                    onValueChange(previousValue)
                                } else {
                                    onValueChange(formatMetric(value, shortcut, zeroAsEmpty))
                                }
                            }
                        },
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.Center) {
                            innerTextField()
                        }
                    }
                )
            }

        }
    }
}

private fun formatMetric(
    value: String,
    shortcut: MetricShortcut,
    zeroAsEmpty: Boolean = false
): String {
    val digits = value.filter(Char::isDigit)
    if (zeroAsEmpty && digits.toLongOrNull() == 0L) return ""
    val expandedDigits = when (shortcut) {
        MetricShortcut.SCORE -> if (digits.length == 4) "${digits}00" else digits
        MetricShortcut.BONUS_OR_DEATH -> if (digits.length in 1..2) "${digits}00" else digits
        MetricShortcut.NONE -> digits
    }
    val rounded = expandedDigits.toLongOrNull()?.roundToHundred() ?: return ""
    return rounded.toString().reversed().chunked(3).joinToString(".").reversed()
}

private fun Long.roundToHundred(): Long = ((this + 50) / 100) * 100
