package com.halil.ozel.moviedb.ui.home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Keyword;
import com.halil.ozel.moviedb.ui.home.adapters.KeywordAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

public class SearchFragment extends Fragment {

    @Inject
    TMDbAPI tmDbAPI;

    private KeywordAdapter adapter;
    private final List<Keyword> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        App.instance().appComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText etQuery = view.findViewById(R.id.etQuery);
        Button btnSearch = view.findViewById(R.id.btnSearch);
        RecyclerView rvKeywords = view.findViewById(R.id.rvKeywords);
        rvKeywords.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new KeywordAdapter(data);
        rvKeywords.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            String query = etQuery.getText().toString().trim();
            if (query.isEmpty()) return;
            data.clear();
            tmDbAPI.searchKeyword(TMDb_API_KEY, query, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.getResults() != null) {
                            data.addAll(response.getResults());
                            adapter.notifyDataSetChanged();
                        }
                    }, e -> Timber.e(e, "Error searching keyword: %s", e.getMessage()));
        });

        return view;
    }
}
