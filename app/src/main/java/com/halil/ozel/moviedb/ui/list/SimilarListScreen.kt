package com.halil.ozel.moviedb.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.halil.ozel.moviedb.R
import com.halil.ozel.moviedb.ui.components.MovieCard
import com.halil.ozel.moviedb.ui.components.TvCard
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun SimilarListScreen(
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit,
    viewModel: SimilarListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val info = gridState.layoutInfo
            val total = info.totalItemsCount
            if (total == 0) return@derivedStateOf false
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= total - 6
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) viewModel.loadNextPage() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(SurfaceVariant, CircleShape)
            ) {
                Icon(Icons.Filled.ArrowBackIosNew, "Back", tint = OnBackground)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.recommended_label),
                style = MaterialTheme.typography.headlineSmall,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            uiState.error != null && uiState.movies.isEmpty() && uiState.tvSeries.isEmpty() ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.error ?: "", color = Error)
                }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (viewModel.isMovie) {
                    items(uiState.movies) { movie ->
                        MovieCard(
                            movie = movie,
                            isFavorite = false,
                            onFavoriteToggle = {},
                            onClick = onMovieClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    items(uiState.tvSeries) { tv ->
                        TvCard(
                            tvSeries = tv,
                            onClick = onTvClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (uiState.isLoadingMore) {
                    item(span = { GridItemSpan(3) }) {
                        Box(
                            Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp)) }
                    }
                }
            }
        }
    }
}
