package com.halil.ozel.moviedb.ui.detail.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.ui.detail.MovieDetailActivity;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_500;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

public class RecommendMovieAdapter extends RecyclerView.Adapter<RecommendMovieAdapter.RecommendMovieHolder> {


    private List<Results> popularMovieList;
    private Context context;


    @Inject
    TMDbAPI tmDbAPI;

    public RecommendMovieAdapter(List<Results> popularMovieList, Context context) {
        this.popularMovieList = popularMovieList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecommendMovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        App.instance().appComponent().inject(this);
        return new RecommendMovieHolder(LayoutInflater.from(context).inflate(R.layout.row_recommend_movie, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final RecommendMovieHolder holder, final int position) {


        Results results = popularMovieList.get(position);

        holder.tvRecommendMovieTitle.setText(results.getTitle());

        Picasso.get().load(IMAGE_BASE_URL_500 + results.getPoster_path()).into(holder.ivRecommendMoviePoster);


        holder.itemView.setOnClickListener(view -> tmDbAPI.getMovieDetail(results.getId(), TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {

                    Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
                    intent.putExtra("id", results.getId());
                    intent.putExtra("title", results.getTitle());
                    intent.putExtra("backdrop", results.getBackdrop_path());
                    intent.putExtra("poster", results.getPoster_path());
                    intent.putExtra("overview", results.getOverview());
                    intent.putExtra("popularity", results.getPopularity());
                    intent.putExtra("release_date", results.getRelease_date());
                    intent.putExtra("genres", (Serializable) response.getGenres());
                    view.getContext().startActivity(intent);

                }, e -> Timber.e(e, "Error fetching now popular movies: %s", e.getMessage())));


    }


    @Override
    public int getItemCount() {

        return popularMovieList.size();

    }


    public static class RecommendMovieHolder extends RecyclerView.ViewHolder {

        private final TextView tvRecommendMovieTitle;
        private final ImageView ivRecommendMoviePoster;

        public RecommendMovieHolder(View itemView) {
            super(itemView);
            tvRecommendMovieTitle = itemView.findViewById(R.id.tvRecommendMovieTitle);
            ivRecommendMoviePoster = itemView.findViewById(R.id.ivRecommendMoviePoster);
        }

    }

}
