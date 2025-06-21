package com.halil.ozel.moviedb.ui.home.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.TvResults;
import com.halil.ozel.moviedb.ui.home.adapters.TvSeriesAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

public class AllTvActivity extends Activity {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_TITLE = "title";

    @Inject
    TMDbAPI tmDbAPI;

    private final List<TvResults> tvList = new ArrayList<>();
    private TvSeriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_all_tv);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        RecyclerView rv = findViewById(R.id.rvAllTv);
        adapter = new TvSeriesAdapter(tvList, this);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(adapter);

        loadTv();
    }

    private void loadTv() {
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        Observable<? extends com.halil.ozel.moviedb.data.models.ResponseTvSeries> call;
        if ("top_rated".equals(category)) {
            call = tmDbAPI.getTvTopRated(TMDb_API_KEY, 1);
        } else if ("airing_today".equals(category)) {
            call = tmDbAPI.getTvAiringToday(TMDb_API_KEY, 1);
        } else if ("on_the_air".equals(category)) {
            call = tmDbAPI.getTvOnTheAir(TMDb_API_KEY, 1);
        } else {
            call = tmDbAPI.getTvPopular(TMDb_API_KEY, 1);
        }
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    tvList.addAll(response.getResults());
                    adapter.notifyDataSetChanged();
                }, e -> Timber.e(e, "Error fetching tv: %s", e.getMessage()));
    }
}
