package com.halil.ozel.moviedb.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halil.ozel.moviedb.R
import com.halil.ozel.moviedb.domain.model.ExternalRatings

// ── Brand colours ─────────────────────────────────────────────────────────────
private val TmdbBlue   = Color(0xFF01B4E4)
private val TmdbGreen  = Color(0xFF90CEA1)
private val ImdbYellow = Color(0xFFF5C518)
private val ImdbDark   = Color(0xFF111111)
private val RtRed      = Color(0xFFFA320A)
private val MetaGreen  = Color(0xFF66CC33)

// All badges share the same surface colour; brand logos carry the identity
private val BadgeBg    = Color(0xFF1E2235)
private val BADGE_CORNER = 10.dp

// ── Data class for a single badge ─────────────────────────────────────────────
private data class BadgeData(
    val key: String,
    val logo: @Composable () -> Unit,
    val score: String,
    val sub: String,
    val onClick: (() -> Unit)? = null
)

@Composable
fun RatingBadgesRow(
    voteAverage: Double,
    voteCount: Int,
    imdbId: String?,
    externalRatings: ExternalRatings?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val seeOnImdb = stringResource(R.string.see_on_imdb)

    val badges = remember(voteAverage, voteCount, imdbId, externalRatings) {
        buildList {
            // TMDb — always shown
            add(BadgeData(
                key   = "tmdb",
                logo  = { TmdbLogo() },
                score = String.format("%.1f", voteAverage),
                sub   = formatCount(voteCount)
            ))

            // IMDb — when imdbId is known
            if (!imdbId.isNullOrBlank()) {
                add(BadgeData(
                    key     = "imdb",
                    logo    = { ImdbLogo() },
                    score   = externalRatings?.imdbRating?.let { String.format("%.1f", it) } ?: "—",
                    sub     = externalRatings?.imdbVotes?.let { formatRawCount(it) } ?: seeOnImdb,
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.imdb.com/title/$imdbId/"))
                        )
                    }
                ))
            }

            // Rotten Tomatoes — when data available
            externalRatings?.rottenTomatoes?.let { rt ->
                add(BadgeData(
                    key   = "rt",
                    logo  = { RtLogo() },
                    score = rt,            // e.g. "98%"
                    sub   = "Tomatometer"
                ))
            }

            // Metacritic — when > 0
            externalRatings?.metacritic?.let { m ->
                if (m > 0) add(BadgeData(
                    key   = "meta",
                    logo  = { MetaLogo() },
                    score = "$m",
                    sub   = "Metacritic"
                ))
            }
        }
    }

    LazyRow(
        modifier             = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment    = Alignment.CenterVertically
    ) {
        items(badges, key = { it.key }) { badge ->
            RatingBadge(
                logo     = badge.logo,
                score    = badge.score,
                sub      = badge.sub,
                modifier = if (badge.onClick != null)
                    Modifier.clickable(onClick = badge.onClick)
                else Modifier
            )
        }
    }
}

// ── Single badge ─────────────────────────────────────────────────────────────

@Composable
private fun RatingBadge(
    logo: @Composable () -> Unit,
    score: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(BADGE_CORNER))
            .background(BadgeBg)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        logo()
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text       = score,
                fontSize   = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                maxLines   = 1,
                softWrap   = false,
                overflow   = TextOverflow.Clip
            )
            Text(
                text     = sub,
                fontSize = 8.5.sp,
                color    = Color.White.copy(alpha = 0.55f),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Logos ─────────────────────────────────────────────────────────────────────

@Composable private fun TmdbLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("TM", fontSize = 11.sp, fontWeight = FontWeight.Black,
            color = TmdbBlue,  letterSpacing = (-0.5).sp)
        Text("db", fontSize = 11.sp, fontWeight = FontWeight.Black,
            color = TmdbGreen, letterSpacing = (-0.5).sp)
    }
}

@Composable private fun ImdbLogo() {
    Box(
        Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(ImdbYellow)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text("IMDb", fontSize = 9.sp, fontWeight = FontWeight.Black,
            color = ImdbDark, letterSpacing = (-0.3).sp)
    }
}

@Composable private fun RtLogo() {
    Box(
        Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(RtRed)
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text("RT", fontSize = 9.sp, fontWeight = FontWeight.Black,
            color = Color.White, letterSpacing = (-0.2).sp)
    }
}

@Composable private fun MetaLogo() {
    Box(
        Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(MetaGreen)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text("MC", fontSize = 9.sp, fontWeight = FontWeight.Black,
            color = Color.White, letterSpacing = (-0.2).sp)
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatCount(n: Int) = when {
    n >= 1_000_000 -> String.format("%.1fM oy", n / 1_000_000.0)
    n >= 1_000     -> String.format("%.1fK oy", n / 1_000.0)
    else           -> "$n oy"
}

private fun formatRawCount(raw: String): String {
    val n = raw.replace(",", "").toLongOrNull() ?: return raw
    return when {
        n >= 1_000_000 -> String.format("%.1fM oy", n / 1_000_000.0)
        n >= 1_000     -> String.format("%.1fK oy", n / 1_000.0)
        else           -> "$n oy"
    }
}
