package com.darkfaker.dkp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlin.math.roundToLong

@Composable
internal fun animatedMetricNumber(target: Long): Long {
    val animatedValue by animateFloatAsState(
        targetValue = target.toFloat(),
        animationSpec = tween(durationMillis = 500),
        label = "metric-count"
    )
    return animatedValue.roundToLong()
}

@Composable
internal fun animatedMetricNumberOnLevelChange(target: Long, level: Int): Long {
    val animatedValue = remember { Animatable(target.toFloat()) }
    var previousLevel by remember { mutableIntStateOf(level) }

    LaunchedEffect(target, level) {
        if (level != previousLevel) {
            previousLevel = level
            animatedValue.animateTo(
                targetValue = target.toFloat(),
                animationSpec = tween(durationMillis = 500)
            )
        } else {
            animatedValue.snapTo(target.toFloat())
        }
    }

    return animatedValue.value.roundToLong()
}

internal fun metricTextToLong(value: String): Long =
    value.filter { it.isDigit() || it == '-' }.toLongOrNull() ?: 0L

internal fun formatMetricNumber(value: Long): String {
    val digits = value.toString()
    val negative = digits.startsWith('-')
    val grouped = digits.removePrefix("-").reversed().chunked(3).joinToString(".").reversed()
    return if (negative) "-$grouped" else grouped
}

internal fun roundMetricToHundred(value: Long): Long = ((value + 50L) / 100L) * 100L
