package com.halil.ozel.moviedb.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halil.ozel.moviedb.ui.theme.OnBackground
import com.halil.ozel.moviedb.ui.theme.OnSurface
import com.halil.ozel.moviedb.ui.theme.Primary

/**
 * Splits "🎬 Some Title" into emoji + text so each part can be
 * independently aligned in the Row — no more emoji baseline drift.
 */
@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Split leading emoji (or any leading non-letter chars) from the rest
    val emojiRegex = Regex("^([\\p{So}\\p{Sm}\\p{Sk}\\p{Sc}]+)\\s*(.*)\$")
    val match = emojiRegex.find(title)
    val emoji = match?.groupValues?.getOrNull(1)
    val text  = match?.groupValues?.getOrNull(2) ?: title

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (emoji != null) {
                Text(
                    text     = emoji,
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.alignByBaseline()
                )
            }
            Text(
                text       = text,
                style      = MaterialTheme.typography.titleLarge,
                color      = OnBackground,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.alignByBaseline()
            )
        }

        if (onSeeAllClick != null) {
            TextButton(
                onClick = onSeeAllClick,
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                Text(
                    text  = "See All",
                    color = Primary,
                    style = MaterialTheme.typography.labelLarge
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
