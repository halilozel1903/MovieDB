package com.halil.ozel.moviedb.ui.detail;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_500;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.PersonDetail;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.data.models.TvResults;
import com.halil.ozel.moviedb.data.models.ResponsePersonMovieCredits;
import com.halil.ozel.moviedb.data.models.ResponsePersonTvCredits;
import com.halil.ozel.moviedb.ui.home.adapters.MovieAdapter;
import com.halil.ozel.moviedb.ui.home.adapters.TvSeriesAdapter;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class CastDetailActivity extends Activity {

    @Inject
    TMDbAPI tmDbAPI;

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvBirthday;
    private TextView tvBiography;
    private TextView tvMoviesTitle;
    private TextView tvTvTitle;
    private RecyclerView rvMovieCredits;
    private RecyclerView rvTvCredits;
    private MovieAdapter movieAdapter;
    private TvSeriesAdapter tvAdapter;
    private final java.util.List<Results> movieList = new java.util.ArrayList<>();
    private final java.util.List<TvResults> tvList = new java.util.ArrayList<>();

    private int personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_cast_detail);

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvBiography = findViewById(R.id.tvBiography);
        tvMoviesTitle = findViewById(R.id.tvMoviesTitle);
        tvTvTitle = findViewById(R.id.tvTvTitle);
        rvMovieCredits = findViewById(R.id.rvMovieCredits);
        rvTvCredits = findViewById(R.id.rvTvCredits);

        tvMoviesTitle.setVisibility(android.view.View.GONE);
        tvTvTitle.setVisibility(android.view.View.GONE);

        rvMovieCredits.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        movieAdapter = new MovieAdapter(movieList, this);
        rvMovieCredits.setAdapter(movieAdapter);

        rvTvCredits.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tvAdapter = new TvSeriesAdapter(tvList, this);
        rvTvCredits.setAdapter(tvAdapter);

        personId = getIntent().getIntExtra("person_id", 0);
        loadPersonDetail();
        loadMovieCredits();
        loadTvCredits();
    }

    private void loadPersonDetail() {
        tmDbAPI.getPersonDetail(personId, TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindPerson,
                        e -> {
                            Timber.e(e, "Error fetching person detail: %s", e.getMessage());
                            Toast.makeText(this, R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                        });
    }

    private void loadMovieCredits() {
        tmDbAPI.getPersonMovieCredits(personId, TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    movieList.addAll(response.getCast());
                    movieAdapter.notifyDataSetChanged();
                    tvMoviesTitle.setVisibility(movieList.isEmpty() ? android.view.View.GONE : android.view.View.VISIBLE);
                }, e -> Timber.e(e, "Error fetching movie credits: %s", e.getMessage()));
    }

    private void loadTvCredits() {
        tmDbAPI.getPersonTvCredits(personId, TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    tvList.addAll(response.getCast());
                    tvAdapter.notifyDataSetChanged();
                    tvTvTitle.setVisibility(tvList.isEmpty() ? android.view.View.GONE : android.view.View.VISIBLE);
                }, e -> Timber.e(e, "Error fetching tv credits: %s", e.getMessage()));
    }

    private void bindPerson(PersonDetail detail) {
        tvName.setText(detail.getName());
        tvBirthday.setText(getString(R.string.birthday_format, detail.getBirthday()));
        tvBiography.setText(detail.getBiography());
        if (detail.getProfile_path() != null) {
            Picasso.get().load(IMAGE_BASE_URL_500 + detail.getProfile_path()).into(ivProfile);
        }
    }
}
