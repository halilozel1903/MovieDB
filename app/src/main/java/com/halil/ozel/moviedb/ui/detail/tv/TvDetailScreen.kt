package com.halil.ozel.moviedb.ui.detail.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
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
    viewModel: TvDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(tvId) {
        viewModel.load(tvId)
    }

    val uiState by viewModel.uiState.collectAsState()

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
            uiState.tvSeries?.let { tv ->
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
                            model = ApiConstants.IMAGE_BASE_URL_1280 + tv.backdropPath,
                            contentDescription = tv.name,
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
                            model = ApiConstants.IMAGE_BASE_URL_500 + tv.posterPath,
                            contentDescription = tv.name,
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
                                text = tv.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = OnBackground,
                                fontWeight = FontWeight.Bold,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (tv.firstAirDate.length >= 4) {
                                Text(
                                    text = tv.firstAirDate.take(4),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            RatingBar(rating = tv.voteAverage)
                            Spacer(modifier = Modifier.height(6.dp))
                            // Genre chips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(tv.genres) { genre ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = SurfaceVariant
                                    ) {
                                        Text(
                                            text = genre,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Secondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Overview + stats
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-50).dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Overview",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tv.overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TvStatChip(label = "Seasons", value = "${tv.numberOfSeasons}")
                            TvStatChip(label = "Episodes", value = "${tv.numberOfEpisodes}")
                            TvStatChip(label = "Language", value = tv.originalLanguage.uppercase())
                        }

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
                                TvCastCard(cast = castMember)
                            }
                        }
                    }

                    if (uiState.recommended.isNotEmpty()) {
                        SectionHeader(
                            title = "Recommended",
                            modifier = Modifier.offset(y = (-40).dp)
                        )
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

        // Back button overlay
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .background(Background.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back",
                tint = OnBackground
            )
        }
    }
}

@Composable
private fun TvStatChip(label: String, value: String) {
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
private fun TvCastCard(cast: com.halil.ozel.moviedb.domain.model.Cast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
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
