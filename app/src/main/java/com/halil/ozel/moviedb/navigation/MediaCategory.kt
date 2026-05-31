package com.halil.ozel.moviedb.navigation

enum class MediaCategory(val displayName: String, val isMovie: Boolean) {
    MOVIE_NOW_PLAYING("Now Playing", true),
    MOVIE_POPULAR("Popular", true),
    MOVIE_TOP_RATED("Top Rated", true),
    MOVIE_UPCOMING("Upcoming", true),
    TV_POPULAR("Popular TV", false),
    TV_TOP_RATED("Top Rated TV", false),
    TV_AIRING_TODAY("Airing Today", false),
    TV_ON_THE_AIR("On The Air", false)
}
