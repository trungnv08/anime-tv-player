package com.example.anime.player.data.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class MovieChapter implements Serializable {
    String id;
    String title;
    String chapterUrl;
    List<MovieResource> resources = new LinkedList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MovieResource> getResources() {
        return resources;
    }

    public void setResources(List<MovieResource> resources) {
        this.resources = resources;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MovieChapter.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("title='" + title + "'")
                .add("chapterUrl='" + chapterUrl + "'")
                .add("resources=" + resources)
                .toString();
    }
}
