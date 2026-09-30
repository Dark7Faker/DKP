package com.example.dkpace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Battery1Bar
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dkpace.ui.theme.ArcadeColors

@Composable
fun HeaderSection(
    modifier: Modifier = Modifier,
    onBatterySaverClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = ArcadeColors.CyanPrimary,
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = "DK PACE TRACKER",
                color = ArcadeColors.TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = ArcadeColors.CardBackground,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = ArcadeColors.BorderSubtle,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(2.dp)
        ) {
            IconButton(
                onClick = onBatterySaverClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Battery2Bar,
                    contentDescription = "Sleep Mode Toggle",
                    tint = ArcadeColors.TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
