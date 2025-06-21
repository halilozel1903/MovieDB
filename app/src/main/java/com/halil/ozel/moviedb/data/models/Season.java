package com.halil.ozel.moviedb.data.models;

import java.io.Serializable;

public class Season implements Serializable {
    private Integer season_number;
    private String name;
    private Integer episode_count;

    public Integer getSeason_number() {
        return season_number;
    }

    public void setSeason_number(Integer season_number) {
        this.season_number = season_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEpisode_count() {
        return episode_count;
    }

    public void setEpisode_count(Integer episode_count) {
        this.episode_count = episode_count;
    }
}
