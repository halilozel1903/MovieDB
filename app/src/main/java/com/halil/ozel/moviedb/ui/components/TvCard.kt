package com.halil.ozel.moviedb.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun TvCard(
    tvSeries: TvSeries,
    onClick: (Int) -> Unit,
    isFavorite: Boolean = false,
    onFavoriteToggle: ((TvSeries) -> Unit)? = null,
    modifier: Modifier = Modifier.width(130.dp)   // default for horizontal rows
) {
    var favoriteAnimating by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (favoriteAnimating) 1.3f else 1f,
        animationSpec = spring(dampingRatio = 0.4f),
        finishedListener = { favoriteAnimating = false },
        label = "fav_scale"
    )

    Card(
        modifier = modifier.clickable { onClick(tvSeries.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + tvSeries.posterPath,
                contentDescription = tvSeries.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, CardGradientEnd),
                            startY = 80f
                        )
                    )
            )
            // Rating and Favorite button in a Row at TopStart
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating chip (left)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Background.copy(alpha = 0.75f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(text = "⭐", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format("%.1f", tvSeries.voteAverage),
                            style = MaterialTheme.typography.labelSmall,
                            color = StarColor
                        )
                    }
                }
                // Favorite button (right)
                if (onFavoriteToggle != null) {
                    IconButton(
                        onClick = {
                            favoriteAnimating = true
                            onFavoriteToggle(tvSeries)
                        },
                        modifier = Modifier.scale(scale)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Error else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = tvSeries.name,
                style = MaterialTheme.typography.bodySmall,
                color = OnBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                lineHeight = MaterialTheme.typography.bodySmall.fontSize * 1.3
            )
            Text(
                text = if (tvSeries.firstAirDate.length >= 4) tvSeries.firstAirDate.take(4) else "",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurface
            )
        }
    }
}
