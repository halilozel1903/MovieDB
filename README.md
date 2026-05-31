# TV Heaven

A modern Android app for browsing movies, TV shows and actors — powered by **TMDb** and **OMDb** APIs. Built entirely with **Kotlin** and **Jetpack Compose**.

## Screenshots

### Home & Discovery

<p align="center">
  <img src="screenshots/home_movies.png" width="230" alt="Home Movies" />
  <img src="screenshots/home_tv.png" width="230" alt="Home TV" />
  <img src="screenshots/onboarding.png" width="230" alt="Onboarding" />
</p>

### Search — Top 10 & Filters

<p align="center">
  <img src="screenshots/search_top10.png" width="230" alt="Search Top 10" />
  <img src="screenshots/search_movies.png" width="230" alt="Search Movies" />
  <img src="screenshots/search_tv.png" width="230" alt="Search TV" />
</p>

<p align="center">
  <img src="screenshots/search_actors.png" width="230" alt="Search Actors" />
</p>

### Movie & TV Detail

<p align="center">
  <img src="screenshots/movie_detail.png" width="230" alt="Movie Detail" />
  <img src="screenshots/movie_detail_scroll.png" width="230" alt="Movie Detail Scrolled" />
  <img src="screenshots/tv_detail.png" width="230" alt="TV Detail" />
</p>

### Seasons, Person & Favorites

<p align="center">
  <img src="screenshots/tv_seasons.png" width="230" alt="TV Seasons" />
  <img src="screenshots/person_detail.png" width="230" alt="Person Detail" />
  <img src="screenshots/favorites_movies.png" width="230" alt="Favorites Movies" />
</p>

<p align="center">
  <img src="screenshots/favorites_tv.png" width="230" alt="Favorites TV" />
</p>

## Features

- **Home** — Auto-scrolling hero banner, horizontal movie rails (Now Playing, Popular, Top Rated, Upcoming)
- **TV** — Popular, Top Rated, Airing Today and On The Air rails with full-bleed poster cards
- **Search** — Netflix-style Top 10 weekly trending movies & TV shows with large outlined rank numbers, trending people pills, real-time search with category filters (Movies / TV / Actors)
- **Movie Detail** — Backdrop, poster, genre chips (FlowRow), trailer playback (YouTube), multi-source ratings (TMDb + IMDb + Rotten Tomatoes + Metacritic via OMDb), watch providers, cast carousel, paged recommendations with "See All" grid
- **TV Detail** — Same richness as movies plus expandable seasons & episodes with thumbnails, episode ratings and runtime
- **Person Detail** — Biography, filmography grid with movie/TV type badges, tap to navigate
- **Favorites** — Separate tabs for movies and TV shows, persisted via DataStore
- **Onboarding** — Multi-page pager with animations, shown only on first launch
- **Splash Screen** — Android 12+ SplashScreen API with app icon, stays until DataStore loads
- **Localization** — Full Turkish and English support via `stringResource()`, auto-detects device language

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| DI | Hilt |
| Networking | Retrofit + Moshi + OkHttp |
| Image Loading | Coil |
| Navigation | Jetpack Navigation Compose |
| Local Storage | DataStore Preferences |
| Splash | AndroidX SplashScreen |
| Logging | Timber |

## API Integrations

| API | Purpose |
|-----|---------|
| [TMDb](https://www.themoviedb.org/) | Movie/TV details, cast, recommendations, trending, search, genres, watch providers, external IDs, season & episode data |
| [OMDb](https://www.omdbapi.com/) | IMDb rating & vote count, Rotten Tomatoes score, Metacritic score |

## Architecture

```
com.halil.ozel.moviedb
├── data
│   ├── local          → DataStore (favorites, onboarding)
│   ├── remote
│   │   ├── api        → TMDbApiService, OmdbApiService, ApiConstants
│   │   └── dto        → Data Transfer Objects with toDomain() mappers
│   └── repository     → Repository implementations
├── di                 → Hilt modules (Network, Repository, DataStore)
├── domain
│   ├── model          → Domain models (Movie, TvSeries, Episode, ExternalRatings, etc.)
│   ├── repository     → Repository interfaces
│   └── usecase        → Use cases
├── navigation         → Screen routes, MediaCategory, NavGraph
└── ui
    ├── components     → MovieCard, TvCard, RatingBadges, SectionHeader, etc.
    ├── detail         → Movie / TV / Person detail screens + ViewModels
    ├── favorites      → Favorites screen + ViewModel
    ├── home           → Home screen + ViewModel
    ├── list           → MediaList, GenreList, SimilarList screens + ViewModels
    ├── onboarding     → Onboarding pager
    ├── search         → Search screen + ViewModel (trending + search)
    └── theme          → Colors, Typography, Theme
```

## Screens

| Screen | Description |
|--------|-------------|
| Home (Movies) | Hero banner with auto-scroll, Now Playing / Popular / Top Rated / Upcoming rails |
| Home (TV) | Popular / Top Rated / Airing Today / On The Air TV rails |
| Search | Top 10 Movies & TV (Netflix-style ranked cards), trending people pills, multi-category search results |
| Movie Detail | Backdrop + poster, rating badges (TMDb/IMDb/RT/Metacritic), genre chips, trailer, watch providers, cast, recommendations |
| TV Detail | Same as movie + seasons & episodes (expandable, with thumbnails and per-episode ratings) |
| Person Detail | Photo, biography, filmography grid with movie/TV badges |
| Favorites | Tabbed grid (Movies / TV), heart toggle, persistent storage |
| Genre List | Paged grid filtered by genre |
| Similar List | Paged grid of recommended content |
| Onboarding | 4-page intro with animations |

## Build & Run

1. Clone the repository
2. Open in Android Studio (Hedgehog or newer)
3. Sync Gradle — all dependencies will download automatically
4. Run on a device or emulator (minSdk 26)

## Requirements

- Android Studio Hedgehog+
- JDK 17
- minSdk 26 / targetSdk 36

## License

```
MIT License

Copyright (c) 2026 Halil OZEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
