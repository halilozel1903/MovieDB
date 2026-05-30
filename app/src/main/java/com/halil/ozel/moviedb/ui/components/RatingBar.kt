package com.halil.ozel.moviedb.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.halil.ozel.moviedb.ui.theme.StarColor

@Composable
fun RatingBar(
    rating: Double,
    maxRating: Int = 10,
    starSize: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    val stars = 5
    val normalizedRating = (rating / maxRating) * stars
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(stars) { index ->
            val starValue = index + 1
            val icon = when {
                normalizedRating >= starValue -> Icons.Filled.Star
                normalizedRating >= starValue - 0.5 -> Icons.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = StarColor,
                modifier = Modifier.width(starSize)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.labelLarge,
            color = StarColor
        )
    }
}
