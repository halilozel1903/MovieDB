package com.halil.ozel.moviedb.ui.home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.data.models.TvResults;
import com.halil.ozel.moviedb.data.models.Cast;
import com.halil.ozel.moviedb.ui.home.adapters.MovieAdapter;
import com.halil.ozel.moviedb.ui.home.adapters.TvSeriesAdapter;
import com.halil.ozel.moviedb.ui.detail.adapters.MovieCastAdapter;

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

    private MovieAdapter movieAdapter;
    private final List<Results> movieList = new ArrayList<>();
    private TvSeriesAdapter tvAdapter;
    private final List<TvResults> tvList = new ArrayList<>();
    private MovieCastAdapter personAdapter;
    private final List<Cast> personList = new ArrayList<>();
    private TextView tvMoviesTitle, tvTvTitle, tvPersonsTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        App.instance().appComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText etQuery = view.findViewById(R.id.etQuery);
        tvMoviesTitle = view.findViewById(R.id.tvMoviesTitle);
        tvTvTitle = view.findViewById(R.id.tvTvTitle);
        tvPersonsTitle = view.findViewById(R.id.tvPersonsTitle);

        tvMoviesTitle.setVisibility(View.GONE);
        tvTvTitle.setVisibility(View.GONE);
        tvPersonsTitle.setVisibility(View.GONE);

        etQuery.post(() -> {
            etQuery.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etQuery, InputMethodManager.SHOW_IMPLICIT);
        });

        RecyclerView rvMovies = view.findViewById(R.id.rvSearchMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        movieAdapter = new MovieAdapter(movieList, getContext());
        rvMovies.setAdapter(movieAdapter);

        RecyclerView rvTv = view.findViewById(R.id.rvSearchTv);
        rvTv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tvAdapter = new TvSeriesAdapter(tvList, getContext());
        rvTv.setAdapter(tvAdapter);

        RecyclerView rvPersons = view.findViewById(R.id.rvSearchPersons);
        rvPersons.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        personAdapter = new MovieCastAdapter(personList, getContext());
        rvPersons.setAdapter(personAdapter);

        etQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    movieList.clear();
                    tvList.clear();
                    personList.clear();
                    movieAdapter.notifyDataSetChanged();
                    tvAdapter.notifyDataSetChanged();
                    personAdapter.notifyDataSetChanged();
                    tvMoviesTitle.setVisibility(View.GONE);
                    tvTvTitle.setVisibility(View.GONE);
                    tvPersonsTitle.setVisibility(View.GONE);
                    return;
                }

                movieList.clear();
                tvList.clear();
                personList.clear();

                tmDbAPI.searchMovie(TMDb_API_KEY, query, 1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.getResults() != null) {
                                movieList.addAll(response.getResults());
                                movieAdapter.notifyDataSetChanged();
                                tvMoviesTitle.setVisibility(movieList.isEmpty() ? View.GONE : View.VISIBLE);
                            } else {
                                tvMoviesTitle.setVisibility(View.GONE);
                            }
                        }, e -> Timber.e(e, "Error searching movie: %s", e.getMessage()));

                tmDbAPI.searchTv(TMDb_API_KEY, query, 1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.getResults() != null) {
                                tvList.addAll(response.getResults());
                                tvAdapter.notifyDataSetChanged();
                                tvTvTitle.setVisibility(tvList.isEmpty() ? View.GONE : View.VISIBLE);
                            } else {
                                tvTvTitle.setVisibility(View.GONE);
                            }
                        }, e -> Timber.e(e, "Error searching tv: %s", e.getMessage()));

                tmDbAPI.searchPerson(TMDb_API_KEY, query, 1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.getResults() != null) {
                                personList.addAll(response.getResults());
                                personAdapter.notifyDataSetChanged();
                                tvPersonsTitle.setVisibility(personList.isEmpty() ? View.GONE : View.VISIBLE);
                            } else {
                                tvPersonsTitle.setVisibility(View.GONE);
                            }
                        }, e -> Timber.e(e, "Error searching person: %s", e.getMessage()));
            }
        });

        return view;
    }
}
