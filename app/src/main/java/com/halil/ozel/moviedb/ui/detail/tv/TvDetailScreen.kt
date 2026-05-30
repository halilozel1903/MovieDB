package com.halil.ozel.moviedb.ui.detail.tv

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.Episode
import com.halil.ozel.moviedb.domain.model.Season
import com.halil.ozel.moviedb.domain.model.WatchProvider
import com.halil.ozel.moviedb.ui.components.RatingBar
import com.halil.ozel.moviedb.ui.components.SectionHeader
import com.halil.ozel.moviedb.ui.components.TvCard
import com.halil.ozel.moviedb.ui.components.shimmerBrush
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun TvDetailScreen(
    tvId: Int,
    onBack: () -> Unit,
    onTvClick: (Int) -> Unit,
    onCastClick: (Int, String) -> Unit = { _, _ -> },
    viewModel: TvDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(tvId) { viewModel.load(tvId) }
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

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(400.dp).background(shimmerBrush())
            )
        } else {
            uiState.tvSeries?.let { tv ->
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    // Backdrop
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        AsyncImage(
                            model = if (tv.backdropPath != null)
                                ApiConstants.IMAGE_BASE_URL_1280 + tv.backdropPath
                            else
                                ApiConstants.IMAGE_BASE_URL_500 + tv.posterPath,
                            contentDescription = tv.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Background.copy(alpha = 0.6f), Background)
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

                    // Poster + Info
                    Row(
                        modifier = Modifier.fillMaxWidth().offset(y = (-60).dp).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        AsyncImage(
                            model = ApiConstants.IMAGE_BASE_URL_500 + tv.posterPath,
                            contentDescription = tv.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.width(110.dp).height(165.dp).clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f).padding(bottom = 8.dp)) {
                            Text(
                                text = tv.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = OnBackground, fontWeight = FontWeight.Bold,
                                maxLines = 3, overflow = TextOverflow.Ellipsis
                            )
                            if (tv.firstAirDate.length >= 4) {
                                Text(tv.firstAirDate.take(4), style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            RatingBar(rating = tv.voteAverage)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(tv.genres) { genre ->
                                    Surface(shape = RoundedCornerShape(20.dp), color = SurfaceVariant) {
                                        Text(
                                            text = genre.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Secondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Overview + Stats
                    Column(
                        modifier = Modifier.fillMaxWidth().offset(y = (-50).dp).padding(horizontal = 16.dp)
                    ) {
                        Text("Overview", style = MaterialTheme.typography.titleLarge, color = OnBackground, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(tv.overview, style = MaterialTheme.typography.bodyMedium, color = OnSurface)

                        // Watch Providers
                        if (uiState.watchProviders.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Where to Watch",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.watchProviders.forEach { provider ->
                                    TvWatchProviderItem(provider = provider)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TvStatChip("Seasons", "${tv.numberOfSeasons}")
                            TvStatChip("Episodes", "${tv.numberOfEpisodes}")
                            TvStatChip("Language", tv.originalLanguage.uppercase())
                        }

                        // Seasons & Episodes
                        if (tv.seasons.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                "Seasons & Episodes",
                                style = MaterialTheme.typography.titleLarge,
                                color = OnBackground, fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            tv.seasons.forEach { season ->
                                SeasonRow(
                                    season = season,
                                    isExpanded = uiState.expandedSeasonNumber == season.seasonNumber,
                                    episodes = uiState.seasonEpisodes[season.seasonNumber] ?: emptyList(),
                                    episodesLoading = uiState.episodesLoading && uiState.expandedSeasonNumber == season.seasonNumber,
                                    onToggle = { viewModel.toggleSeasonExpanded(season.seasonNumber) }
                                )
                            }
                        }

                        // Cast
                        if (uiState.cast.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            SectionHeader(title = "Cast")
                        }
                    }

                    if (uiState.cast.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.offset(y = (-50).dp)
                        ) {
                            items(uiState.cast) { castMember ->
                                TvCastCard(
                                    cast = castMember,
                                    onClick = { onCastClick(castMember.id, castMember.name) }
                                )
                            }
                        }
                    }

                    if (uiState.recommended.isNotEmpty()) {
                        SectionHeader(title = "Recommended", modifier = Modifier.offset(y = (-40).dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.offset(y = (-40).dp)
                        ) {
                            items(uiState.recommended) { rec ->
                                TvCard(tvSeries = rec, onClick = onTvClick)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } ?: run {
                uiState.error?.let { error ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error, color = Error, style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }

        // Top bar overlay
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Background.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(Icons.Filled.ArrowBackIosNew, "Back", tint = OnBackground)
            }
            IconButton(
                onClick = { favAnimating = true; viewModel.toggleFavorite() },
                modifier = Modifier.scale(favScale).background(Background.copy(alpha = 0.6f), CircleShape)
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
private fun TvWatchProviderItem(provider: WatchProvider) {
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
private fun SeasonRow(
    season: Season,
    isExpanded: Boolean,
    episodes: List<Episode>,
    episodesLoading: Boolean,
    onToggle: () -> Unit
) {
    val arrowAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrow")

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        // Season header card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SurfaceVariant,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ApiConstants.IMAGE_BASE_URL_500 + season.posterPath,
                    contentDescription = season.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(50.dp).height(75.dp).clip(RoundedCornerShape(8.dp))
                        .background(Background)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = season.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnBackground, fontWeight = FontWeight.SemiBold
                    )
                    if (season.airDate.length >= 4) {
                        Text(season.airDate.take(4), style = MaterialTheme.typography.bodySmall, color = OnSurface)
                    }
                    Text(
                        text = "${season.episodeCount} bölüm",
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = OnSurface,
                    modifier = Modifier.rotate(arrowAngle)
                )
            }
        }

        // Episodes list (animated)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                if (episodesLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                    }
                } else {
                    episodes.forEach { episode ->
                        EpisodeItem(episode = episode)
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeItem(episode: Episode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 6.dp)
            .background(Background, RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Still image
        Box(
            modifier = Modifier.width(100.dp).height(60.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceVariant)
        ) {
            if (episode.stillPath != null) {
                AsyncImage(
                    model = ApiConstants.IMAGE_BASE_URL_500 + episode.stillPath,
                    contentDescription = episode.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Episode number badge
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(4.dp),
                shape = RoundedCornerShape(4.dp),
                color = Primary.copy(alpha = 0.9f)
            ) {
                Text(
                    text = "${episode.episodeNumber}",
                    color = OnPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = episode.name,
                style = MaterialTheme.typography.bodySmall,
                color = OnBackground, fontWeight = FontWeight.SemiBold,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            if (episode.airDate.isNotBlank()) {
                Text(episode.airDate, style = MaterialTheme.typography.labelSmall, color = OnSurface)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (episode.voteAverage > 0) {
                    Text(
                        text = "⭐ ${String.format("%.1f", episode.voteAverage)}",
                        style = MaterialTheme.typography.labelSmall, color = StarColor
                    )
                }
                if (episode.runtime > 0) {
                    Text(
                        text = "${episode.runtime} dk",
                        style = MaterialTheme.typography.labelSmall, color = OnSurface
                    )
                }
            }
            if (episode.overview.isNotBlank()) {
                Text(
                    text = episode.overview,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurface.copy(alpha = 0.8f),
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun TvStatChip(label: String, value: String) {
    Surface(shape = RoundedCornerShape(10.dp), color = SurfaceVariant) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, color = Primary, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurface)
        }
    }
}

@Composable
private fun TvCastCard(cast: Cast, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ApiConstants.IMAGE_BASE_URL_500 + cast.profilePath,
            contentDescription = cast.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(72.dp).clip(CircleShape).background(SurfaceVariant)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(cast.name, style = MaterialTheme.typography.labelSmall, color = OnBackground,
            maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        Text(cast.character, style = MaterialTheme.typography.labelSmall, color = OnSurface,
            maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}
