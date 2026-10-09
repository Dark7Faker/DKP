package com.darkfaker.dkp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.EmojiEvents

import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkfaker.dkp.ui.theme.ArcadeColors

@Composable
fun CurrentRunCard(
    modifier: Modifier = Modifier,
    pace: String,
    currentAverage: String,
    nextLevelCurrentAverage: String,
    neededAverage: String,
    nextLevelNeededAverage: String,
    pointsInThisLevel: String,
    progressColor: Color,
    title: String = "LEVEL STATISTICS",
    showPointsThisLevel: Boolean = true,
    showNextLevelMetrics: Boolean = true,
    animateOnEnter: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ArcadeColors.CardBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.5.dp,
                color = ArcadeColors.CyanBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {

            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.FactCheck,
                    contentDescription = null,
                    tint = ArcadeColors.CyanPrimary,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        color = ArcadeColors.CyanPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
            // Header


            Spacer(modifier = Modifier.height(12.dp))

            CenteredMetricRow(
                icon = {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = ArcadeColors.CyanPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = "PACE",
                value = pace,
                valueColor = ArcadeColors.TextWhite,
                animateOnEnter = animateOnEnter
            )

            GridDivider()

            if (showPointsThisLevel) {
                CenteredMetricRow(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = ArcadeColors.CyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = "POINTS THIS LEVEL",
                    value = pointsInThisLevel,
                    valueColor = ArcadeColors.TextWhite,
                    animateOnEnter = animateOnEnter
                )

                GridDivider()
            }

            // Row 2: CURRENT AVERAGE | NEEDED AVERAGE
            GridRow(
                leftIcon = {
                    Icon(
                        imageVector = Icons.Default.Balance,
                        contentDescription = null,
                        tint = progressColor,
                        modifier = Modifier.padding(top = 30.dp).size(24.dp)
                    )
                },
                leftLabel = "CURRENT AVERAGE",
                leftValue = currentAverage,
                leftValueColor = progressColor,
                
                rightIcon = {
                    Icon(
                        imageVector = Icons.Default.DoNotDisturbOn,
                        contentDescription = null,
                        tint = ArcadeColors.GoldAccent,
                        modifier = Modifier.padding(top = 30.dp).size(24.dp)
                    )
                },
                rightLabel = "NEEDED AVERAGE",
                rightValue = neededAverage,
                rightValueColor = ArcadeColors.GoldAccent,
                animateOnEnter = animateOnEnter
            )

            if (showNextLevelMetrics) GridDivider()

            // Row 3: NEXT LEVEL - CURRENT | NEXT LEVEL - NEEDED
            if (showNextLevelMetrics) GridRow(
                leftIcon = {
                    Icon(
                        imageVector = Icons.Default.DoubleArrow,
                        contentDescription = null,
                        tint = progressColor,
                        modifier = Modifier.padding(top = 30.dp).size(24.dp)
                    )
                },
                leftLabel = "NEXT LEVEL - CURRENT",
                leftValue = nextLevelCurrentAverage,
                leftValueColor = progressColor,
                
                rightIcon = {
                    Icon(
                        imageVector = Icons.Default.DoNotDisturbOn,
                        contentDescription = null,
                        tint = ArcadeColors.GoldAccent,
                        modifier = Modifier.padding(top = 30.dp).size(24.dp)
                    )
                },
                rightLabel = "NEXT LEVEL - NEEDED",
                rightValue = nextLevelNeededAverage,
                rightValueColor = ArcadeColors.GoldAccent,
                animateOnEnter = animateOnEnter
            )
        }
    }
}

@Composable
private fun CenteredMetricRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    valueColor: Color,
    animateOnEnter: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(24.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = ArcadeColors.TextSecondary,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
                AnimatedMetricValue(
                    value = value,
                    color = valueColor,
                    fontSize = 22.sp,
                    animateOnEnter = animateOnEnter
            )
        }
        Box(
            modifier = Modifier.width(24.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

@Composable
private fun GridDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        thickness = 1.dp,
        color = ArcadeColors.Divider
    )
}

@Composable
private fun GridRow(
    leftIcon: @Composable () -> Unit,
    leftLabel: String,
    leftValue: String,
    leftValueColor: Color,
    rightIcon: @Composable () -> Unit,
    rightLabel: String,
    rightValue: String,
    rightValueColor: Color,
    animateOnEnter: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Column
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leftIcon()
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                ResponsiveMetricLabel(leftLabel)
                Spacer(modifier = Modifier.height(2.dp))
                AnimatedMetricValue(
                    value = leftValue,
                    color = leftValueColor,
                    fontSize = 22.sp,
                    animateOnEnter = animateOnEnter
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right Column
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            rightIcon()
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                ResponsiveMetricLabel(rightLabel)
                Spacer(modifier = Modifier.height(2.dp))
                AnimatedMetricValue(
                    value = rightValue,
                    color = rightValueColor,
                    fontSize = 22.sp,
                    animateOnEnter = animateOnEnter
                )
            }
        }
    }
}

@Composable
private fun ResponsiveMetricLabel(label: String) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = ArcadeColors.TextSecondary,
            fontSize = if (maxWidth < 140.dp) 8.sp else 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = if (maxWidth < 140.dp) 0.25.sp else 0.5.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun AnimatedMetricValue(
    value: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    animateOnEnter: Boolean
) {
    val entranceValue = remember { Animatable(0f) }
    var entranceAnimationFinished by remember {
        mutableStateOf(!animateOnEnter || value == "---" || value.isBlank())
    }
    LaunchedEffect(animateOnEnter) {
        if (animateOnEnter && value != "---" && value.isNotBlank()) {
            entranceValue.animateTo(
                targetValue = metricTextToLong(value).toFloat(),
                animationSpec = tween(durationMillis = 281)
            )
            entranceAnimationFinished = true
        }
    }
    val animatedValue = animatedMetricNumber(metricTextToLong(value))
    val visibleValue = if (animateOnEnter && !entranceAnimationFinished) {
        entranceValue.value.toLong()
    } else {
        animatedValue
    }
    val displayValue = if (value == "---" && visibleValue == 0L) {
        "---"
    } else {
        formatMetricNumber(roundMetricToHundred(visibleValue))
    }
    Text(
        text = displayValue,
        color = color,
        fontSize = fontSize,
        fontWeight = FontWeight.Black
    )
}

