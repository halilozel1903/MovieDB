package com.halil.ozel.moviedb.data.models;

import java.util.List;

public class ResponseTvSeries {
    private List<TvResults> results;

    public List<TvResults> getResults() {
        return results;
    }

    public void setResults(List<TvResults> results) {
        this.results = results;
    }
}
