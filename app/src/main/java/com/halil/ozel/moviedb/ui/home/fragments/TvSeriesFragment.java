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

        getPopularTv();
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
}
