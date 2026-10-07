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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dkpace.ui.theme.ArcadeColors

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
    showNextLevelMetrics: Boolean = true
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
                valueColor = ArcadeColors.TextWhite
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
                    valueColor = ArcadeColors.TextWhite
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
                rightValueColor = ArcadeColors.GoldAccent
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
                rightValueColor = ArcadeColors.GoldAccent
            )
        }
    }
}

@Composable
private fun CenteredMetricRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    valueColor: Color
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
                fontSize = 22.sp
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
    rightValueColor: Color
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
                Text(
                    text = leftLabel,
                    color = ArcadeColors.TextSecondary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                AnimatedMetricValue(
                    value = leftValue,
                    color = leftValueColor,
                    fontSize = 22.sp
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
                Text(
                    text = rightLabel,
                    color = ArcadeColors.TextSecondary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                AnimatedMetricValue(
                    value = rightValue,
                    color = rightValueColor,
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
private fun AnimatedMetricValue(
    value: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    val animatedValue = animatedMetricNumber(metricTextToLong(value))
    val displayValue = if (value == "---" && animatedValue == 0L) {
        "---"
    } else {
        formatMetricNumber(roundMetricToHundred(animatedValue))
    }
    Text(
        text = displayValue,
        color = color,
        fontSize = fontSize,
        fontWeight = FontWeight.Black
    )
}

