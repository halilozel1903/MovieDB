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
import com.halil.ozel.moviedb.data.models.TvResults;
import com.halil.ozel.moviedb.data.models.ResponseTvSeries;
import com.halil.ozel.moviedb.ui.home.adapters.TvSeriesAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

public class TvSeriesFragment extends Fragment {

    @Inject
    TMDbAPI tmDbAPI;

    private RecyclerView rvTvPopular;
    private RecyclerView.Adapter tvPopularAdapter;
    private RecyclerView.LayoutManager tvPopularLayoutManager;
    private List<TvResults> tvPopularDataList;

    private RecyclerView rvTvTopRated;
    private RecyclerView.Adapter tvTopRatedAdapter;
    private RecyclerView.LayoutManager tvTopRatedLayoutManager;
    private List<TvResults> tvTopRatedDataList;

    private RecyclerView rvTvAiringToday;
    private RecyclerView.Adapter tvAiringTodayAdapter;
    private RecyclerView.LayoutManager tvAiringTodayLayoutManager;
    private List<TvResults> tvAiringTodayDataList;

    private RecyclerView rvTvOnTheAir;
    private RecyclerView.Adapter tvOnTheAirAdapter;
    private RecyclerView.LayoutManager tvOnTheAirLayoutManager;
    private List<TvResults> tvOnTheAirDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        App.instance().appComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_tv, container, false);

        tvPopularDataList = new ArrayList<>();
        tvPopularAdapter = new TvSeriesAdapter(tvPopularDataList, getContext());
        tvPopularLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvTvPopular = view.findViewById(R.id.rvTvPopular);
        rvTvPopular.setLayoutManager(tvPopularLayoutManager);
        rvTvPopular.setAdapter(tvPopularAdapter);
        rvTvPopular.setNestedScrollingEnabled(false);

        tvTopRatedDataList = new ArrayList<>();
        tvTopRatedAdapter = new TvSeriesAdapter(tvTopRatedDataList, getContext());
        tvTopRatedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvTvTopRated = view.findViewById(R.id.rvTvTopRated);
        rvTvTopRated.setLayoutManager(tvTopRatedLayoutManager);
        rvTvTopRated.setAdapter(tvTopRatedAdapter);
        rvTvTopRated.setNestedScrollingEnabled(false);

        tvAiringTodayDataList = new ArrayList<>();
        tvAiringTodayAdapter = new TvSeriesAdapter(tvAiringTodayDataList, getContext());
        tvAiringTodayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvTvAiringToday = view.findViewById(R.id.rvTvAiringToday);
        rvTvAiringToday.setLayoutManager(tvAiringTodayLayoutManager);
        rvTvAiringToday.setAdapter(tvAiringTodayAdapter);
        rvTvAiringToday.setNestedScrollingEnabled(false);

        tvOnTheAirDataList = new ArrayList<>();
        tvOnTheAirAdapter = new TvSeriesAdapter(tvOnTheAirDataList, getContext());
        tvOnTheAirLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvTvOnTheAir = view.findViewById(R.id.rvTvOnTheAir);
        rvTvOnTheAir.setLayoutManager(tvOnTheAirLayoutManager);
        rvTvOnTheAir.setAdapter(tvOnTheAirAdapter);
        rvTvOnTheAir.setNestedScrollingEnabled(false);

        view.findViewById(R.id.tvTvPopularSeeAll).setOnClickListener(v -> startAllTv("popular", "TV Popular"));
        view.findViewById(R.id.tvTvTopRatedSeeAll).setOnClickListener(v -> startAllTv("top_rated", "Top Rated"));
        view.findViewById(R.id.tvTvAiringTodaySeeAll).setOnClickListener(v -> startAllTv("airing_today", "Airing Today"));
        view.findViewById(R.id.tvTvOnTheAirSeeAll).setOnClickListener(v -> startAllTv("on_the_air", "On The Air"));

        getPopularTv();
        getTopRatedTv();
        getAiringTodayTv();
        getOnTheAirTv();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getPopularTv() {
        tmDbAPI.getTvPopular(TMDb_API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    tvPopularDataList.addAll(response.getResults());
                    tvPopularAdapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching popular tv series: %s", e.getMessage()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getTopRatedTv() {
        tmDbAPI.getTvTopRated(TMDb_API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    tvTopRatedDataList.addAll(response.getResults());
                    tvTopRatedAdapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching top rated tv series: %s", e.getMessage()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAiringTodayTv() {
        tmDbAPI.getTvAiringToday(TMDb_API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    tvAiringTodayDataList.addAll(response.getResults());
                    tvAiringTodayAdapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching airing today tv series: %s", e.getMessage()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getOnTheAirTv() {
        tmDbAPI.getTvOnTheAir(TMDb_API_KEY, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    tvOnTheAirDataList.addAll(response.getResults());
                    tvOnTheAirAdapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching on the air tv series: %s", e.getMessage()));
    }

    @Override
    public void onResume() {
        super.onResume();
        tvPopularAdapter.notifyDataSetChanged();
        tvTopRatedAdapter.notifyDataSetChanged();
        tvAiringTodayAdapter.notifyDataSetChanged();
        tvOnTheAirAdapter.notifyDataSetChanged();
    }

    private void startAllTv(String category, String title) {
        android.content.Intent intent = new android.content.Intent(getContext(), com.halil.ozel.moviedb.ui.home.activity.AllTvActivity.class);
        intent.putExtra(com.halil.ozel.moviedb.ui.home.activity.AllTvActivity.EXTRA_CATEGORY, category);
        intent.putExtra(com.halil.ozel.moviedb.ui.home.activity.AllTvActivity.EXTRA_TITLE, title);
        startActivity(intent);
    }
}
