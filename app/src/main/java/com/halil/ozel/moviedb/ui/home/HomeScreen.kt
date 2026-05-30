package com.halil.ozel.moviedb.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit = {},
    showTv: Boolean = false,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val tvFavoriteIds by viewModel.tvFavoriteIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Auto-scrolling Hero Banner
        if (!showTv) {
            val bannerItems = uiState.nowPlayingMovies.take(5)
            if (bannerItems.isEmpty()) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(shimmerBrush())
                    )
                }
            } else {
                val pagerState = rememberPagerState(pageCount = { bannerItems.size })
                LaunchedEffect(pagerState) {
                    while (true) {
                        delay(3500)
                        val next = (pagerState.currentPage + 1) % bannerItems.size
                        pagerState.animateScrollToPage(next)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val movie = bannerItems[page]
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onMovieClick(movie.id) }
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
                                    .padding(start = 16.dp, end = 16.dp, bottom = 28.dp)
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
                                RatingBar(rating = movie.voteAverage, starSize = 14.dp)
                            }
                        }
                    }
                    // Dot indicators
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(bannerItems.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == pagerState.currentPage) 8.dp else 5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == pagerState.currentPage) Primary
                                        else OnSurface.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                }
            }
        } else {
            // TV Hero Banner
            val bannerItems = uiState.popularTv.take(5)
            if (bannerItems.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { bannerItems.size })
                LaunchedEffect(pagerState) {
                    while (true) {
                        delay(3500)
                        val next = (pagerState.currentPage + 1) % bannerItems.size
                        pagerState.animateScrollToPage(next)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val tv = bannerItems[page]
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onTvClick(tv.id) }
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
                                    .padding(start = 16.dp, end = 16.dp, bottom = 28.dp)
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
                    // Dot indicators
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(bannerItems.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == pagerState.currentPage) 8.dp else 5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == pagerState.currentPage) Primary
                                        else OnSurface.copy(alpha = 0.4f)
                                    )
                            )
                        }
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
                        TvCard(
                            tvSeries = tv,
                            onClick = onTvClick,
                            isFavorite = tvFavoriteIds.contains(tv.id),
                            onFavoriteToggle = viewModel::toggleTvFavorite
                        )
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
                        TvCard(
                            tvSeries = tv,
                            onClick = onTvClick,
                            isFavorite = tvFavoriteIds.contains(tv.id),
                            onFavoriteToggle = viewModel::toggleTvFavorite
                        )
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
                        TvCard(
                            tvSeries = tv,
                            onClick = onTvClick,
                            isFavorite = tvFavoriteIds.contains(tv.id),
                            onFavoriteToggle = viewModel::toggleTvFavorite
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
