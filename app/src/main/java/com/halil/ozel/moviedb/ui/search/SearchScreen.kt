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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.R
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.PersonSearchResult
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.ui.components.MovieCard
import com.halil.ozel.moviedb.ui.components.RatingBar
import com.halil.ozel.moviedb.ui.components.SectionHeader
import com.halil.ozel.moviedb.ui.components.TvCard
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit,
    onPersonClick: (Int, String) -> Unit = { _, _ -> },
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val hasMovies   = uiState.movies.isNotEmpty()
    val hasTv       = uiState.tvSeries.isNotEmpty()
    val hasPeople   = uiState.persons.isNotEmpty()
    val hasResults  = hasMovies || hasTv || hasPeople
    val showChips   = uiState.query.isNotBlank() && hasResults && !uiState.isLoading
    val isBlank     = uiState.query.isBlank()

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
            value       = uiState.query,
            onValueChange = viewModel::onQueryChanged,
            modifier    = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = {
                Text(stringResource(R.string.search_placeholder), color = OnSurface.copy(alpha = 0.6f))
            },
            leadingIcon  = { Icon(Icons.Filled.Search, null, tint = Primary) },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChanged("") }) {
                        Icon(Icons.Filled.Clear, null, tint = OnSurface)
                    }
                }
            },
            shape  = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor    = Primary,
                unfocusedBorderColor  = SurfaceVariant,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                focusedTextColor      = OnBackground,
                unfocusedTextColor    = OnBackground,
                cursorColor           = Primary
            ),
            singleLine = true
        )

        // ── Filter chips ─────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = showChips,
            enter   = fadeIn() + expandVertically(),
            exit    = fadeOut() + shrinkVertically()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SearchFilterChip(
                        label    = stringResource(R.string.search_filter_all),
                        count    = uiState.movies.size + uiState.tvSeries.size + uiState.persons.size,
                        icon     = Icons.Filled.Search,
                        selected = uiState.selectedFilter == SearchFilter.ALL,
                        onClick  = { viewModel.onFilterSelected(SearchFilter.ALL) }
                    )
                }
                if (hasMovies) item {
                    SearchFilterChip(
                        label    = stringResource(R.string.search_filter_movies),
                        count    = uiState.movies.size,
                        icon     = Icons.Filled.Movie,
                        selected = uiState.selectedFilter == SearchFilter.MOVIES,
                        onClick  = { viewModel.onFilterSelected(SearchFilter.MOVIES) }
                    )
                }
                if (hasTv) item {
                    SearchFilterChip(
                        label    = stringResource(R.string.search_filter_tv),
                        count    = uiState.tvSeries.size,
                        icon     = Icons.Filled.Tv,
                        selected = uiState.selectedFilter == SearchFilter.TV,
                        onClick  = { viewModel.onFilterSelected(SearchFilter.TV) }
                    )
                }
                if (hasPeople) item {
                    SearchFilterChip(
                        label    = stringResource(R.string.search_filter_people),
                        count    = uiState.persons.size,
                        icon     = Icons.Filled.Person,
                        selected = uiState.selectedFilter == SearchFilter.PEOPLE,
                        onClick  = { viewModel.onFilterSelected(SearchFilter.PEOPLE) }
                    )
                }
            }
        }

        // ── Content ──────────────────────────────────────────────────────────
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }

            // ── BLANK QUERY → Trending discovery ────────────────────────────
            isBlank -> {
                if (uiState.isTrendingLoading) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp)
                    ) {
                        // ── Trending people pills (tıklayınca isimle arama) ─
                        if (uiState.trendingPeople.isNotEmpty()) {
                            SectionHeader(title = stringResource(R.string.trending_people))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(uiState.trendingPeople.take(12)) { person ->
                                    TrendingPersonPill(
                                        person  = person,
                                        onClick = { viewModel.onQueryChanged(person.name) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // Top 10 movies
                        if (uiState.trendingMovies.isNotEmpty()) {
                            SectionHeader(title = stringResource(R.string.trending_movies_week))
                            LazyRow(
                                contentPadding = PaddingValues(start = 8.dp, end = 16.dp)
                            ) {
                                itemsIndexed(uiState.trendingMovies.take(10)) { idx, movie ->
                                    RankedMovieCard(rank = idx + 1, movie = movie, onClick = onMovieClick)
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                        }
                        // Top 10 TV
                        if (uiState.trendingTv.isNotEmpty()) {
                            SectionHeader(title = stringResource(R.string.trending_tv_week))
                            LazyRow(
                                contentPadding = PaddingValues(start = 8.dp, end = 16.dp)
                            ) {
                                itemsIndexed(uiState.trendingTv.take(10)) { idx, tv ->
                                    RankedTvCard(rank = idx + 1, tv = tv, onClick = onTvClick)
                                }
                            }
                        }
                    }
                }
            }

            !hasResults -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = stringResource(R.string.search_no_results),
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = stringResource(R.string.search_no_results_sub, uiState.query),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface
                    )
                }
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (visibleMovies.isNotEmpty()) {
                    item { SearchSectionHeader(Icons.Filled.Movie, stringResource(R.string.section_movies), visibleMovies.size) }
                    items(visibleMovies) { SearchMovieItem(it) { onMovieClick(it.id) } }
                }
                if (visibleTv.isNotEmpty()) {
                    item { SearchSectionHeader(Icons.Filled.Tv, stringResource(R.string.section_tv_shows), visibleTv.size) }
                    items(visibleTv) { SearchTvItem(it) { onTvClick(it.id) } }
                }
                if (visiblePersons.isNotEmpty()) {
                    item { SearchSectionHeader(Icons.Filled.Person, stringResource(R.string.section_actors), visiblePersons.size) }
                    items(visiblePersons) { SearchPersonItem(it) { onPersonClick(it.id, it.name) } }
                }
            }
        }
    }
}

