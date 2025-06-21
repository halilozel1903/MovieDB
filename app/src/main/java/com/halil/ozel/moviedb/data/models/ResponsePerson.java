package com.halil.ozel.moviedb.data.models;

import java.util.List;

public class ResponsePerson {
    private Integer page;
    private List<Cast> results;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Cast> getResults() {
        return results;
    }

    public void setResults(List<Cast> results) {
        this.results = results;
    }
}
