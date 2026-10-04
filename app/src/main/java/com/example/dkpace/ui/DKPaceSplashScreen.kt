package com.example.dkpace.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.dkpace.R
import kotlinx.coroutines.delay

@Composable
fun DKPaceSplashScreen() {
    var logoVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80)
        logoVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020916),
                        Color(0xFF06152A),
                        Color(0xFF020916)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(310.dp)
                .blur(28.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x2800E5FF), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        AnimatedVisibility(
            visible = logoVisible,
            enter = fadeIn(animationSpec = tween(700)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                )
        ) {
            Image(
                painter = painterResource(R.drawable.dkpace_splash_logo),
                contentDescription = "DK Pace logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(300.dp)
            )
        }
    }
}
