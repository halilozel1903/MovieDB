package com.halil.ozel.moviedb.ui.home.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class MoviesFragment extends Fragment {

    @Inject
    TMDbAPI tmDbAPI;

    private RecyclerView rvPopularMovie;
    private RecyclerView.Adapter popularMovieAdapter;
    private RecyclerView.LayoutManager popularMovieLayoutManager;
    private List<Results> popularMovieDataList;

    private RecyclerView rvNowPlaying;
    private RecyclerView.Adapter nowPlayingMovieAdapter;
    private RecyclerView.LayoutManager nowPlayingLayoutManager;
    private List<Results> nowPlayingDataList;

    private RecyclerView rvTopRated;
    private RecyclerView.Adapter topRatedMovieAdapter;
    private RecyclerView.LayoutManager topRatedLayoutManager;
    private List<Results> topRatedDataList;

    private RecyclerView rvUpcoming;
    private RecyclerView.Adapter upcomingMovieAdapter;
    private RecyclerView.LayoutManager upcomingLayoutManager;
    private List<Results> upcomingDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        App.instance().appComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        popularMovieDataList = new ArrayList<>();
        popularMovieAdapter = new MovieAdapter(popularMovieDataList, getContext());
        popularMovieLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvPopularMovie = view.findViewById(R.id.rvPopularMovie);
        rvPopularMovie.setLayoutManager(popularMovieLayoutManager);
        rvPopularMovie.setAdapter(popularMovieAdapter);
        rvPopularMovie.setNestedScrollingEnabled(false);

        nowPlayingDataList = new ArrayList<>();
        nowPlayingMovieAdapter = new MovieAdapter(nowPlayingDataList, getContext());
        nowPlayingLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvNowPlaying = view.findViewById(R.id.rvNowPlaying);
        rvNowPlaying.setLayoutManager(nowPlayingLayoutManager);
        rvNowPlaying.setAdapter(nowPlayingMovieAdapter);
        rvNowPlaying.setNestedScrollingEnabled(false);

        topRatedDataList = new ArrayList<>();
        topRatedMovieAdapter = new MovieAdapter(topRatedDataList, getContext());
        topRatedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvTopRated = view.findViewById(R.id.rvTopRated);
        rvTopRated.setLayoutManager(topRatedLayoutManager);
        rvTopRated.setAdapter(topRatedMovieAdapter);
        rvTopRated.setNestedScrollingEnabled(false);

        upcomingDataList = new ArrayList<>();
        upcomingMovieAdapter = new MovieAdapter(upcomingDataList, getContext());
        upcomingLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        rvUpcoming.setLayoutManager(upcomingLayoutManager);
        rvUpcoming.setAdapter(upcomingMovieAdapter);
        rvUpcoming.setNestedScrollingEnabled(false);

        getPopularMovies();
        getNowPlaying();
        getTopRatedMovies();
        getUpcomingMovies();

        return view;
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
