package com.halil.ozel.moviedb.data.models;

import java.util.List;

public class ResponseTvSeries {
    private Integer page;
    private Integer total_pages;
    private List<TvResults> results;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(Integer total_pages) {
        this.total_pages = total_pages;
    }

    public List<TvResults> getResults() {
        return results;
    }

    public void setResults(List<TvResults> results) {
        this.results = results;
    }
}
