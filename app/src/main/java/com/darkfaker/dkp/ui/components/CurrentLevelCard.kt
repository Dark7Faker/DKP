package com.darkfaker.dkp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkfaker.dkp.ui.theme.ArcadeColors
import kotlin.math.roundToInt

@Composable
fun CurrentLevelCard(
    modifier: Modifier = Modifier,
    currentLevel: Int = 4,
    compact: Boolean = false,
    hideBorder: Boolean = false,
    animateTextFromZeroOnEnter: Boolean = false
) {
    val animatedLevel = if (animateTextFromZeroOnEnter) {
        val entranceLevel = remember { Animatable(0f) }
        LaunchedEffect(currentLevel) {
            entranceLevel.animateTo(
                targetValue = currentLevel.toFloat(),
                animationSpec = tween(durationMillis = 281)
            )
        }
        entranceLevel.value.roundToInt()
    } else {
        animatedMetricNumber(currentLevel.toLong()).toInt()
    }
    val levelText = String.format("L = %02d", animatedLevel)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ArcadeColors.CardBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.5.dp,
                color = if (hideBorder) androidx.compose.ui.graphics.Color.Transparent else ArcadeColors.CyanBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                horizontal = if (compact) 6.dp else 12.dp,
                vertical = if (compact) 6.dp else 12.dp
            ),
        contentAlignment = if (compact) Alignment.TopCenter else Alignment.Center
    ) {
        // Center Content: CURRENT LEVEL & selected level
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Adjust,
                    contentDescription = null,
                    tint = ArcadeColors.CyanPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "LEVEL",
                    color = ArcadeColors.CyanPrimary,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(if (compact) 6.dp else 7.dp))
            Text(
                text = levelText,
                color = ArcadeColors.CyanPrimary,
                fontSize = if (compact) 18.sp else 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            if (compact) {
                val levelProgress = (14f + (currentLevel - 4).coerceIn(0, 17) * 6f) / 117f
                val animatedLevelProgress = remember { Animatable(0f) }
                LaunchedEffect(currentLevel) {
                    animatedLevelProgress.animateTo(
                        targetValue = levelProgress,
                        animationSpec = tween(durationMillis = 500)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ArcadeColors.CyanBorder.copy(alpha = 0.55f))
                ) {
                    Box(
                        modifier = Modifier
                        .fillMaxWidth(animatedLevelProgress.value)
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(ArcadeColors.CyanPrimary)
                    )
                }
            }
        }
    }
}
