package com.halil.ozel.moviedb.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
import com.halil.ozel.moviedb.ui.components.*
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    showTv: Boolean = false,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Banner
        if (!showTv) {
            val featuredMovie = uiState.nowPlayingMovies.firstOrNull()
            featuredMovie?.let { movie ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    AsyncImage(
                        model = ApiConstants.IMAGE_BASE_URL_1280 + movie.backdropPath,
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
                                        Background.copy(alpha = 0.1f),
                                        Background.copy(alpha = 0.7f),
                                        Background
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnBackground,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        RatingBar(
                            rating = movie.voteAverage,
                            starSize = 14.dp
                        )
                    }
                }
            } ?: run {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(shimmerBrush())
                    )
                }
            }
        } else {
            // TV Hero
            val featuredTv = uiState.popularTv.firstOrNull()
            featuredTv?.let { tv ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
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
                                        Background.copy(alpha = 0.1f),
                                        Background.copy(alpha = 0.7f),
                                        Background
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = tv.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnBackground,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        RatingBar(rating = tv.voteAverage, starSize = 14.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!showTv) {
            // Now Playing
            SectionHeader(title = "🎬 Now Playing")
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.nowPlayingMovies) { movie ->
                        MovieCard(
                            movie = movie,
                            isFavorite = favoriteIds.contains(movie.id),
                            onFavoriteToggle = viewModel::toggleFavorite,
                            onClick = onMovieClick
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Popular
            SectionHeader(title = "🔥 Popular")
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.popularMovies) { movie ->
                        MovieCard(
                            movie = movie,
                            isFavorite = favoriteIds.contains(movie.id),
                            onFavoriteToggle = viewModel::toggleFavorite,
                            onClick = onMovieClick
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Top Rated
            SectionHeader(title = "⭐ Top Rated")
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.topRatedMovies) { movie ->
                        MovieCard(
                            movie = movie,
                            isFavorite = favoriteIds.contains(movie.id),
                            onFavoriteToggle = viewModel::toggleFavorite,
                            onClick = onMovieClick
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upcoming
            SectionHeader(title = "🗓 Upcoming")
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.upcomingMovies) { movie ->
                        MovieCard(
                            movie = movie,
                            isFavorite = favoriteIds.contains(movie.id),
                            onFavoriteToggle = viewModel::toggleFavorite,
                            onClick = onMovieClick
                        )
                    }
                }
            }
        } else {
            // TV sections
            SectionHeader(title = "🔥 Popular TV")
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.popularTv) { tv ->
                        TvCard(tvSeries = tv, onClick = onMovieClick)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "⭐ Top Rated TV")
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.topRatedTv) { tv ->
                        TvCard(tvSeries = tv, onClick = onMovieClick)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "📺 Airing Today")
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.airingTodayTv) { tv ->
                        TvCard(tvSeries = tv, onClick = onMovieClick)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
