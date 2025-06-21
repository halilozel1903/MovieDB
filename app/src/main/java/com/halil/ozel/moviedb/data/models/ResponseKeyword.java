package com.halil.ozel.moviedb.data.models;

import java.util.List;

public class ResponseKeyword {
    private List<Keyword> results;

    public List<Keyword> getResults() { return results; }
    public void setResults(List<Keyword> results) { this.results = results; }
}
