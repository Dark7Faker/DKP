package com.darkfaker.dkp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.darkfaker.dkp.ui.theme.ArcadeColors

/** Compact bottom navigation for the tracker and saved-run views. */
@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .background(ArcadeColors.CardBackground, RoundedCornerShape(16.dp))
            .border(1.dp, ArcadeColors.CyanBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 5.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavIcon(
            icon = Icons.Default.Visibility,
            description = "Analyze Runs",
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavIcon(
            icon = Icons.Default.BarChart,
            description = "DK Pace Tracker",
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavIcon(
            icon = Icons.Default.Settings,
            description = "Settings",
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
    }
}

@Composable
private fun NavIcon(
    icon: ImageVector,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(
                color = if (selected) Color(0xFF0A1B30) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (selected) ArcadeColors.CyanPrimary else Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}
