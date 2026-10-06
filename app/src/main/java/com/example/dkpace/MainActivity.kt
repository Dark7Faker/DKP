package com.example.dkpace

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import com.example.dkpace.ui.ArcadeTrackerScreen
import com.example.dkpace.ui.DKPaceSplashScreen
import com.example.dkpace.ui.theme.DKPaceTheme
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
                LaunchedEffect(Unit) {
                    delay(1_100)
                    splashVisible = false
                }
                Box(Modifier.fillMaxSize()) {
                    ArcadeTrackerScreen()
                    AnimatedVisibility(
                        visible = splashVisible,
                        modifier = Modifier.fillMaxSize(),
                        exit = fadeOut(animationSpec = tween(durationMillis = 450))
                    ) {
                        DKPaceSplashScreen()
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
