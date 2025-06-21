package com.halil.ozel.moviedb.ui.detail;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Cast;
import com.halil.ozel.moviedb.ui.detail.adapters.MovieCastAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

public class AllCastActivity extends Activity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_IS_TV = "is_tv";
    public static final String EXTRA_TITLE = "title";

    @Inject
    TMDbAPI tmDbAPI;

    private final List<Cast> castList = new ArrayList<>();
    private MovieCastAdapter adapter;
    private int contentId;
    private boolean isTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_all_cast);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        RecyclerView rv = findViewById(R.id.rvAllCast);
        adapter = new MovieCastAdapter(castList, this);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

        contentId = getIntent().getIntExtra(EXTRA_ID, 0);
        isTv = getIntent().getBooleanExtra(EXTRA_IS_TV, false);

        loadCast();
    }

    private void loadCast() {
        if (isTv) {
            tmDbAPI.getTvCastDetail(contentId, TMDb_API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        castList.addAll(response.getCast());
                        adapter.notifyDataSetChanged();
                    }, e -> Timber.e(e, "Error fetching tv cast: %s", e.getMessage()));
        } else {
            tmDbAPI.getCreditDetail(contentId, TMDb_API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        castList.addAll(response.getCast());
                        adapter.notifyDataSetChanged();
                    }, e -> Timber.e(e, "Error fetching movie cast: %s", e.getMessage()));
        }
    }
}

