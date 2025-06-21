package com.halil.ozel.moviedb.data.models;

import java.util.List;

import com.halil.ozel.moviedb.data.models.TvResults;

/**
 * Response model for person TV credits endpoint.
 */

public class ResponsePersonTvCredits {
    private List<TvResults> cast;

    public List<TvResults> getCast() {
        return cast;
    }

    public void setCast(List<TvResults> cast) {
        this.cast = cast;
    }
}
