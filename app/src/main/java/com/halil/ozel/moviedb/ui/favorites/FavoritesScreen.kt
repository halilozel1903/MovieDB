package com.halil.ozel.moviedb.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.halil.ozel.moviedb.ui.components.EmptyState
import com.halil.ozel.moviedb.ui.components.MovieCard
import com.halil.ozel.moviedb.ui.components.TvCard
import com.halil.ozel.moviedb.ui.theme.Background
import com.halil.ozel.moviedb.ui.theme.OnBackground
import com.halil.ozel.moviedb.ui.theme.Primary
import com.halil.ozel.moviedb.ui.theme.Surface

@Composable
fun FavoritesScreen(
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("🎬 Films", "📺 Diziler")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "❤️ Favorites",
                style = MaterialTheme.typography.headlineMedium,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            val count = if (selectedTab == 0) uiState.favorites.size else uiState.tvFavorites.size
            if (count > 0) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Primary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Background,
            contentColor = Primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) Primary else OnBackground.copy(alpha = 0.6f)
                        )
                    }
                )
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else when (selectedTab) {
            0 -> if (uiState.favorites.isEmpty()) {
                EmptyState(
                    title = "No movie favorites",
                    subtitle = "Tap the heart on any movie"
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.favorites, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            isFavorite = true,
                            onFavoriteToggle = viewModel::toggleFavorite,
                            onClick = onMovieClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            1 -> if (uiState.tvFavorites.isEmpty()) {
                EmptyState(
                    title = "No TV favorites",
                    subtitle = "Tap the heart on any TV show"
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tvFavorites, key = { it.id }) { tv ->
                        TvCard(
                            tvSeries = tv,
                            isFavorite = true,
                            onFavoriteToggle = viewModel::toggleTvFavorite,
                            onClick = onTvClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
