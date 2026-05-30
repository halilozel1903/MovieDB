package com.halil.ozel.moviedb.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.halil.ozel.moviedb.domain.model.Movie
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

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, FavoriteMovieJson::class.java)
    private val adapter = moshi.adapter<List<FavoriteMovieJson>>(type)

    data class FavoriteMovieJson(
        val id: Int = 0,
        val title: String = "",
        val overview: String = "",
        val posterPath: String? = null,
        val backdropPath: String? = null,
        val releaseDate: String = "",
        val popularity: Double = 0.0,
        val voteAverage: Double = 0.0,
        val voteCount: Int = 0
    )

    private fun Movie.toJson() = FavoriteMovieJson(
        id = id, title = title, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        releaseDate = releaseDate, popularity = popularity,
        voteAverage = voteAverage, voteCount = voteCount
    )

    private fun FavoriteMovieJson.toDomain() = Movie(
        id = id, title = title, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        releaseDate = releaseDate, popularity = popularity,
        voteAverage = voteAverage, voteCount = voteCount
    )

    private suspend fun readAll(): List<FavoriteMovieJson> {
        val json = dataStore.data.first()[favoritesKey] ?: return emptyList()
        return try { adapter.fromJson(json) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    private suspend fun writeAll(list: List<FavoriteMovieJson>) {
        dataStore.edit { prefs ->
            prefs[favoritesKey] = adapter.toJson(list)
        }
    }

    override fun getFavorites(): Flow<List<Movie>> =
        dataStore.data.map { prefs ->
            val json = prefs[favoritesKey] ?: return@map emptyList()
            try { adapter.fromJson(json)?.map { it.toDomain() } ?: emptyList() }
            catch (e: Exception) { emptyList() }
        }

    override suspend fun addFavorite(movie: Movie) {
        val list = readAll().toMutableList()
        if (list.none { it.id == movie.id }) {
            list.add(movie.toJson())
            writeAll(list)
        }
    }

    override suspend fun removeFavorite(movieId: Int) {
        val list = readAll().filter { it.id != movieId }
        writeAll(list)
    }

    override suspend fun isFavorite(movieId: Int): Boolean =
        readAll().any { it.id == movieId }

    override fun isFavoriteFlow(movieId: Int): Flow<Boolean> =
        dataStore.data.map { prefs ->
            val json = prefs[favoritesKey] ?: return@map false
            try { adapter.fromJson(json)?.any { it.id == movieId } ?: false }
            catch (e: Exception) { false }
        }
}
