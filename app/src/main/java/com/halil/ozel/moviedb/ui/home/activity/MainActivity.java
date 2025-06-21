package com.halil.ozel.moviedb.ui.home.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.ui.home.adapters.MovieAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;


public class MainActivity extends AppCompatActivity {

    @Inject
    TMDbAPI tmDbAPI;

    public RecyclerView rvPopularMovie;
    public RecyclerView.Adapter popularMovieAdapter;
    public RecyclerView.LayoutManager popularMovieLayoutManager;
    public List<Results> popularMovieDataList;

    public RecyclerView rvNowPlaying;
    public RecyclerView.Adapter nowPlayingMovieAdapter;
    public RecyclerView.LayoutManager nowPlayingLayoutManager;
    public List<Results> nowPlayingDataList;

    public RecyclerView rvTopRated;
    public RecyclerView.Adapter topRatedMovieAdapter;
    public RecyclerView.LayoutManager topRatedLayoutManager;
    public List<Results> topRatedDataList;

    public RecyclerView rvUpcoming;
    public RecyclerView.Adapter upcomingMovieAdapter;
    public RecyclerView.LayoutManager upcomingLayoutManager;
    public List<Results> upcomingDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_main);

        popularMovieDataList = new ArrayList<>();
        popularMovieAdapter = new MovieAdapter(popularMovieDataList, this);
        popularMovieLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvPopularMovie = findViewById(R.id.rvPopularMovie);
        rvPopularMovie.setHasFixedSize(true);
        rvPopularMovie.setLayoutManager(popularMovieLayoutManager);
        rvPopularMovie.setAdapter(popularMovieAdapter);

        nowPlayingDataList = new ArrayList<>();
        nowPlayingMovieAdapter = new MovieAdapter(nowPlayingDataList, this);
        nowPlayingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvNowPlaying = findViewById(R.id.rvNowPlaying);
        rvNowPlaying.setHasFixedSize(true);
        rvNowPlaying.setLayoutManager(nowPlayingLayoutManager);
        rvNowPlaying.setAdapter(nowPlayingMovieAdapter);

        topRatedDataList = new ArrayList<>();
        topRatedMovieAdapter = new MovieAdapter(topRatedDataList, this);
        topRatedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvTopRated = findViewById(R.id.rvTopRated);
        rvTopRated.setHasFixedSize(true);
        rvTopRated.setLayoutManager(topRatedLayoutManager);
        rvTopRated.setAdapter(topRatedMovieAdapter);

        upcomingDataList = new ArrayList<>();
        upcomingMovieAdapter = new MovieAdapter(upcomingDataList, this);
        upcomingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvUpcoming = findViewById(R.id.rvUpcoming);
        rvUpcoming.setHasFixedSize(true);
        rvUpcoming.setLayoutManager(upcomingLayoutManager);
        rvUpcoming.setAdapter(upcomingMovieAdapter);

        getPopularMovies();
        getNowPlaying();
        getTopRatedMovies();
        getUpcomingMovies();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void getNowPlaying() {
        tmDbAPI.getNowPlaying(TMDb_API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    nowPlayingDataList.addAll(response.getResults());
                    nowPlayingMovieAdapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching now playing movies: %s", e.getMessage()));     
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getPopularMovies() {
        tmDbAPI.getPopularMovie(TMDb_API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    popularMovieDataList.addAll(response.getResults());
                    popularMovieAdapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching popular movies: %s", e.getMessage()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getTopRatedMovies() {
        tmDbAPI.getTopRatedMovie(TMDb_API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    topRatedDataList.addAll(response.getResults());
                    topRatedMovieAdapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching top rated movies: %s", e.getMessage()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getUpcomingMovies() {
        tmDbAPI.getUpcomingMovie(TMDb_API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    upcomingDataList.addAll(response.getResults());
                    upcomingMovieAdapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching upcoming movies: %s", e.getMessage()));
    }
}