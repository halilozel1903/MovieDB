package com.halil.ozel.moviedb.ui.detail;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_1280;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_500;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Cast;
import com.halil.ozel.moviedb.data.models.Genres;
import com.halil.ozel.moviedb.data.models.TvResults;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.data.models.Season;
import com.halil.ozel.moviedb.data.models.Episode;
import com.halil.ozel.moviedb.data.models.ResponseSeasonDetail;
import com.halil.ozel.moviedb.data.FavoritesManager;
import com.halil.ozel.moviedb.ui.detail.adapters.MovieCastAdapter;
import com.halil.ozel.moviedb.ui.home.adapters.TvSeriesAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.blogc.android.views.ExpandableTextView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class TvSeriesDetailActivity extends AppCompatActivity {

    String title;
    int id;
    ImageView ivHorizontalPoster, ivVerticalPoster;
    TextView tvTitle, tvGenres, tvPopularity, tvReleaseDate, tvSeasons, tvEpisodes, tvRelated;
    android.widget.AutoCompleteTextView spSeason, spEpisode;
    ExpandableTextView etvOverview;
    Button btnToggle;
    ImageButton fabFavorite;
    MaterialToolbar detailToolbar;

    @Inject
    TMDbAPI tmDbAPI;

    public RecyclerView rvCast, rvRecommendContents;
    public RecyclerView.Adapter castAdapter, recommendAdapter;
    public RecyclerView.LayoutManager castLayoutManager, recommendLayoutManager;
    public List<Cast> castDataList;
    public List<TvResults> recommendDataList;
    public List<Season> seasonList;
    public List<Episode> episodeList;
    private int currentSeasonNumber;
    private ResponseSeasonDetail currentSeasonDetail;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_tv_series_detail);

        ivVerticalPoster = findViewById(R.id.ivVerticalPoster);
        ivHorizontalPoster = findViewById(R.id.ivHorizontalPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvGenres = findViewById(R.id.tvGenres);
        tvPopularity = findViewById(R.id.tvPopularity);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvSeasons = findViewById(R.id.tvSeasons);
        tvEpisodes = findViewById(R.id.tvEpisodes);
        spSeason = findViewById(R.id.spSeason);
        spEpisode = findViewById(R.id.spEpisode);
        tvRelated = findViewById(R.id.tvRelated);
        etvOverview = findViewById(R.id.etvOverview);
        btnToggle = findViewById(R.id.btnToggle);
        fabFavorite = findViewById(R.id.fabFavorite);
        detailToolbar = findViewById(R.id.detailToolbar);
        detailToolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        detailToolbar.setNavigationOnClickListener(v -> onBackPressed());
        detailToolbar.setTitle("");

        castDataList = new ArrayList<>();
        castAdapter = new MovieCastAdapter(castDataList, this);
        castLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvCast = findViewById(R.id.rvCast);
        rvCast.setHasFixedSize(true);
        rvCast.setLayoutManager(castLayoutManager);
        rvCast.setAdapter(castAdapter);

        recommendDataList = new ArrayList<>();
        recommendAdapter = new TvSeriesAdapter(recommendDataList, this);
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
            if (etvOverview.isExpanded()) {
                etvOverview.collapse();
                btnToggle.setBackgroundResource(R.drawable.ic_expand_more);
            } else {
                etvOverview.expand();
                btnToggle.setBackgroundResource(R.drawable.ic_expand_less);
            }
        });

        title = getIntent().getStringExtra("title");
        id = getIntent().getIntExtra("id", 0);
        tvTitle.setText(title);
        etvOverview.setText(getIntent().getStringExtra("overview"));
        tvPopularity.setText(getString(R.string.popularity_format,
                getIntent().getDoubleExtra("popularity", 0)));
        tvReleaseDate.setText(getString(R.string.first_air_date_format,
                getIntent().getStringExtra("release_date")));
        int seasons = getIntent().getIntExtra("seasons", 0);
        int episodes = getIntent().getIntExtra("episodes", 0);
        tvSeasons.setText(getString(R.string.seasons_format, seasons));
        tvEpisodes.setText(getString(R.string.episodes_format, episodes));
        seasonList = (List<Season>) getIntent().getSerializableExtra("season_list");

        Picasso.get().load(IMAGE_BASE_URL_1280 + getIntent().getStringExtra("backdrop")).into(ivHorizontalPoster);
        Picasso.get().load(IMAGE_BASE_URL_500 + getIntent().getStringExtra("poster")).into(ivVerticalPoster);

        List<Genres> labelPS = (List<Genres>) getIntent().getSerializableExtra("genres");
        if (labelPS != null && !labelPS.isEmpty()) {
            StringBuilder genres = new StringBuilder();
            for (int i = 0; i < labelPS.size(); i++) {
                if (labelPS.get(i) == null) continue;
                if (i == labelPS.size() - 1) {
                    genres.append(labelPS.get(i).getName());
                } else {
                    genres.append(labelPS.get(i).getName()).append(" | ");
                }
            }
            tvGenres.setText("\uD83C\uDFAD " + genres.toString());
        } else {
            tvGenres.setText("");
        }

        if (seasonList != null && !seasonList.isEmpty()) {
            List<String> sNames = new ArrayList<>();
            for (Season s : seasonList) {
                sNames.add(s.getName());
            }
            android.widget.ArrayAdapter<String> sAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sNames);
            spSeason.setAdapter(sAdapter);
            spSeason.setOnItemClickListener((parent, view1, position, id1) -> {
                Season season = seasonList.get(position);
                loadEpisodes(season.getSeason_number(), true);
            });
            loadEpisodes(seasonList.get(0).getSeason_number(), false);
        }

        getCastInfo();
        getRecommendTv();

        updateFab();
        fabFavorite.setOnClickListener(v -> {
            if (FavoritesManager.isFavorite(this, id)) {
                FavoritesManager.remove(this, id);
                fabFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                Toast.makeText(this, R.string.favorite_removed, Toast.LENGTH_SHORT).show();
            } else {
                TvResults r = new TvResults();
                r.setId(id);
                r.setName(title);
                r.setPoster_path(getIntent().getStringExtra("poster"));
                FavoritesManager.add(this, convert(r));
                fabFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                Toast.makeText(this, R.string.favorite_added, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Results convert(TvResults tv) {
        Results r = new Results();
        r.setId(tv.getId());
        r.setTitle(tv.getName());
        r.setPoster_path(tv.getPoster_path());
        return r;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getCastInfo() {
        tmDbAPI.getTvCastDetail(id, TMDb_API_KEY).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
            castDataList.addAll(response.getCast());
            castAdapter.notifyDataSetChanged();

        }, e -> Timber.e(e, "Error fetching tv cast: %s", e.getMessage()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getRecommendTv() {
        tmDbAPI.getTvRecommendations(id, TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    recommendDataList.addAll(response.getResults());
                    recommendAdapter.notifyDataSetChanged();
                    if (recommendDataList.isEmpty()) {
                        tvRelated.setVisibility(View.GONE);
                    }

                }, e -> Timber.e(e, "Error fetching tv recommendations: %s", e.getMessage()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadEpisodes(int seasonNumber, boolean showDialog) {
        currentSeasonNumber = seasonNumber;
        tmDbAPI.getSeasonDetail(id, seasonNumber, TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    currentSeasonDetail = response;
                    episodeList = response.getEpisodes();
                    if (episodeList != null) {
                        List<String> eNames = new ArrayList<>();
                        for (Episode e : episodeList) {
                            eNames.add(e.getName());
                        }
                        android.widget.ArrayAdapter<String> eAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eNames);
                        spEpisode.setAdapter(eAdapter);
                        spEpisode.setOnItemClickListener((p, v, pos, id2) -> {
                            Episode ep = episodeList.get(pos);
                            loadEpisodeDetail(ep.getEpisode_number());
                        });
                    }
                    if (showDialog) {
                        showSeasonDetail(response);
                    }
                }, e -> Timber.e(e, "Error fetching season detail: %s", e.getMessage()));
    }

    private void loadEpisodeDetail(int episodeNumber) {
        tmDbAPI.getEpisodeDetail(id, currentSeasonNumber, episodeNumber, TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showEpisodeDetail,
                        e -> Timber.e(e, "Error fetching episode detail: %s", e.getMessage()));
    }

    private void showSeasonDetail(ResponseSeasonDetail detail) {
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_season_detail, null);
        ImageView poster = view.findViewById(R.id.ivSeasonPoster);
        TextView name = view.findViewById(R.id.tvSeasonName);
        TextView overview = view.findViewById(R.id.tvSeasonOverview);
        name.setText(detail.getName());
        overview.setText(detail.getOverview());
        if (detail.getPoster_path() != null) {
            Picasso.get().load(IMAGE_BASE_URL_500 + detail.getPoster_path()).into(poster);
        }
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showEpisodeDetail(Episode episode) {
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_episode_detail, null);
        ImageView poster = view.findViewById(R.id.ivEpisodeStill);
        TextView name = view.findViewById(R.id.tvEpisodeName);
        TextView overview = view.findViewById(R.id.tvEpisodeOverview);
        name.setText(episode.getName());
        overview.setText(episode.getOverview());
        if (episode.getStill_path() != null) {
            Picasso.get().load(IMAGE_BASE_URL_500 + episode.getStill_path()).into(poster);
        }
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void updateFab() {
        if (FavoritesManager.isFavorite(this, id)) {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }
}
