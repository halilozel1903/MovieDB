package com.halil.ozel.moviedb.ui.home.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import com.google.android.material.appbar.MaterialToolbar;
import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.ui.home.adapters.MovieAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

public class AllMoviesActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_TITLE = "title";

    @Inject
    TMDbAPI tmDbAPI;

    private final List<Results> movieList = new ArrayList<>();
    private MovieAdapter adapter;
    private int currentPage = 1;
    private int totalPages = Integer.MAX_VALUE;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_all_movies);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        RecyclerView rv = findViewById(R.id.rvAllMovies);
        adapter = new MovieAdapter(movieList, this);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisible = manager.findLastVisibleItemPosition();
                if (!isLoading && currentPage <= totalPages && lastVisible >= movieList.size() - 4) {
                    loadMovies();
                }
            }
        });

        loadMovies();
    }

    private void loadMovies() {
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        Observable<? extends com.halil.ozel.moviedb.data.models.ResponseNowPlaying> call;
        if ("now_playing".equals(category)) {
            call = tmDbAPI.getNowPlaying(TMDb_API_KEY, currentPage);
        } else if ("top_rated".equals(category)) {
            call = tmDbAPI.getTopRatedMovie(TMDb_API_KEY, currentPage);
        } else if ("upcoming".equals(category)) {
            call = tmDbAPI.getUpcomingMovie(TMDb_API_KEY, currentPage);
        } else {
            call = tmDbAPI.getPopularMovie(TMDb_API_KEY, currentPage);
        }
        isLoading = true;
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    totalPages = response.getTotal_pages();
                    movieList.addAll(response.getResults());
                    adapter.notifyDataSetChanged();
                    currentPage++;
                    isLoading = false;
                }, e -> {
                    Timber.e(e, "Error fetching movies: %s", e.getMessage());
                    isLoading = false;
                });
    }
}
