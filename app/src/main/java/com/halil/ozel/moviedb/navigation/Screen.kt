package com.halil.ozel.moviedb.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
    object TvDetail : Screen("tv_detail/{tvId}") {
        fun createRoute(tvId: Int) = "tv_detail/$tvId"
    }
    object PersonDetail : Screen("person_detail/{personId}") {
        fun createRoute(personId: Int) = "person_detail/$personId"
    }
    object Favorites : Screen("favorites")
    object Search : Screen("search")
    object MediaList : Screen("media_list/{category}") {
        fun createRoute(category: MediaCategory) = "media_list/${category.name}"
    }
    object GenreList : Screen("genre_list/{genreId}/{mediaType}/{genreName}") {
        fun createRoute(genreId: Int, isMovie: Boolean, genreName: String) =
            "genre_list/$genreId/${if (isMovie) "movie" else "tv"}/${android.net.Uri.encode(genreName)}"
    }
}
