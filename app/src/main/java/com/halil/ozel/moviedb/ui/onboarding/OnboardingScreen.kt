package com.halil.ozel.moviedb.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halil.ozel.moviedb.ui.theme.*
import kotlinx.coroutines.launch

// ── Data model ────────────────────────────────────────────────────────────────

private data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val accentColor: Color,
    val features: List<Pair<String, String>>   // emoji → text
)

private val pages = listOf(
    OnboardingPage(
        emoji = "🎬",
        title = "MovieDB'ye\nHoş Geldiniz",
        description = "Sinema dünyasının tüm içeriklerine tek bir yerden ulaşın.",
        accentColor = Color(0xFFF5C518),
        features = listOf(
            "🔥" to "Güncel filmler ve diziler",
            "⭐" to "En yüksek puanlı içerikler",
            "📅" to "Yakında gelecekler"
        )
    ),
    OnboardingPage(
        emoji = "🔍",
        title = "Keşfet &\nAra",
        description = "Milyonlarca film, dizi ve oyuncu arasında anında arama yapın.",
        accentColor = Color(0xFF4FC3F7),
        features = listOf(
            "🎭" to "Oyuncu araması",
            "🎬" to "Film & dizi filtreleme",
            "📺" to "Streaming platformları"
        )
    ),
    OnboardingPage(
        emoji = "❤️",
        title = "Favorilerin\nHep Yanında",
        description = "Beğendiğiniz film ve dizileri kaydedin, istediğiniz an geri dönün.",
        accentColor = Color(0xFFEF5350),
        features = listOf(
            "🎬" to "Film favorileri",
            "📺" to "Dizi favorileri",
            "💾" to "Çevrimdışı erişim"
        )
    ),
    OnboardingPage(
        emoji = "🎭",
        title = "Her Detay\nElinizde",
        description = "Fragman izleyin, oyuncu filmografisini görün, nerede izleyeceğinizi öğrenin.",
        accentColor = Color(0xFF66BB6A),
        features = listOf(
            "▶️" to "YouTube fragmanı",
            "🏆" to "Oyuncu filmografisi",
            "📡" to "Netflix · Prime · Disney+"
        )
    )
)

// ── Main composable ───────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage
    val page = pages[currentPage]

    val bgColor by animateColorAsState(
        targetValue = page.accentColor.copy(alpha = 0.08f),
        animationSpec = tween(600),
        label = "bg"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Soft accent glow at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(bgColor, Color.Transparent)
                    )
                )
        )

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            PageContent(page = pages[index])
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isSelected = index == currentPage
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 28.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "dot_width"
                    )
                    val color by animateColorAsState(
                        targetValue = if (isSelected) page.accentColor else SurfaceVariant,
                        animationSpec = tween(300),
                        label = "dot_color"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (currentPage == pages.lastIndex) {
                // Last page → "Başla" button
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = page.accentColor,
                        contentColor = Background
                    )
                ) {
                    Text(
                        text = "Başla 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onComplete) {
                        Text(
                            text = "Atla",
                            color = OnSurface.copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentPage + 1)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = page.accentColor,
                            contentColor = Background
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "İleri →",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Single page ───────────────────────────────────────────────────────────────

@Composable
private fun PageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        // Emoji icon with glow circle
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            page.accentColor.copy(alpha = 0.25f),
                            page.accentColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = page.emoji, fontSize = 72.sp)
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.displaySmall,
            color = OnBackground,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurface,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Feature bullets
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            page.features.forEach { (emoji, text) ->
                FeatureRow(emoji = emoji, text = text, accentColor = page.accentColor)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FeatureRow(emoji: String, text: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.07f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = OnBackground,
            fontWeight = FontWeight.Medium
        )
    }
}
