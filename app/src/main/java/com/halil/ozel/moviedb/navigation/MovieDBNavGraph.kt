package com.halil.ozel.moviedb.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.halil.ozel.moviedb.ui.detail.movie.MovieDetailScreen
import com.halil.ozel.moviedb.ui.detail.tv.TvDetailScreen
import com.halil.ozel.moviedb.ui.favorites.FavoritesScreen
import com.halil.ozel.moviedb.ui.home.HomeScreen
import com.halil.ozel.moviedb.ui.search.SearchScreen
import com.halil.ozel.moviedb.ui.theme.Background
import com.halil.ozel.moviedb.ui.theme.OnSurface
import com.halil.ozel.moviedb.ui.theme.Primary
import com.halil.ozel.moviedb.ui.theme.Surface

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun MovieDBNavGraph() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("Movies", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("TV", "tv_home", Icons.Filled.Tv, Icons.Outlined.Tv),
        BottomNavItem("Search", Screen.Search.route, Icons.Filled.Search, Icons.Outlined.Search),
        BottomNavItem("Favorites", Screen.Favorites.route, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    )

    val detailRoutes = setOf(
        "movie_detail/{movieId}",
        "tv_detail/{tvId}"
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route !in detailRoutes

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Surface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                unselectedIconColor = OnSurface,
                                unselectedTextColor = OnSurface,
                                indicatorColor = Surface
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    }
                )
            }
            composable("tv_home") {
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    showTv = true
                )
            }
            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )
                }
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                MovieDetailScreen(
                    movieId = movieId,
                    onBack = { navController.popBackStack() },
                    onMovieClick = { id ->
                        navController.navigate(Screen.MovieDetail.createRoute(id))
                    }
                )
            }
            composable(
                route = Screen.TvDetail.route,
                arguments = listOf(navArgument("tvId") { type = NavType.IntType }),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )
                }
            ) { backStackEntry ->
                val tvId = backStackEntry.arguments?.getInt("tvId") ?: return@composable
                TvDetailScreen(
                    tvId = tvId,
                    onBack = { navController.popBackStack() },
                    onTvClick = { id ->
                        navController.navigate(Screen.TvDetail.createRoute(id))
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onTvClick = { tvId ->
                        navController.navigate(Screen.TvDetail.createRoute(tvId))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    }
                )
            }
        }
    }
}
