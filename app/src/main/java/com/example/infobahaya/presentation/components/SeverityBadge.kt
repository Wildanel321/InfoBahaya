package com.example.infobahaya.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.domain.model.HazardSeverity

@Composable
fun SeverityBadge(
    severity: HazardSeverity,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(InfoBahayaThemeTokens.shapes.badge)
            .background(severity.containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Icon(
                imageVector = severity.icon,
                contentDescription = severity.displayName,
                tint = severity.contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = severity.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = severity.contentColor
        )
    }
}
