package com.example.dkpace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dkpace.ui.theme.ArcadeColors

enum class MainMenu { TRACKER, ANALYZE }

@Composable
fun MainNavigationBar(
    selectedMenu: MainMenu,
    onMenuSelected: (MainMenu) -> Unit,
    modifier: Modifier = Modifier
) {
    val barShape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(ArcadeColors.CardBackground, barShape)
                .border(1.5.dp, ArcadeColors.CyanBorder, barShape)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMenuSelected(MainMenu.ANALYZE) }) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Analyze runs",
                    tint = if (selectedMenu == MainMenu.ANALYZE) ArcadeColors.CyanPrimary else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = { onMenuSelected(MainMenu.TRACKER) }) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "DK Pace Tracker menu",
                    tint = if (selectedMenu == MainMenu.TRACKER) ArcadeColors.CyanPrimary else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
