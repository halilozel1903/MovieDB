package com.halil.ozel.moviedb.ui.home.adapters;

import android.content.Context;
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
import com.halil.ozel.moviedb.data.models.TvResults;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_500;

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
        Picasso.get().load(IMAGE_BASE_URL_500 + tv.getPoster_path()).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return tvList.size();
    }

    static class TvSeriesHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView poster;
        TvSeriesHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvPopularMovieTitle);
            poster = itemView.findViewById(R.id.ivPopularPoster);
        }
    }
}
