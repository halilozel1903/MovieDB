package com.halil.ozel.moviedb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.halil.ozel.moviedb.navigation.MovieDBNavGraph
import com.halil.ozel.moviedb.ui.main.MainViewModel
import com.halil.ozel.moviedb.ui.onboarding.OnboardingScreen
import com.halil.ozel.moviedb.ui.theme.Background
import com.halil.ozel.moviedb.ui.theme.MovieDBTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep the system splash screen visible until DataStore finishes loading
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isOnboardingComplete.value == null
        }

        setContent {
            MovieDBTheme {
                val isOnboardingComplete by mainViewModel.isOnboardingComplete.collectAsState()

                Crossfade(
                    targetState = isOnboardingComplete,
                    animationSpec = tween(500),
                    label = "root_crossfade"
                ) { complete ->
                    when (complete) {
                        null -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Background)
                            )
                        }
                        false -> {
                            OnboardingScreen(
                                onComplete = { mainViewModel.completeOnboarding() }
                            )
                        }
                        true -> {
                            MovieDBNavGraph()
                        }
                    }
                }
            }
        }
    }
}
