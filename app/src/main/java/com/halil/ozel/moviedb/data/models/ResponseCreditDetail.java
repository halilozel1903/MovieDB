package com.halil.ozel.moviedb.data.models;

import java.util.List;

public class ResponseCreditDetail {

    private List<Cast> cast;

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }
}
