package com.halil.ozel.moviedb.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.ui.detail.MovieDetailActivity;
import com.halil.ozel.moviedb.data.FavoritesManager;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_500;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PopularMovieHolder> {

    private final List<Results> popularMovieList;
    private final Context context;
    private OnFavoriteChangeListener favoriteChangeListener;

    @Inject
    TMDbAPI tmDbAPI;

    public MovieAdapter(List<Results> popularMovieList, Context context) {
        this.popularMovieList = popularMovieList;
        this.context = context;
    }

    public interface OnFavoriteChangeListener {
        void onChange();
    }

    public void setOnFavoriteChangeListener(OnFavoriteChangeListener listener) {
        this.favoriteChangeListener = listener;
    }

    @NonNull
    @Override
    public PopularMovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        App.instance().appComponent().inject(this);
        return new PopularMovieHolder(LayoutInflater.from(context).inflate(R.layout.row_nowplaying_movie, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final PopularMovieHolder holder, final int position) {
        Results results = popularMovieList.get(position);
        holder.tvPopularMovieTitle.setText(results.getTitle());
        if (results.getPoster_path() != null) {
            Picasso.get()
                    .load(IMAGE_BASE_URL_500 + results.getPoster_path())
                    .into(holder.ivPopularPoster);
        } else {
            Picasso.get()
                    .load("https://www.salonlfc.com/wp-content/uploads/2018/01/image-not-found-scaled-1150x647.png")
                    .into(holder.ivPopularPoster);
        }

        boolean fav = FavoritesManager.isFavorite(context, results.getId());
        holder.btnFavorite.setImageResource(fav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        holder.btnFavorite.setOnClickListener(v -> {
            if (FavoritesManager.isFavorite(context, results.getId())) {
                FavoritesManager.remove(context, results.getId());
                holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                if (favoriteChangeListener != null) favoriteChangeListener.onChange();
            } else {
                FavoritesManager.add(context, results);
                holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                if (favoriteChangeListener != null) favoriteChangeListener.onChange();
            }
        });

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

    public static class PopularMovieHolder extends RecyclerView.ViewHolder {
        private final TextView tvPopularMovieTitle;
        private final ImageView ivPopularPoster;
        private final ImageButton btnFavorite;

        public PopularMovieHolder(View itemView) {
            super(itemView);
            tvPopularMovieTitle = itemView.findViewById(R.id.tvPopularMovieTitle);
            ivPopularPoster = itemView.findViewById(R.id.ivPopularPoster);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

        }
    }
}
