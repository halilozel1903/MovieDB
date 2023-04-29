package com.halil.ozel.moviedb.ui.detail;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_1280;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_500;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Cast;
import com.halil.ozel.moviedb.data.models.Genres;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.ui.detail.adapters.MovieCastAdapter;
import com.halil.ozel.moviedb.ui.home.adapters.MovieAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.blogc.android.views.ExpandableTextView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MovieDetailActivity extends Activity {

    String title;
    int id;
    ImageView ivHorizontalPoster, ivVerticalPoster;
    TextView tvTitle, tvGenres, tvPopularity, tvReleaseDate;
    ExpandableTextView etvOverview;
    Button btnToggle;

    @Inject
    TMDbAPI tmDbAPI;

    public RecyclerView rvCast, rvRecommendContents;
    public RecyclerView.Adapter castAdapter, recommendAdapter;
    public RecyclerView.LayoutManager castLayoutManager, recommendLayoutManager;
    public List<Cast> castDataList;
    public List<Results> recommendDataList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_movie_detail);

        ivVerticalPoster = findViewById(R.id.ivVerticalPoster);
        ivHorizontalPoster = findViewById(R.id.ivHorizontalPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvGenres = findViewById(R.id.tvGenres);
        tvPopularity = findViewById(R.id.tvPopularity);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        etvOverview = findViewById(R.id.etvOverview);
        btnToggle = findViewById(R.id.btnToggle);

        castDataList = new ArrayList<>();
        castAdapter = new MovieCastAdapter(castDataList, this);
        castLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvCast = findViewById(R.id.rvCast);
        rvCast.setHasFixedSize(true);
        rvCast.setLayoutManager(castLayoutManager);
        rvCast.setAdapter(castAdapter);

        recommendDataList = new ArrayList<>();
        recommendAdapter = new MovieAdapter(recommendDataList, this);
        recommendLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvRecommendContents = findViewById(R.id.rvRecommendContents);
        rvRecommendContents.setHasFixedSize(true);
        rvRecommendContents.setLayoutManager(recommendLayoutManager);
        rvRecommendContents.setAdapter(recommendAdapter);

        etvOverview.setAnimationDuration(750L);
        etvOverview.setInterpolator(new OvershootInterpolator());
        etvOverview.setExpandInterpolator(new OvershootInterpolator());
        etvOverview.setCollapseInterpolator(new OvershootInterpolator());

        btnToggle.setOnClickListener(v -> {
            btnToggle.setBackgroundResource(etvOverview.isExpanded() ? R.drawable.ic_expand_more : R.drawable.ic_expand_less);
            etvOverview.toggle();
        });

        btnToggle.setOnClickListener(v -> {
            if (etvOverview.isExpanded()) {
                etvOverview.collapse();
                btnToggle.setBackgroundResource(R.drawable.ic_expand_more);

            } else {
                etvOverview.expand();
                btnToggle.setBackgroundResource(R.drawable.ic_expand_less);
            }
        });

        etvOverview.setText(getIntent().getStringExtra("overview"));
        title = getIntent().getStringExtra("title");
        id = getIntent().getIntExtra("id", 0);
        tvTitle.setText(title);
        tvPopularity.setText("Popularity : " + getIntent().getDoubleExtra("popularity", 0));
        tvReleaseDate.setText("Release Date : " + getIntent().getStringExtra("release_date"));

        Picasso.get().load(IMAGE_BASE_URL_1280 + getIntent().getStringExtra("backdrop")).into(ivHorizontalPoster);
        Picasso.get().load(IMAGE_BASE_URL_500 + getIntent().getStringExtra("poster")).into(ivVerticalPoster);

        List<Genres> labelPS = (List<Genres>) getIntent().getSerializableExtra("genres");

        if (labelPS != null) {
            String genres = "";
            for (int i = 0; i < labelPS.size(); i++) {
                if (labelPS.get(i) == null) continue;
                if (i == labelPS.size() - 1) {
                    genres = genres.concat(labelPS.get(i).getName());
                } else {
                    genres = genres.concat(labelPS.get(i).getName() + " | ");
                }
            }
            tvGenres.setText(genres);
        } else if (labelPS.size() == 0) {
            tvGenres.setText("");
        }
        getCastInfo();
        getRecommendMovie();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getCastInfo() {
        tmDbAPI.getCreditDetail(id, TMDb_API_KEY).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
            castDataList.addAll(response.getCast());
            castAdapter.notifyDataSetChanged();

        }, e -> Timber.e(e, "Error fetching now popular movies: %s", e.getMessage()));
    }


    @SuppressLint("NotifyDataSetChanged")
    public void getRecommendMovie() {
        tmDbAPI.getRecommendDetail(id, TMDb_API_KEY).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
            recommendDataList.addAll(response.getResults());
            recommendAdapter.notifyDataSetChanged();

        }, e -> Timber.e(e, "Error fetching now popular movies: %s", e.getMessage()));
    }
}