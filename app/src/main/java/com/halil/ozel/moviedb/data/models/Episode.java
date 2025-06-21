package com.halil.ozel.moviedb.data.models;

import java.io.Serializable;

public class Episode implements Serializable {
    private Integer episode_number;
    private String name;

    public Integer getEpisode_number() {
        return episode_number;
    }

    public void setEpisode_number(Integer episode_number) {
        this.episode_number = episode_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
