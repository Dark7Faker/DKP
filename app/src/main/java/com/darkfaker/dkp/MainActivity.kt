package com.darkfaker.dkp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.darkfaker.dkp.ui.ArcadeTrackerScreen
import com.darkfaker.dkp.ui.DKPaceSplashScreen
import com.darkfaker.dkp.ui.theme.DKPaceTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        setContent {
            DKPaceTheme {
                var splashVisible by remember { mutableStateOf(true) }
                var trackerVisible by remember { mutableStateOf(false) }
                Box(Modifier.fillMaxSize()) {
                    if (trackerVisible) {
                        ArcadeTrackerScreen()
                        LaunchedEffect(Unit) {
                            withFrameNanos { }
                            withFrameNanos { }
                            splashVisible = false
                        }
                    }
                    AnimatedVisibility(
                        visible = splashVisible,
                        modifier = Modifier.fillMaxSize(),
                        exit = fadeOut(animationSpec = tween(durationMillis = 450))
                    ) {
                        DKPaceSplashScreen(onLogoAnimationFinished = { trackerVisible = true })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArcadeTrackerPreview() {
    DKPaceTheme {
        ArcadeTrackerScreen()
    }
}
