package com.example.dkpace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dkpace.ui.theme.ArcadeColors

@Composable
fun CurrentLevelCard(
    modifier: Modifier = Modifier,
    currentLevel: Int = 4,
    onPreviousLevel: () -> Unit = {},
    onNextLevel: () -> Unit = {},
    compact: Boolean = false
) {
    val levelText = String.format("L = %02d", currentLevel)

    var totalDragX by remember { mutableFloatStateOf(0f) }
    var hasSwiped by remember { mutableStateOf(false) }

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
            .padding(
                horizontal = if (compact) 4.dp else 12.dp,
                vertical = if (compact) 6.dp else 12.dp
            )
            .pointerInput(currentLevel) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        hasSwiped = false
                    },
                    onDragEnd = {
                        totalDragX = 0f
                        hasSwiped = false
                    },
                    onDragCancel = {
                        totalDragX = 0f
                        hasSwiped = false
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount
                        val swipeThreshold = 30.dp.toPx()
                        if (!hasSwiped) {
                            if (totalDragX > swipeThreshold) {
                                // Finger moved left-to-right (Swipe Right) -> decrease level
                                if (currentLevel > 4) {
                                    onPreviousLevel()
                                }
                                hasSwiped = true
                            } else if (totalDragX < -swipeThreshold) {
                                // Finger moved right-to-left (Swipe Left) -> increase level
                                if (currentLevel < 21) {
                                    onNextLevel()
                                }
                                hasSwiped = true
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Center Content: CURRENT LEVEL & selected level
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CURRENT LEVEL",
                color = ArcadeColors.TextMuted,
                fontSize = if (compact) 7.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(if (compact) 1.dp else 2.dp))
            Text(
                text = levelText,
                color = ArcadeColors.CyanPrimary,
                fontSize = if (compact) 18.sp else 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }
}
