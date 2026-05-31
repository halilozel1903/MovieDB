package com.halil.ozel.moviedb.ui.detail.movie

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import com.halil.ozel.moviedb.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
import com.halil.ozel.moviedb.domain.model.WatchProvider
import com.halil.ozel.moviedb.ui.components.MovieCard
import com.halil.ozel.moviedb.ui.components.RatingBar
import com.halil.ozel.moviedb.ui.components.RatingBadgesRow
import com.halil.ozel.moviedb.ui.components.SectionHeader
import com.halil.ozel.moviedb.ui.components.shimmerBrush
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onCastClick: (Int, String) -> Unit = { _, _ -> },
    onGenreClick: (Int, String) -> Unit = { _, _ -> },
    onSeeAllSimilar: (Int) -> Unit = {},
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(movieId) {
        viewModel.load(movieId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var favAnimating by remember { mutableStateOf(false) }
    val favScale by animateFloatAsState(
        targetValue = if (favAnimating) 1.4f else 1f,
        animationSpec = spring(dampingRatio = 0.3f),
        finishedListener = { favAnimating = false },
        label = "fav_scale"
    )
    val favTint by animateColorAsState(
        targetValue = if (uiState.isFavorite) Error else Color.White,
        label = "fav_tint"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(shimmerBrush())
            )
        } else {
            uiState.movie?.let { movie ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Backdrop
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        AsyncImage(
                            model = if (movie.backdropPath != null)
                                ApiConstants.IMAGE_BASE_URL_1280 + movie.backdropPath
                            else
                                ApiConstants.IMAGE_BASE_URL_500 + movie.posterPath,
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Background.copy(alpha = 0.6f),
                                            Background
                                        )
                                    )
                                )
                        )
                        // Play button for trailer
                        if (uiState.trailer != null) {
                            IconButton(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.youtube.com/watch?v=${uiState.trailer!!.key}")
                                    )
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(56.dp)
                                    .background(Primary, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play Trailer",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    // Poster + Info row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-60).dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        AsyncImage(
                            model = ApiConstants.IMAGE_BASE_URL_500 + movie.posterPath,
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(110.dp)
                                .height(165.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = OnBackground,
                                fontWeight = FontWeight.Bold,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (movie.releaseDate.length >= 4) {
                                Text(
                                    text = movie.releaseDate.take(4),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            RatingBadgesRow(
                                voteAverage = movie.voteAverage,
                                voteCount = movie.voteCount,
                                imdbId = movie.imdbId,
                                externalRatings = uiState.externalRatings
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            // Genre chips — FlowRow fills available width
                            @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                movie.genres.forEach { genre ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = SurfaceVariant,
                                        modifier = Modifier.clickable { onGenreClick(genre.id, genre.name) }
                                    ) {
                                        Text(
                                            text = genre.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Secondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Overview
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-50).dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.overview_label),
                            style = MaterialTheme.typography.titleLarge,
                            color = OnBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = movie.overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                        )

                        // Watch Providers
                        if (uiState.watchProviders.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.where_to_watch_label),
                                style = MaterialTheme.typography.titleMedium,
                                color = OnBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.watchProviders.forEach { provider ->
                                    WatchProviderItem(provider = provider)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatChip(label = stringResource(R.string.runtime_label), value = if (movie.runtime > 0) "${movie.runtime} ${stringResource(R.string.min_suffix)}" else "N/A")
                            StatChip(label = stringResource(R.string.language_label), value = movie.originalLanguage.uppercase())
                            StatChip(label = stringResource(R.string.votes_label), value = "${movie.voteCount}")
                        }

                        // Cast
                        if (uiState.cast.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            SectionHeader(title = stringResource(R.string.cast_label))
                        }
                    }

                    if (uiState.cast.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.offset(y = (-50).dp)
                        ) {
                            items(uiState.cast) { castMember ->
                                CastCard(
                                    cast = castMember,
                                    onClick = { onCastClick(castMember.id, castMember.name) }
                                )
                            }
                        }
                    }

                    // Recommended
                    if (uiState.recommended.isNotEmpty()) {
                        SectionHeader(
                            title = stringResource(R.string.recommended_label),
                            onSeeAllClick = { onSeeAllSimilar(movieId) },
                            modifier = Modifier.offset(y = (-40).dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.offset(y = (-40).dp)
                        ) {
                            items(uiState.recommended) { rec ->
                                MovieCard(
                                    movie = rec,
                                    isFavorite = false,
                                    onFavoriteToggle = {},
                                    onClick = onMovieClick
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } ?: run {
                uiState.error?.let { error ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error,
                            color = Error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // Top bar overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Background.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = OnBackground
                )
            }

            IconButton(
                onClick = {
                    favAnimating = true
                    viewModel.toggleFavorite()
                },
                modifier = Modifier
                    .scale(favScale)
                    .background(Background.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = favTint
                )
            }
        }
    }
}

@Composable
private fun WatchProviderItem(provider: WatchProvider) {
    AsyncImage(
        model = "https://image.tmdb.org/t/p/w92" + provider.logoPath,
        contentDescription = provider.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
private fun StatChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurface
            )
        }
    }
}

@Composable
private fun CastCard(cast: com.halil.ozel.moviedb.domain.model.Cast, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ApiConstants.IMAGE_BASE_URL_500 + cast.profilePath,
            contentDescription = cast.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceVariant)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = cast.name,
            style = MaterialTheme.typography.labelSmall,
            color = OnBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = cast.character,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
