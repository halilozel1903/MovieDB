package com.halil.ozel.moviedb.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.FavoritesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : FavoritesRepository {

    private val favoritesKey = stringPreferencesKey("favorites_list")
    private val tvFavoritesKey = stringPreferencesKey("tv_favorites_list")

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val movieType = Types.newParameterizedType(List::class.java, FavoriteMovieJson::class.java)
    private val movieAdapter = moshi.adapter<List<FavoriteMovieJson>>(movieType)

    private val tvType = Types.newParameterizedType(List::class.java, FavoriteTvJson::class.java)
    private val tvAdapter = moshi.adapter<List<FavoriteTvJson>>(tvType)

    data class FavoriteMovieJson(
        val id: Int = 0, val title: String = "", val overview: String = "",
        val posterPath: String? = null, val backdropPath: String? = null,
        val releaseDate: String = "", val popularity: Double = 0.0,
        val voteAverage: Double = 0.0, val voteCount: Int = 0
    )

    data class FavoriteTvJson(
        val id: Int = 0, val name: String = "", val overview: String = "",
        val posterPath: String? = null, val backdropPath: String? = null,
        val firstAirDate: String = "", val popularity: Double = 0.0,
        val voteAverage: Double = 0.0, val voteCount: Int = 0
    )

    private fun Movie.toJson() = FavoriteMovieJson(
        id = id, title = title, overview = overview, posterPath = posterPath,
        backdropPath = backdropPath, releaseDate = releaseDate,
        popularity = popularity, voteAverage = voteAverage, voteCount = voteCount
    )

    private fun FavoriteMovieJson.toDomain() = Movie(
        id = id, title = title, overview = overview, posterPath = posterPath,
        backdropPath = backdropPath, releaseDate = releaseDate,
        popularity = popularity, voteAverage = voteAverage, voteCount = voteCount
    )

    private fun TvSeries.toJson() = FavoriteTvJson(
        id = id, name = name, overview = overview, posterPath = posterPath,
        backdropPath = backdropPath, firstAirDate = firstAirDate,
        popularity = popularity, voteAverage = voteAverage, voteCount = voteCount
    )

    private fun FavoriteTvJson.toDomain() = TvSeries(
        id = id, name = name, overview = overview, posterPath = posterPath,
        backdropPath = backdropPath, firstAirDate = firstAirDate,
        popularity = popularity, voteAverage = voteAverage, voteCount = voteCount
    )

    // ── Movies ──────────────────────────────────────────────────────────────

    private suspend fun readAllMovies(): List<FavoriteMovieJson> {
        val json = dataStore.data.first()[favoritesKey] ?: return emptyList()
        return try { movieAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    private suspend fun writeAllMovies(list: List<FavoriteMovieJson>) {
        dataStore.edit { it[favoritesKey] = movieAdapter.toJson(list) }
    }

    override fun getFavorites(): Flow<List<Movie>> = dataStore.data.map { prefs ->
        val json = prefs[favoritesKey] ?: return@map emptyList()
        try { movieAdapter.fromJson(json)?.map { it.toDomain() } ?: emptyList() }
        catch (e: Exception) { emptyList() }
    }

    override suspend fun addFavorite(movie: Movie) {
        val list = readAllMovies().toMutableList()
        if (list.none { it.id == movie.id }) { list.add(movie.toJson()); writeAllMovies(list) }
    }

    override suspend fun removeFavorite(movieId: Int) =
        writeAllMovies(readAllMovies().filter { it.id != movieId })

    override suspend fun isFavorite(movieId: Int): Boolean = readAllMovies().any { it.id == movieId }

    override fun isFavoriteFlow(movieId: Int): Flow<Boolean> = dataStore.data.map { prefs ->
        val json = prefs[favoritesKey] ?: return@map false
        try { movieAdapter.fromJson(json)?.any { it.id == movieId } ?: false }
        catch (_: Exception) { false }
    }

    // ── TV ──────────────────────────────────────────────────────────────────

    private suspend fun readAllTv(): List<FavoriteTvJson> {
        val json = dataStore.data.first()[tvFavoritesKey] ?: return emptyList()
        return try { tvAdapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    private suspend fun writeAllTv(list: List<FavoriteTvJson>) {
        dataStore.edit { it[tvFavoritesKey] = tvAdapter.toJson(list) }
    }

    override fun getTvFavorites(): Flow<List<TvSeries>> = dataStore.data.map { prefs ->
        val json = prefs[tvFavoritesKey] ?: return@map emptyList()
        try { tvAdapter.fromJson(json)?.map { it.toDomain() } ?: emptyList() }
        catch (e: Exception) { emptyList() }
    }

    override suspend fun addTvFavorite(tvSeries: TvSeries) {
        val list = readAllTv().toMutableList()
        if (list.none { it.id == tvSeries.id }) { list.add(tvSeries.toJson()); writeAllTv(list) }
    }

    override suspend fun removeTvFavorite(tvId: Int) =
        writeAllTv(readAllTv().filter { it.id != tvId })

    override suspend fun isTvFavorite(tvId: Int): Boolean = readAllTv().any { it.id == tvId }

    override fun isTvFavoriteFlow(tvId: Int): Flow<Boolean> = dataStore.data.map { prefs ->
        val json = prefs[tvFavoritesKey] ?: return@map false
        try { tvAdapter.fromJson(json)?.any { it.id == tvId } ?: false }
        catch (_: Exception) { false }
    }
}
