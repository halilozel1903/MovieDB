package com.halil.ozel.moviedb.data.models;

import java.util.List;

public class ResponsePersonMovieCredits {
    private List<Results> cast;

    public List<Results> getCast() {
        return cast;
    }

    public void setCast(List<Results> cast) {
        this.cast = cast;
    }
}
