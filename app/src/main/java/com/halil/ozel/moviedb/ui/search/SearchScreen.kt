package com.halil.ozel.moviedb.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.PersonSearchResult
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.ui.components.EmptyState
import com.halil.ozel.moviedb.ui.components.RatingBar
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit,
    onPersonClick: (Int, String) -> Unit = { _, _ -> },
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val hasMovies = uiState.movies.isNotEmpty()
    val hasTv = uiState.tvSeries.isNotEmpty()
    val hasPeople = uiState.persons.isNotEmpty()
    val hasResults = hasMovies || hasTv || hasPeople
    val showChips = uiState.query.isNotBlank() && hasResults && !uiState.isLoading

    val visibleMovies = when (uiState.selectedFilter) {
        SearchFilter.ALL, SearchFilter.MOVIES -> uiState.movies
        else -> emptyList()
    }
    val visibleTv = when (uiState.selectedFilter) {
        SearchFilter.ALL, SearchFilter.TV -> uiState.tvSeries
        else -> emptyList()
    }
    val visiblePersons = when (uiState.selectedFilter) {
        SearchFilter.ALL, SearchFilter.PEOPLE -> uiState.persons
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // ── Search bar ───────────────────────────────────────────────────────
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = {
                Text(
                    text = "Film, dizi veya oyuncu ara…",
                    color = OnSurface.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = Primary)
            },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChanged("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Temizle", tint = OnSurface)
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = SurfaceVariant,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                focusedTextColor = OnBackground,
                unfocusedTextColor = OnBackground,
                cursorColor = Primary
            ),
            singleLine = true
        )

        // ── Filter chips ─────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = showChips,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SearchFilterChip(
                        label = "Tümü",
                        count = uiState.movies.size + uiState.tvSeries.size + uiState.persons.size,
                        icon = Icons.Filled.Search,
                        selected = uiState.selectedFilter == SearchFilter.ALL,
                        onClick = { viewModel.onFilterSelected(SearchFilter.ALL) }
                    )
                }
                if (hasMovies) item {
                    SearchFilterChip(
                        label = "Film",
                        count = uiState.movies.size,
                        icon = Icons.Filled.Movie,
                        selected = uiState.selectedFilter == SearchFilter.MOVIES,
                        onClick = { viewModel.onFilterSelected(SearchFilter.MOVIES) }
                    )
                }
                if (hasTv) item {
                    SearchFilterChip(
                        label = "Dizi",
                        count = uiState.tvSeries.size,
                        icon = Icons.Filled.Tv,
                        selected = uiState.selectedFilter == SearchFilter.TV,
                        onClick = { viewModel.onFilterSelected(SearchFilter.TV) }
                    )
                }
                if (hasPeople) item {
                    SearchFilterChip(
                        label = "Oyuncu",
                        count = uiState.persons.size,
                        icon = Icons.Filled.Person,
                        selected = uiState.selectedFilter == SearchFilter.PEOPLE,
                        onClick = { viewModel.onFilterSelected(SearchFilter.PEOPLE) }
                    )
                }
            }
        }

        // ── Content ──────────────────────────────────────────────────────────
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            uiState.query.isBlank() -> {
                EmptyState(
                    title = "Ne aramak istersiniz?",
                    subtitle = "Film, dizi veya oyuncu adı yazın"
                )
            }

            !hasResults -> {
                EmptyState(
                    title = "Sonuç bulunamadı",
                    subtitle = "\"${uiState.query}\" için eşleşen içerik yok"
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Movies
                    if (visibleMovies.isNotEmpty()) {
                        item {
                            SearchSectionHeader(
                                icon = Icons.Filled.Movie,
                                title = "Filmler",
                                count = visibleMovies.size
                            )
                        }
                        items(visibleMovies) { movie ->
                            SearchMovieItem(movie = movie, onClick = { onMovieClick(movie.id) })
                        }
                    }

                    // TV Shows
                    if (visibleTv.isNotEmpty()) {
                        item {
                            SearchSectionHeader(
                                icon = Icons.Filled.Tv,
                                title = "Diziler",
                                count = visibleTv.size
                            )
                        }
                        items(visibleTv) { tv ->
                            SearchTvItem(tv = tv, onClick = { onTvClick(tv.id) })
                        }
                    }

                    // People
                    if (visiblePersons.isNotEmpty()) {
                        item {
                            SearchSectionHeader(
                                icon = Icons.Filled.Person,
                                title = "Oyuncular",
                                count = visiblePersons.size
                            )
                        }
                        items(visiblePersons) { person ->
                            SearchPersonItem(
                                person = person,
                                onClick = { onPersonClick(person.id, person.name) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Filter chip ───────────────────────────────────────────────────────────────

@Composable
private fun SearchFilterChip(
    label: String,
    count: Int,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                )
                if (count > 0) {
                    Surface(
                        shape = CircleShape,
                        color = if (selected) Primary.copy(alpha = 0.2f) else SurfaceVariant
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Primary else OnSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Primary.copy(alpha = 0.15f),
            selectedLabelColor = Primary,
            selectedLeadingIconColor = Primary,
            containerColor = SurfaceVariant,
            labelColor = OnSurface,
            iconColor = OnSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = Primary.copy(alpha = 0.5f),
            borderColor = SurfaceVariant
        )
    )
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SearchSectionHeader(icon: ImageVector, title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = OnBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "($count)",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurface
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = SurfaceVariant,
            thickness = 1.dp
        )
    }
}

// ── Movie item ────────────────────────────────────────────────────────────────

@Composable
private fun SearchMovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + movie.posterPath,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(58.dp)
                    .height(87.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                // Type badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Primary.copy(alpha = 0.15f),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "FİLM",
                        color = Primary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBackground,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (movie.releaseDate.length >= 4) {
                    Text(
                        text = movie.releaseDate.take(4),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                RatingBar(rating = movie.voteAverage, starSize = 12.dp)
                if (movie.overview.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface.copy(alpha = 0.75f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ── TV item ───────────────────────────────────────────────────────────────────

@Composable
private fun SearchTvItem(tv: TvSeries, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + tv.posterPath,
                contentDescription = tv.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(58.dp)
                    .height(87.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Secondary.copy(alpha = 0.15f),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "DİZİ",
                        color = Secondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = tv.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBackground,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (tv.firstAirDate.length >= 4) {
                    Text(
                        text = tv.firstAirDate.take(4),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                RatingBar(rating = tv.voteAverage, starSize = 12.dp)
                if (tv.overview.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tv.overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface.copy(alpha = 0.75f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ── Person item ───────────────────────────────────────────────────────────────

@Composable
private fun SearchPersonItem(person: PersonSearchResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile photo (circle)
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + person.profilePath,
                contentDescription = person.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = StarColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "OYUNCU",
                        color = StarColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBackground,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (person.knownForDepartment.isNotBlank()) {
                    val deptIcon = when (person.knownForDepartment) {
                        "Acting" -> "🎭"
                        "Directing" -> "🎥"
                        "Writing" -> "🖊"
                        "Production" -> "🎬"
                        else -> "🎭"
                    }
                    Text(
                        text = "$deptIcon ${person.knownForDepartment}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = OnSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
