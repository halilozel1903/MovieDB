package com.halil.ozel.moviedb.ui.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.TvResults;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.data.models.ResponseTvSeriesDetail;
import com.halil.ozel.moviedb.data.FavoritesManager;
import com.halil.ozel.moviedb.ui.detail.TvSeriesDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_500;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class TvSeriesAdapter extends RecyclerView.Adapter<TvSeriesAdapter.TvSeriesHolder> {

    private final List<TvResults> tvList;
    private final Context context;

    @Inject
    TMDbAPI tmDbAPI;

    public TvSeriesAdapter(List<TvResults> tvList, Context context) {
        this.tvList = tvList;
        this.context = context;
    }

    @NonNull
    @Override
    public TvSeriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        App.instance().appComponent().inject(this);
        return new TvSeriesHolder(LayoutInflater.from(context).inflate(R.layout.row_nowplaying_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TvSeriesHolder holder, int position) {
        TvResults tv = tvList.get(position);
        holder.title.setText(tv.getName());
        if (tv.getPoster_path() != null) {
            Picasso.get()
                    .load(IMAGE_BASE_URL_500 + tv.getPoster_path())
                    .into(holder.poster);
        } else {
            Picasso.get()
                    .load("https://www.salonlfc.com/wp-content/uploads/2018/01/image-not-found-scaled-1150x647.png")
                    .into(holder.poster);
        }

        boolean fav = FavoritesManager.isFavorite(context, tv.getId());
        holder.btnFavorite.setImageResource(fav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        holder.btnFavorite.setOnClickListener(v -> {
            if (FavoritesManager.isFavorite(context, tv.getId())) {
                FavoritesManager.remove(context, tv.getId());
                holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                FavoritesManager.add(context, convert(tv));
                holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            }
        });

        holder.itemView.setOnClickListener(view -> tmDbAPI.getTvSeriesDetail(tv.getId(), TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Intent intent = new Intent(view.getContext(), TvSeriesDetailActivity.class);
                    intent.putExtra("id", tv.getId());
                    intent.putExtra("title", tv.getName());
                    intent.putExtra("backdrop", "");
                    intent.putExtra("poster", tv.getPoster_path());
                    intent.putExtra("overview", response instanceof ResponseTvSeriesDetail ? ((ResponseTvSeriesDetail) response).getOverview() : "");
                    intent.putExtra("popularity", response.getPopularity());
                    intent.putExtra("release_date", response.getFirst_air_date());
                    intent.putExtra("genres", (java.io.Serializable) response.getGenres());
                    view.getContext().startActivity(intent);
                }, e -> Timber.e(e, "Error fetching tv detail: %s", e.getMessage())));
    }

    private Results convert(TvResults tv) {
        Results r = new Results();
        r.setId(tv.getId());
        r.setTitle(tv.getName());
        r.setPoster_path(tv.getPoster_path());
        return r;
    }

    @Override
    public int getItemCount() {
        return tvList.size();
    }

    static class TvSeriesHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView poster;
        ImageButton btnFavorite;
        TvSeriesHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvPopularMovieTitle);
            poster = itemView.findViewById(R.id.ivPopularPoster);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
