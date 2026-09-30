package com.example.dkpace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.dkpace.ui.ArcadeTrackerScreen
import com.example.dkpace.ui.theme.DKPaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DKPaceTheme {
                ArcadeTrackerScreen()
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
