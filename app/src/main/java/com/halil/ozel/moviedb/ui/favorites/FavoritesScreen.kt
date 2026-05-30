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
import com.halil.ozel.moviedb.ui.theme.Background
import com.halil.ozel.moviedb.ui.theme.OnBackground
import com.halil.ozel.moviedb.ui.theme.Primary

@Composable
fun FavoritesScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            if (uiState.favorites.isNotEmpty()) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Primary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${uiState.favorites.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (uiState.favorites.isEmpty()) {
            EmptyState(
                title = "No favorites yet",
                subtitle = "Tap the heart icon on any movie to add it here"
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
    }
}
