package com.halil.ozel.moviedb.ui.detail.person

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halil.ozel.moviedb.data.remote.api.ApiConstants
import com.halil.ozel.moviedb.domain.model.PersonCredit
import com.halil.ozel.moviedb.domain.model.PersonInfo
import com.halil.ozel.moviedb.ui.theme.*

@Composable
fun PersonDetailScreen(
    personId: Int,
    personName: String,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit,
    viewModel: PersonDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(personId) { viewModel.load(personId) }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top bar
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
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = OnBackground
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            // Profile photo + name + department
            val info = uiState.personInfo
            if (info != null) {
                AsyncImage(
                    model = ApiConstants.IMAGE_BASE_URL_500 + info.profilePath,
                    contentDescription = info.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariant)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = departmentIcon(info.knownForDepartment),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = info.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnBackground,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = info.knownForDepartment,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface
                    )
                }
            } else {
                Column {
                    Text(
                        text = personName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnBackground,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Filmography",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface
                    )
                }
            }
        }

        // Biography section
        uiState.personInfo?.let { info ->
            if (info.biography.isNotBlank()) {
                BiographySection(biography = info.biography)
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (uiState.credits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No credits found", color = OnSurface)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.credits) { credit ->
                    CreditItem(
                        credit = credit,
                        onClick = {
                            if (credit.mediaType == "movie") onMovieClick(credit.id)
                            else onTvClick(credit.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BiographySection(biography: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = biography,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurface,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
        )
        TextButton(
            onClick = { expanded = !expanded },
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = if (expanded) "Show less" else "...more",
                color = Primary,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

private fun departmentIcon(department: String): String = when (department) {
    "Acting" -> "🎬"
    "Sound" -> "🎤"
    "Directing" -> "🎥"
    "Writing" -> "🖊"
    else -> "🎭"
}

@Composable
private fun CreditItem(credit: PersonCredit, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            AsyncImage(
                model = ApiConstants.IMAGE_BASE_URL_500 + credit.posterPath,
                contentDescription = credit.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant)
            )
            // Movie / TV type badge with gradient background
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f),
                                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.45f)
                            )
                        ),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (credit.mediaType == "movie") "🎬" else "📺",
                    fontSize = 13.sp
                )
            }
            // Rating
            if (credit.voteAverage > 0) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Background.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "⭐ ${String.format("%.1f", credit.voteAverage)}",
                        color = StarColor,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = credit.title,
            style = MaterialTheme.typography.labelSmall,
            color = OnBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        if (credit.character.isNotBlank()) {
            Text(
                text = credit.character,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}
