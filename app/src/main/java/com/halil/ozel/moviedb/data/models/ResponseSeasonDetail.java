package com.halil.ozel.moviedb.data.models;

import java.io.Serializable;
import java.util.List;

public class ResponseSeasonDetail implements Serializable {
    private List<Episode> episodes;

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
