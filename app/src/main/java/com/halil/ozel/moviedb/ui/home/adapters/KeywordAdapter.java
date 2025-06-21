package com.halil.ozel.moviedb.ui.home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.models.Keyword;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordHolder> {

    private final List<Keyword> keywords;

    public KeywordAdapter(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    @NonNull
    @Override
    public KeywordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_keyword, parent, false);
        return new KeywordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordHolder holder, int position) {
        holder.title.setText(keywords.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    static class KeywordHolder extends RecyclerView.ViewHolder {
        TextView title;
        KeywordHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvKeyword);
        }
    }
}
