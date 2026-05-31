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
import com.halil.ozel.moviedb.ui.detail.person.PersonDetailScreen
import com.halil.ozel.moviedb.ui.detail.tv.TvDetailScreen
import com.halil.ozel.moviedb.ui.favorites.FavoritesScreen
import com.halil.ozel.moviedb.ui.home.HomeScreen
import com.halil.ozel.moviedb.ui.list.GenreListScreen
import com.halil.ozel.moviedb.ui.list.MediaListScreen
import com.halil.ozel.moviedb.ui.list.SimilarListScreen
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
        Screen.MovieDetail.route,
        Screen.TvDetail.route,
        Screen.PersonDetail.route,
        Screen.MediaList.route,
        Screen.GenreList.route,
        Screen.SimilarList.route
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = detailRoutes.none { currentRoute?.startsWith(it.substringBefore("{")) == true }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Surface) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
                                selectedIconColor = Primary, selectedTextColor = Primary,
                                unselectedIconColor = OnSurface, unselectedTextColor = OnSurface,
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
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onSeeAllClick = { category ->
                        navController.navigate(Screen.MediaList.createRoute(category))
                    }
                )
            }
            composable("tv_home") {
                HomeScreen(
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onTvClick = { navController.navigate(Screen.TvDetail.createRoute(it)) },
                    onSeeAllClick = { category ->
                        navController.navigate(Screen.MediaList.createRoute(category))
                    },
                    showTv = true
                )
            }
            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(400))
                }
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                MovieDetailScreen(
                    movieId = movieId,
                    onBack = { navController.popBackStack() },
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onCastClick = { personId, name ->
                        navController.navigate(Screen.PersonDetail.createRoute(personId) + "?name=${name}")
                    },
                    onGenreClick = { genreId, genreName ->
                        navController.navigate(Screen.GenreList.createRoute(genreId, true, genreName))
                    },
                    onSeeAllSimilar = { id ->
                        navController.navigate(Screen.SimilarList.createRoute(id, true))
                    }
                )
            }
            composable(
                route = Screen.TvDetail.route,
                arguments = listOf(navArgument("tvId") { type = NavType.IntType }),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(400))
                }
            ) { backStackEntry ->
                val tvId = backStackEntry.arguments?.getInt("tvId") ?: return@composable
                TvDetailScreen(
                    tvId = tvId,
                    onBack = { navController.popBackStack() },
                    onTvClick = { navController.navigate(Screen.TvDetail.createRoute(it)) },
                    onCastClick = { personId, name ->
                        navController.navigate(Screen.PersonDetail.createRoute(personId) + "?name=${name}")
                    },
                    onGenreClick = { genreId, genreName ->
                        navController.navigate(Screen.GenreList.createRoute(genreId, false, genreName))
                    },
                    onSeeAllSimilar = { id ->
                        navController.navigate(Screen.SimilarList.createRoute(id, false))
                    }
                )
            }
            composable(
                route = Screen.PersonDetail.route + "?name={name}",
                arguments = listOf(
                    navArgument("personId") { type = NavType.IntType },
                    navArgument("name") { type = NavType.StringType; defaultValue = "Actor" }
                ),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(400))
                }
            ) { backStackEntry ->
                val personId = backStackEntry.arguments?.getInt("personId") ?: return@composable
                val name = backStackEntry.arguments?.getString("name") ?: "Actor"
                PersonDetailScreen(
                    personId = personId,
                    personName = name,
                    onBack = { navController.popBackStack() },
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onTvClick = { navController.navigate(Screen.TvDetail.createRoute(it)) }
                )
            }
            composable(
                route = Screen.MediaList.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType }),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(400))
                }
            ) {
                // category is read from SavedStateHandle inside MediaListViewModel
                MediaListScreen(
                    onBack = { navController.popBackStack() },
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onTvClick = { navController.navigate(Screen.TvDetail.createRoute(it)) }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onTvClick = { navController.navigate(Screen.TvDetail.createRoute(it)) },
                    onPersonClick = { personId, name ->
                        navController.navigate(Screen.PersonDetail.createRoute(personId) + "?name=${name}")
                    }
                )
            }
            composable(
                route = Screen.GenreList.route,
                arguments = listOf(
                    navArgument("genreId") { type = NavType.IntType },
                    navArgument("mediaType") { type = NavType.StringType },
                    navArgument("genreName") { type = NavType.StringType; defaultValue = "" }
                ),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(400))
                }
            ) {
                GenreListScreen(
                    onBack = { navController.popBackStack() },
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onTvClick = { navController.navigate(Screen.TvDetail.createRoute(it)) }
                )
            }
            composable(
                route = Screen.SimilarList.route,
                arguments = listOf(
                    navArgument("contentId") { type = NavType.IntType },
                    navArgument("mediaType") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(400))
                }
            ) {
                SimilarListScreen(
                    onBack = { navController.popBackStack() },
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onTvClick = { navController.navigate(Screen.TvDetail.createRoute(it)) }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                    onTvClick = { navController.navigate(Screen.TvDetail.createRoute(it)) }
                )
            }
        }
    }
}
