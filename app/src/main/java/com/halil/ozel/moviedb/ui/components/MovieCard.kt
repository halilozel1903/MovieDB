package com.halil.ozel.moviedb.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun MovieCard(
    movie: Movie,
    isFavorite: Boolean,
    onFavoriteToggle: (Movie) -> Unit,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier.width(150.dp)
) {
    var favoriteAnimating by remember { mutableStateOf(false) }
    val favScale by animateFloatAsState(
        targetValue = if (favoriteAnimating) 1.3f else 1f,
        animationSpec = spring(dampingRatio = 0.4f),
        finishedListener = { favoriteAnimating = false },
        label = "fav_scale"
    )

    Card(
        modifier = modifier.clickable { onClick(movie.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.aspectRatio(2f / 3f)) {
            // Full-bleed poster
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + movie.posterPath,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Cinematic gradient: transparent → very dark at bottom 45%
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.Transparent,
                                0.42f to Color.Transparent,
                                0.68f to Color(0xBB000000),
                                1.00f to Color(0xFF000000)
                            )
                        )
                    )
            )

            // Favorite button — small dark circle top-right
            IconButton(
                onClick = {
                    favoriteAnimating = true
                    onFavoriteToggle(movie)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .scale(favScale)
                    .background(Color.Black.copy(alpha = 0.55f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Error else Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Bottom info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    lineHeight = MaterialTheme.typography.labelLarge.fontSize * 1.3
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = String.format("%.1f", movie.voteAverage),
                        style = MaterialTheme.typography.labelSmall,
                        color = StarColor,
                        fontWeight = FontWeight.Bold
                    )
                    if (movie.releaseDate.length >= 4) {
                        Text(
                            text = "  ·  ${movie.releaseDate.take(4)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
