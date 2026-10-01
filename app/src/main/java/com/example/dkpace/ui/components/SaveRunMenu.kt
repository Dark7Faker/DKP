package com.example.dkpace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dkpace.ui.theme.ArcadeColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SaveRunMenu(
    score: String,
    level: Int,
    pace: String,
    average: String,
    averageColor: Color,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = ArcadeColors.CyanPrimary,
                    modifier = Modifier.padding(top = 2.dp).size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "SAVE RUN",
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
                SaveRunInputPreview(
                    modifier = Modifier.weight(1.15f),
                    label = "RUN NAME",
                    icon = Icons.Default.Edit,
                    value = "New Run",
                    borderColor = ArcadeColors.CyanPrimary.copy(alpha = 0.6f)
                )
                SaveRunInputPreview(
                    modifier = Modifier.weight(0.85f),
                    label = "SCORE",
                    icon = Icons.Default.Star,
                    value = score.ifBlank { "New Score" },
                    tint = Color(0xFFB000FF),
                    borderColor = Color(0xFFB000FF).copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(14.dp))

            SaveRunDateItem(
                date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            )

            Spacer(Modifier.height(14.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val trackerLevelBlockWidth = (maxWidth + 18.dp) / 3.5f
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SaveRunSummaryItem(
                        modifier = Modifier.weight(1.1f).height(80.dp),
                        label = "PACE",
                        value = pace,
                        icon = Icons.Default.EmojiEvents,
                        tint = Color.White,
                        borderColor = Color.White
                    )
                    SaveRunSummaryItem(
                        modifier = Modifier.weight(0.9f).height(80.dp),
                        label = "AVERAGE",
                        value = average,
                        icon = Icons.Default.Balance,
                        tint = averageColor,
                        borderColor = averageColor
                    )
                    SaveRunSummaryItem(
                        modifier = Modifier.width(trackerLevelBlockWidth).height(80.dp),
                        label = "LEVEL",
                        value = String.format("L = %02d", level),
                        icon = Icons.Default.Adjust,
                        tint = ArcadeColors.CyanPrimary,
                        borderColor = ArcadeColors.CyanBorder,
                        emphasizeValue = true
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
                    onClick = onDismiss
                )
                SaveRunActionButton(
                    modifier = Modifier.weight(1f),
                    label = "SAVE",
                    icon = Icons.Default.Save,
                    color = ArcadeColors.GreenAccent,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SaveRunDateItem(date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(9.dp))
            .border(1.dp, ArcadeColors.BorderSubtle, RoundedCornerShape(9.dp))
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
    Row(
        modifier = modifier
            .height(48.dp)
            .background(color, RoundedCornerShape(12.dp))
            .then(
                if (outlined) Modifier.border(1.5.dp, color, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable(onClick = onClick)
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
private fun SaveRunInputPreview(
    modifier: Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    tint: Color = ArcadeColors.CyanPrimary,
    borderColor: Color = ArcadeColors.BorderSubtle
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                color = tint,
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
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                color = if (value == "New Run" || value == "New Score" || value == "—") {
                    ArcadeColors.TextMuted
                } else {
                    ArcadeColors.TextWhite
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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
    emphasizeValue: Boolean = false
) {
    Column(
        modifier = modifier
            .background(ArcadeColors.InnerBoxBackground, RoundedCornerShape(9.dp))
            .border(1.dp, borderColor, RoundedCornerShape(9.dp))
            .padding(horizontal = 7.dp, vertical = 9.dp),
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
            text = value,
            color = if (emphasizeValue) tint else ArcadeColors.TextWhite,
            fontSize = if (emphasizeValue) 18.sp else 11.sp,
            fontWeight = if (emphasizeValue) FontWeight.ExtraBold else FontWeight.Bold,
            letterSpacing = if (emphasizeValue) 1.sp else 0.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
