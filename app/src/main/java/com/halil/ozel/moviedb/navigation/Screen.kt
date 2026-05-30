package com.halil.ozel.moviedb.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
    object TvDetail : Screen("tv_detail/{tvId}") {
        fun createRoute(tvId: Int) = "tv_detail/$tvId"
    }
    object Favorites : Screen("favorites")
    object Search : Screen("search")
}
