package com.halil.ozel.moviedb.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.halil.ozel.moviedb.ui.theme.OnBackground

@Composable
fun GenreChip(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = OnBackground
            )
        },
        modifier = modifier
    )
}