// ── Filter chip ───────────────────────────────────────────────────────────────

@Composable
private fun SearchFilterChip(
    label: String, count: Int, icon: ImageVector,
    selected: Boolean, onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                if (count > 0) Surface(shape = CircleShape, color = if (selected) Primary.copy(alpha = 0.2f) else SurfaceVariant) {
                    Text("$count", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = if (selected) Primary else OnSurface,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp))
                }
            }
        },
        leadingIcon = { Icon(icon, null, modifier = Modifier.size(16.dp)) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Primary.copy(alpha = 0.15f),
            selectedLabelColor = Primary, selectedLeadingIconColor = Primary,
            containerColor = SurfaceVariant, labelColor = OnSurface, iconColor = OnSurface
        ),
        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selected,
            selectedBorderColor = Primary.copy(alpha = 0.5f), borderColor = SurfaceVariant)
    )
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SearchSectionHeader(icon: ImageVector, title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = Primary, modifier = Modifier.size(18.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = OnBackground, fontWeight = FontWeight.Bold)
        Text("($count)", style = MaterialTheme.typography.bodySmall, color = OnSurface)
        HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceVariant, thickness = 1.dp)
    }
}

// ── Result items ──────────────────────────────────────────────────────────────

@Composable
private fun SearchMovieItem(movie: Movie, onClick: () -> Unit) {
    val badge = stringResource(R.string.badge_movie)
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        onClick = onClick
    ) {
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + movie.posterPath,
                contentDescription = movie.title, contentScale = ContentScale.Crop,
                modifier = Modifier.width(58.dp).height(87.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceVariant)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                TypeBadge(badge, Primary)
                Text(movie.title, style = MaterialTheme.typography.titleSmall, color = OnBackground,
                    fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (movie.releaseDate.length >= 4)
                    Text(movie.releaseDate.take(4), style = MaterialTheme.typography.bodySmall, color = OnSurface, modifier = Modifier.padding(top = 2.dp))
                Spacer(Modifier.height(4.dp))
                RatingBar(rating = movie.voteAverage, starSize = 12.dp)
                if (movie.overview.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(movie.overview, style = MaterialTheme.typography.bodySmall, color = OnSurface.copy(alpha = 0.75f),
                        maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun SearchTvItem(tv: TvSeries, onClick: () -> Unit) {
    val badge = stringResource(R.string.badge_tv)
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        onClick = onClick
    ) {
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + tv.posterPath,
                contentDescription = tv.name, contentScale = ContentScale.Crop,
                modifier = Modifier.width(58.dp).height(87.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceVariant)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                TypeBadge(badge, Secondary)
                Text(tv.name, style = MaterialTheme.typography.titleSmall, color = OnBackground,
                    fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (tv.firstAirDate.length >= 4)
                    Text(tv.firstAirDate.take(4), style = MaterialTheme.typography.bodySmall, color = OnSurface, modifier = Modifier.padding(top = 2.dp))
                Spacer(Modifier.height(4.dp))
                RatingBar(rating = tv.voteAverage, starSize = 12.dp)
                if (tv.overview.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(tv.overview, style = MaterialTheme.typography.bodySmall, color = OnSurface.copy(alpha = 0.75f),
                        maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun SearchPersonItem(person: PersonSearchResult, onClick: () -> Unit) {
    val badge = stringResource(R.string.badge_actor)
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        onClick = onClick
    ) {
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + person.profilePath,
                contentDescription = person.name, contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).clip(CircleShape).background(SurfaceVariant)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                TypeBadge(badge, StarColor)
                Text(person.name, style = MaterialTheme.typography.titleSmall, color = OnBackground,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (person.knownForDepartment.isNotBlank()) {
                    val icon = when (person.knownForDepartment) {
                        "Acting" -> "🎭"; "Directing" -> "🎥"; "Writing" -> "🖊"; "Production" -> "🎬"; else -> "🎭"
                    }
                    Text("$icon ${person.knownForDepartment}", style = MaterialTheme.typography.bodySmall,
                        color = OnSurface, modifier = Modifier.padding(top = 2.dp))
                }
            }
            Icon(Icons.Filled.Person, null, tint = OnSurface.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun TypeBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Surface(shape = RoundedCornerShape(4.dp), color = color.copy(alpha = 0.15f),
        modifier = Modifier.padding(bottom = 4.dp)) {
        Text(text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

// ── Netflix-style Top 10 ranked cards ────────────────────────────────────────

private val RANKED_CARD_W = 130.dp
private val RANKED_CARD_H = 195.dp

@Composable
private fun RankedMovieCard(rank: Int, movie: Movie, onClick: (Int) -> Unit) {
    RankedItem(rank = rank) {
        MovieCard(
            movie = movie, isFavorite = false, onFavoriteToggle = {},
            onClick = onClick, modifier = it
        )
    }
}

@Composable
private fun RankedTvCard(rank: Int, tv: TvSeries, onClick: (Int) -> Unit) {
    RankedItem(rank = rank) {
        TvCard(tvSeries = tv, onClick = onClick, modifier = it)
    }
}

@Composable
private fun RankedItem(
    rank: Int,
    card: @Composable BoxScope.(Modifier) -> Unit
) {
    val density = androidx.compose.ui.platform.LocalDensity.current

    val numFontSize  = with(density) { (RANKED_CARD_H * 0.85f).toSp() }
    val letterSp     = if (rank >= 10) (-6).sp else 0.sp
    val strokeColor  = androidx.compose.ui.graphics.Color(0xFFB0B0C8)
    val fillColor    = Background
    val strokeWidth  = with(density) { 3.dp.toPx() }

    Row(
        modifier = Modifier.height(RANKED_CARD_H),
        verticalAlignment = Alignment.Bottom
    ) {
        // ── Number ───────────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .offset(x = 8.dp, y = 10.dp)
                .wrapContentWidth(unbounded = true)
        ) {
            // Stroke outline
            Text(
                text = "$rank",
                letterSpacing = letterSp,
                color = strokeColor,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = numFontSize,
                    lineHeight = numFontSize,
                    fontWeight = FontWeight.Black,
                    letterSpacing = letterSp,
                    drawStyle = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                )
            )
            // Dark fill
            Text(
                text = "$rank",
                letterSpacing = letterSp,
                color = fillColor,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = numFontSize,
                    lineHeight = numFontSize,
                    fontWeight = FontWeight.Black,
                    letterSpacing = letterSp
                )
            )
        }

        // ── Poster card ──────────────────────────────────────────────────
        Box(modifier = Modifier.offset(x = (-12).dp)) {
            card(
                Modifier
                    .width(RANKED_CARD_W)
                    .height(RANKED_CARD_H)
            )
        }
    }
}

// ── Trending person pill ──────────────────────────────────────────────────────

@Composable
private fun TrendingPersonPill(
    person: PersonSearchResult,
    onClick: () -> Unit
) {
    Surface(
        shape  = RoundedCornerShape(40.dp),
        color  = SurfaceVariant,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(end = 12.dp, top = 6.dp, bottom = 6.dp, start = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Profile photo
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + person.profilePath,
                contentDescription = person.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Background)
            )
            Column {
                Text(
                    text       = person.name,
                    style      = MaterialTheme.typography.labelMedium,
                    color      = OnBackground,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                if (person.knownForDepartment.isNotBlank()) {
                    Text(
                        text  = person.knownForDepartment,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurface.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
