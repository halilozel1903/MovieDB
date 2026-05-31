package com.halil.ozel.moviedb.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halil.ozel.moviedb.R
import com.halil.ozel.moviedb.domain.model.ExternalRatings

private val TmdbBlue   = Color(0xFF01B4E4)
private val TmdbGreen  = Color(0xFF90CEA1)
private val ImdbYellow = Color(0xFFF5C518)
private val ImdbBg     = Color(0xFF111111)
private val RtRed      = Color(0xFFFA320A)
private val MetaGreen  = Color(0xFF66CC33)

private val BADGE_HEIGHT = 52.dp
private val BADGE_CORNER = 10.dp

@Composable
fun RatingBadgesRow(
    voteAverage: Double,
    voteCount: Int,
    imdbId: String?,
    externalRatings: ExternalRatings?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── TMDb ───────────────────────────────────────────────────────────
        RatingBadge(
            logo  = { TmdbLogo() },
            score = String.format("%.1f", voteAverage),
            sub   = formatVoteCount(voteCount),
            bg    = TmdbBlue.copy(alpha = 0.15f)
        )

        // ── IMDb ───────────────────────────────────────────────────────────
        if (!imdbId.isNullOrBlank()) {
            RatingBadge(
                logo  = { ImdbLogo() },
                score = externalRatings?.imdbRating?.let { String.format("%.1f", it) } ?: "—",
                sub   = externalRatings?.imdbVotes?.let { formatRawVotes(it) }
                    ?: stringResource(R.string.see_on_imdb),
                bg    = ImdbYellow.copy(alpha = 0.12f),
                modifier = Modifier.clickable {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.imdb.com/title/$imdbId/"))
                    )
                }
            )
        }

        // ── Rotten Tomatoes ────────────────────────────────────────────────
        externalRatings?.rottenTomatoes?.let { rt ->
            RatingBadge(
                logo  = { RtLogo() },
                score = rt,
                sub   = "Tomatometer",
                bg    = RtRed.copy(alpha = 0.12f)
            )
        }

        // ── Metacritic ─────────────────────────────────────────────────────
        externalRatings?.metacritic?.let { m ->
            if (m > 0) RatingBadge(
                logo  = { MetaLogo() },
                score = "$m",
                sub   = "Metacritic",
                bg    = MetaGreen.copy(alpha = 0.12f)
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
    bg: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(BADGE_HEIGHT)
            .clip(RoundedCornerShape(BADGE_CORNER))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        logo()
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text       = score,
                fontSize   = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                lineHeight = 16.sp
            )
            Text(
                text       = sub,
                fontSize   = 9.sp,
                color      = Color.White.copy(alpha = 0.55f),
                lineHeight = 9.sp,
                maxLines   = 1
            )
        }
    }
}

// ── Stylised logos ────────────────────────────────────────────────────────────

@Composable private fun TmdbLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("TM", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TmdbBlue,  letterSpacing = (-0.5).sp)
        Text("db", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TmdbGreen, letterSpacing = (-0.5).sp)
    }
}

@Composable private fun ImdbLogo() {
    Box(
        Modifier.clip(RoundedCornerShape(3.dp)).background(ImdbYellow)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text("IMDb", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ImdbBg, letterSpacing = (-0.3).sp)
    }
}

@Composable private fun RtLogo() {
    Box(Modifier.clip(RoundedCornerShape(3.dp)).background(RtRed).padding(3.dp)) {
        Text("🍅", fontSize = 10.sp, lineHeight = 10.sp)
    }
}

@Composable private fun MetaLogo() {
    Box(
        Modifier.clip(RoundedCornerShape(3.dp)).background(MetaGreen)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text("M", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White, lineHeight = 10.sp)
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatVoteCount(n: Int) = when {
    n >= 1_000_000 -> String.format("%.1fM oy", n / 1_000_000.0)
    n >= 1_000     -> String.format("%.1fK oy", n / 1_000.0)
    else           -> "$n oy"
}

private fun formatRawVotes(raw: String): String {
    val n = raw.replace(",", "").toLongOrNull() ?: return raw
    return when {
        n >= 1_000_000 -> String.format("%.1fM oy", n / 1_000_000.0)
        n >= 1_000     -> String.format("%.1fK oy", n / 1_000.0)
        else           -> "$n oy"
    }
}
