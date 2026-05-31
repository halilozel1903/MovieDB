package com.halil.ozel.moviedb.ui.home

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
import com.halil.ozel.moviedb.navigation.MediaCategory
import com.halil.ozel.moviedb.ui.components.MovieCard
import com.halil.ozel.moviedb.ui.components.RatingBar
import com.halil.ozel.moviedb.ui.components.SectionHeader
import com.halil.ozel.moviedb.ui.components.ShimmerMovieRow
import com.halil.ozel.moviedb.ui.components.TvCard
import com.halil.ozel.moviedb.ui.components.shimmerBrush
import com.halil.ozel.moviedb.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit = {},
    onSeeAllClick: ((MediaCategory) -> Unit)? = null,
    showTv: Boolean = false,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val tvFavoriteIds by viewModel.tvFavoriteIds.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

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
            SectionHeader(
                title = "🎬 Now Playing",
                onSeeAllClick = onSeeAllClick?.let { { it(MediaCategory.MOVIE_NOW_PLAYING) } }
            )
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.nowPlayingMovies.take(6)) { movie ->
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
            SectionHeader(
                title = "🔥 Popular",
                onSeeAllClick = onSeeAllClick?.let { { it(MediaCategory.MOVIE_POPULAR) } }
            )
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.popularMovies.take(6)) { movie ->
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
            SectionHeader(
                title = "⭐ Top Rated",
                onSeeAllClick = onSeeAllClick?.let { { it(MediaCategory.MOVIE_TOP_RATED) } }
            )
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.topRatedMovies.take(6)) { movie ->
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
            SectionHeader(
                title = "🗓 Upcoming",
                onSeeAllClick = onSeeAllClick?.let { { it(MediaCategory.MOVIE_UPCOMING) } }
            )
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.upcomingMovies.take(6)) { movie ->
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
            SectionHeader(
                title = "🔥 Popular TV",
                onSeeAllClick = onSeeAllClick?.let { { it(MediaCategory.TV_POPULAR) } }
            )
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.popularTv.take(6)) { tv ->
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

            SectionHeader(
                title = "⭐ Top Rated TV",
                onSeeAllClick = onSeeAllClick?.let { { it(MediaCategory.TV_TOP_RATED) } }
            )
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.topRatedTv.take(6)) { tv ->
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

            SectionHeader(
                title = "📺 Airing Today",
                onSeeAllClick = onSeeAllClick?.let { { it(MediaCategory.TV_AIRING_TODAY) } }
            )
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.airingTodayTv.take(6)) { tv ->
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

            SectionHeader(
                title = "📡 On The Air",
                onSeeAllClick = onSeeAllClick?.let { { it(MediaCategory.TV_ON_THE_AIR) } }
            )
            if (uiState.isLoading) ShimmerMovieRow()
            else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.onTheAirTv.take(6)) { tv ->
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
